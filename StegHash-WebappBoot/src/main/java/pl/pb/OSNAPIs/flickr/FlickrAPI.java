package pl.pb.OSNAPIs.flickr;


import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import com.flickr4java.flickr.util.AuthStore;
import org.scribe.model.Token;
import pl.pb.utils.PropertiesUtility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * Created by Patryk on 10/23/2017.
 */
public class FlickrAPI {

    private Flickr flickr;

    private static String API_KEY = PropertiesUtility.getInstance().getProperty("flickrApiKey");

    private static String SHARED_SECRED = PropertiesUtility.getInstance().getProperty("flickrSharedSecred");


    public FlickrAPI() {
        flickr = new Flickr(API_KEY, SHARED_SECRED, new REST());
    }


    public void publish(BufferedImage image, String description, String format,
                        String accessToken, String accessTokenSecret) throws Exception {
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

    public String getAuthorizationUrl(String username) {
        AuthInterface authInterface = flickr.getAuthInterface();

        Token token = authInterface.getRequestToken();
        //zapisujemy do bazy token i secret per user
        System.out.println("token: " + token);
        return  authInterface.getAuthorizationUrl(token, Permission.WRITE);
    }

    public void setAccessToken(String user, String tokenKey) {
        AuthInterface authInterface = flickr.getAuthInterface();
        //Tutaj bierzemy z bazy secret i token dla tego usera
//        Token recreatedToken = new Token(token, secret);
//        Token requestToken = authInterface.getAccessToken(recreatedToken, new Verifier(tokenKey));
//        String accessToken = requestToken.getToken();
//        String accessTokenSecret = requestToken.getSecret();
        //zapisujemy to do bazy per user
    }

    public static void main(String args[]) {

        String apiKey = "fc32193b415279c080cec0c72456d3f9";
        String sharedSecret = "aee652de5bf382a2";
        Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
        InputStream in;
        String photoId = "";
        AuthStore store;

        String pathToImage = PropertiesUtility.getInstance().getProperty("steganograms") + "lion_steg.png";
        String pathToImage2 = PropertiesUtility.getInstance().getProperty("steganograms") + "test.png";
        Uploader uploader = flickr.getUploader();
        UploadMetaData metaData = new UploadMetaData();
        metaData.setPublicFlag(true);
        metaData.setFriendFlag(true);
        metaData.setFamilyFlag(true);
        metaData.setFilemimetype("image/png"); //HARDCODED - change it later
        metaData.setTitle("StegHash tests");
        metaData.setTags(Arrays.asList("steghash", "tests","fun"));


        try {
//            SearchParameters params = new SearchParameters();
//            params.setText("test");
//            PhotosInterface photosInterface = flickr.getPhotosInterface();
//            PhotoList<Photo> photoList = photosInterface.search(params, 10, 1); //FlickrException thrown here

            AuthInterface authInterface = flickr.getAuthInterface();

//            Scanner scanner = new Scanner(System.in);
//
//            Token token = authInterface.getRequestToken();
//            System.out.println("token: " + token);
//            String url = authInterface.getAuthorizationUrl(token, Permission.WRITE);
//            System.out.println("Follow this URL to authorise yourself on Flickr");
//            System.out.println(url);
//            System.out.println("Paste in the token it gives you:");
//            System.out.print(">>");
//
//            String tokenKey = scanner.nextLine();
//            scanner.close();
//
//            System.out.println("Auth token: " + token.getToken());
//            System.out.println("Auth token secret: " + token.getSecret());
//
//
//            Token requestToken = authInterface.getAccessToken(token, new Verifier(tokenKey));

//            Token requestToken = authInterface.getAccessToken(new Token("72157688270599774-a4ca0d20b3402ff5",
//                    "4be2e2234a225a98"), new Verifier("484-360-917"));



//            Auth auth = authInterface.checkToken(requestToken);
//            RequestContext.getRequestContext().setAuth(auth);
//            System.out.println("Authentication success");
//
//
//            // This token can be used until the user revokes it.
//            System.out.println("Token: " + requestToken.getToken());
//            System.out.println("Secret: " + requestToken.getSecret());
//            System.out.println("nsid: " + auth.getUser().getId());
//            System.out.println("Realname: " + auth.getUser().getRealName());
//            System.out.println("Username: " + auth.getUser().getUsername());
//            System.out.println("Permission: " + auth.getPermission().getType());

            Token tokenReused = new Token("72157688314019393-cf16a913c8441f3d", "4e404854da2c5262");
            Auth auth = authInterface.checkToken(tokenReused);
            RequestContext.getRequestContext().setAuth(auth);
            System.out.println("Authentication success");
            in = new FileInputStream(pathToImage);
            photoId = uploader.upload(in,metaData);
            System.out.println("Uploaded photo id: " + photoId);
            in = new FileInputStream(pathToImage2);
            photoId = uploader.upload(in,metaData);
            System.out.println("Uploaded photo id: " + photoId);
        } catch (Exception e) {
            e.printStackTrace();
        }





        //Collection results = testInterface.echo(Collections.EMPTY_MAP);
    }
}
