package pl.pb.OSNAPIs.flickr;


import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import com.flickr4java.flickr.util.AuthStore;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import pl.pb.downloadContentContext.DownloadedItem;
import pl.pb.exceptions.FlickrException;
import pl.pb.utils.PropertiesUtility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Patryk on 10/23/2017.
 */
public class FlickrAPI {

    private Map<String, Token> accessTokenProcessing = new ConcurrentHashMap<>();

    public void publish(BufferedImage image, String description, String format,
                        String consumerToken, String consumerTokenSecret,
                        String accessToken, String accessTokenSecret) throws Exception {
        Flickr flickr = getFlickrInstance(consumerToken, consumerTokenSecret);
        Uploader uploader = flickr.getUploader();
        AuthInterface authInterface = flickr.getAuthInterface();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, format, os); //e.g. png
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        UploadMetaData metaData = new UploadMetaData();
        metaData.setPublicFlag(true);
        metaData.setFriendFlag(true);
        metaData.setFamilyFlag(true);
        metaData.setFilemimetype("image/" + format); //HARDCODED - change it later
        //metaData.setTitle("StegHash tests");
        String[] hashtags = description.split(" ");
        List<String> hashtagsList = Arrays.asList(hashtags);
        hashtagsList.forEach(s -> s.replace("#",""));
        metaData.setTags(Arrays.asList(hashtags));
        //TO_DO get this parameters for requested user
        Token tokenReused = new Token(accessToken, accessTokenSecret);
        Auth auth = authInterface.checkToken(tokenReused);
        RequestContext.getRequestContext().setAuth(auth);
        System.out.println("Authentication success");
        String photoId = uploader.upload(is,metaData);
        System.out.println("Uploaded photo id: " + photoId);
    }

    private Flickr getFlickrInstance(String consumerToken, String consumerTokenSecret) {
        return new Flickr(consumerToken, consumerTokenSecret, new REST());
    }

    public List<DownloadedItem> downloadImages(String hashtagPermutationStr, String consumerToken,
                                               String consumerTokenSecret, String accessToken,
                                               String accessTokenSecret, String userOwnerId) throws FlickrException {
        Flickr flickr = getFlickrInstance(consumerToken, consumerTokenSecret);
        PhotosInterface photosInterface = flickr.getPhotosInterface();
        List<DownloadedItem> downloadedItems = new ArrayList<>();
        AuthInterface authInterface = flickr.getAuthInterface();
        Token tokenReused = new Token(accessToken, accessTokenSecret);
        try {
            Auth auth = authInterface.checkToken(tokenReused);
            RequestContext.getRequestContext().setAuth(auth);
            System.out.println("Authentication success");

            SearchParameters searchParameters = new SearchParameters();
            searchParameters.setTags(getTagsArrayFromString(hashtagPermutationStr));
            searchParameters.setUserId(userOwnerId);
            PhotoList<Photo> photoList = photosInterface.search(searchParameters, 10, 1);
            PhotoList<Photo> originalPhotos = getOriginalPhotos(photoList, photosInterface,
                    tokenReused.getSecret());

            originalPhotos.forEach(photo -> {
                DownloadedItem downloadedItem = new DownloadedItem();
                List<String> hashtags = new LinkedList<>();
                photo.getTags().forEach(tag -> hashtags.add(tag.getValue()));
                downloadedItem.setHashtags(hashtags);
                try {
                    BufferedImage image = photosInterface.getImage(photo.getOriginalUrl());
                    downloadedItem.setBufferedImage(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                downloadedItems.add(downloadedItem);
            });
        } catch (com.flickr4java.flickr.FlickrException e) {
            throw new FlickrException("[Flickr] Authentication credentials error, ensure that you have " +
                    "provide valid Access Token/Access Token Secret In case there are valid, just refresh them :).");
        }

        return downloadedItems;
    }

    private PhotoList<Photo> getOriginalPhotos(PhotoList<Photo> photos, PhotosInterface photosInterface,
                                               String secret) throws com.flickr4java.flickr.FlickrException {
        PhotoList<Photo> originalPhotos = new PhotoList<>();
        for (Photo photo : photos) {
            Photo originalPhoto = photosInterface.getInfo(photo.getId(), secret);
            originalPhotos.add(originalPhoto);
        }
        return originalPhotos;
    }

    public String getAuthorizationUrl(String username, String apiKey, String apiSecret) {
        Flickr flickr = new Flickr(apiKey, apiSecret, new REST());
        AuthInterface authInterface = flickr.getAuthInterface();
        Token requestToken = authInterface.getRequestToken();
        accessTokenProcessing.put(username, requestToken);
        return  authInterface.getAuthorizationUrl(requestToken, Permission.WRITE);
    }

    private static String[] getTagsArrayFromString(String hashtagPermutationStr) {
        Pattern pattern = Pattern.compile("#(\\w+)");
        Matcher matcher = pattern.matcher(hashtagPermutationStr);
        List<String> tags = new ArrayList<>();
        while (matcher.find()) {
            tags.add(matcher.group(1));
        }
        return tags.stream().toArray(String[]::new);
    }

    public void setAccessToken(String username, String secret, String apiKey, String apiSecret) {
        Flickr flickr = new Flickr(apiKey, apiSecret, new REST());
        AuthInterface authInterface = flickr.getAuthInterface();
        Token requestToken = accessTokenProcessing.get(username);
        Token accessToken = authInterface.getAccessToken(requestToken, new Verifier(secret));
        String accessTokenSecretStr = accessToken.getSecret();
        String accessTokenStr = accessToken.getToken();
        //zapisujemy to do bazy per user
    }

    public static void main(String args[]) {

        String apiKey = "6521b1e17ec4e77f0133fc766d11234c";
        String sharedSecret = "8c3d778240a70ebc";
        Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
        InputStream in;
        String photoId = "";
        AuthStore store;

        String pathToImage = PropertiesUtility.getInstance().getProperty("steganograms") + "lion_steg.png";
        Uploader uploader = flickr.getUploader();
        UploadMetaData metaData = new UploadMetaData();
        metaData.setPublicFlag(true);
        metaData.setFriendFlag(true);
        metaData.setFamilyFlag(true);
        metaData.setFilemimetype("image/png"); //HARDCODED - change it later
        metaData.setTitle("StegHash tests");
        metaData.setTags(Arrays.asList("steghash", "tests","fun"));
//
//
        try {
            AuthInterface authInterface = flickr.getAuthInterface();

//            Scanner scanner = new Scanner(System.in);
//
//            Token requestToken = authInterface.getRequestToken();
//            System.out.println("token: " + requestToken);
//            String url = authInterface.getAuthorizationUrl(requestToken, Permission.WRITE);
//            System.out.println("Follow this URL to authorise yourself on Flickr");
//            System.out.println(url);
//            System.out.println("Paste in the token it gives you:");
//            System.out.print(">>");
//
//            String tokenKey = scanner.nextLine();
//            scanner.close();
//
//
//
//
//            Token accessToken = authInterface.getAccessToken(requestToken, new Verifier(tokenKey));
//
//            Token tokenReused = new Token(accessToken.getToken(), accessToken.getSecret());
//
//            Auth auth = authInterface.checkToken(accessToken);
//            RequestContext.getRequestContext().setAuth(auth);
//            System.out.println("Authentication success");
//
//            // This token can be used until the user revokes it.
//            System.out.println("Token: " + accessToken.getToken());
//            System.out.println("Secret: " + accessToken.getSecret());
//            System.out.println("nsid: " + auth.getUser().getId());
//            System.out.println("Realname: " + auth.getUser().getRealName());
//            System.out.println("Username: " + auth.getUser().getUsername());
//            System.out.println("Permission: " + auth.getPermission().getType());


            Token tokenReused = new Token("72157689739289005-32747b62351f4b66",
                    "7da3802635a9b4b3");



            Auth auth = authInterface.checkToken(tokenReused);
            RequestContext.getRequestContext().setAuth(auth);
            System.out.println("Authentication success");
//
//
//
//
            String secret  = tokenReused.getSecret();
            SearchParameters params = new SearchParameters();
            params.setText("#newApproach #forstegreader");
            PhotosInterface photosInterface = flickr.getPhotosInterface();
            PhotoList<Photo> photoList = photosInterface.search(params, 10, 1); //FlickrException thrown here
            photoList.forEach(photo -> {
                try {
                    Photo originalPhoto = photosInterface.getInfo(photo.getId(), secret);
                    System.out.println(originalPhoto.getOriginalUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
//
//            Auth auth = authInterface.checkToken(tokenReused);
//            RequestContext.getRequestContext().setAuth(auth);
//            System.out.println("Authentication success");
            in = new FileInputStream(pathToImage);
            photoId = uploader.upload(in,metaData);
            System.out.println("Uploaded photo id: " + photoId);

        } catch (Exception e) {
            e.printStackTrace();
        }





        //Collection results = testInterface.echo(Collections.EMPTY_MAP);
    }
}
