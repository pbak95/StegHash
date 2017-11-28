package pl.pb.OSNAPIs.dropbox;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.users.FullAccount;
import pl.pb.exceptions.DropboxException;
import pl.pb.utils.PropertiesUtility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Patryk on 10/19/2017.
 */
public class DropboxAPI {

    private static final String ACCESS_TOKEN = PropertiesUtility.getInstance().getProperty("dropboxAccessToken");

    private DbxClientV2 client;

    public DropboxAPI() {
        DbxRequestConfig config = new DbxRequestConfig("dropbox/steghash", "en_US");
        this.client = new DbxClientV2(config, ACCESS_TOKEN);
    }

    public String upload(BufferedImage image, String name, String format) throws DropboxException {
        String path = "/" + name.hashCode() + "." + format;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        FileMetadata metadata;
        try {
            ImageIO.write(image, format, os); //e.g. png
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            metadata = this.client.files().uploadBuilder(path)
                    .uploadAndFinish(is);
            System.out.println("[DROPBOX] Uploaded file: " + metadata.getPathLower());
            return metadata.getPathLower();
            //uncomment if twitter change their api behaviour 3===0
            //return this.client.sharing().createSharedLinkWithSettings(metadata.getPathDisplay()).getUrl();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DbxException e) {
            throw new DropboxException("[Dropbox] Authentication credentials expired, ensure that you have " +
                    "provide valid accessToken.");
        }
        return "";
    }

    public BufferedImage downloadByPath(String path) throws  DropboxException {
        BufferedImage image = null;
        try {
            DbxDownloader downloader = this.client.files().download(path);
            InputStream is = downloader.getInputStream();
            image = ImageIO.read(is);
            System.out.println("[DROPBOX] Downloaded file");
        } catch (DbxException dbxException) {
            new DropboxException("[Dropbox] Problem with downloading content from dopbox, contacnt with administrators");
        } catch (IOException ioException) {
            new DropboxException("[Dropbox] Problem with downloading content from dopbox, contacnt with administrators");
        }

        return image;
    }

    public BufferedImage downloadBySharableUrl(String url) throws  DropboxException {
        BufferedImage image = null;
        try {
            DbxDownloader<SharedLinkMetadata> dbxDownloader = client.sharing().getSharedLinkFile(url);
            String path = dbxDownloader.getResult().getPathLower();
            DbxDownloader downloader = this.client.files().download(path);
            InputStream is = downloader.getInputStream();
            image = ImageIO.read(is);
            System.out.println("[DROPBOX] Downloaded file");
        } catch (DbxException dbxException) {
            new DropboxException("[Dropbox] Problem with downloading content from dopbox, contacnt with administrators");
        } catch (IOException ioException) {
            new DropboxException("[Dropbox] Problem with downloading content from dopbox, contacnt with administrators");
        }

        return image;
    }


    public static void main(String args[]) throws DbxException {
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("dropbox/steghash", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        FullAccount account = client.users().getCurrentAccount();
        System.out.println(account.getName().getDisplayName());

        ListFolderResult result = client.files().listFolder("");
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                System.out.println(metadata.getPathLower());
            }

            if (!result.getHasMore()) {
                break;
            }

            result = client.files().listFolderContinue(result.getCursor());
        }

        // Upload "test.txt" to Dropbox
        String pathToImage = PropertiesUtility.getInstance().getProperty("steganograms") + "lion_steg.png";
        try {
            InputStream in = new FileInputStream(pathToImage);
            FileMetadata metadata = client.files().uploadBuilder("/lion_steg2.png")
                    .uploadAndFinish(in);
            System.out.println(metadata.getContentHash());
            System.out.println(metadata.getName());
            System.out.println(metadata.getPathDisplay());
//            DbxDownloader downloader = client.files().downloadBySharableUrl("/lion_steg.png");
//            InputStream is = downloader.getInputStream();
//            BufferedImage bufferedImage = ImageIO.read(is);
//            downloader.close();
//            System.out.println(LSBMethod.getHiddenData(bufferedImage));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
