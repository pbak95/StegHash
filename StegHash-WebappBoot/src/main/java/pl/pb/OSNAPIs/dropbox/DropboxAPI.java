package pl.pb.OSNAPIs.dropbox;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import pl.pb.utils.PropertiesUtility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Patryk on 10/19/2017.
 */
public class DropboxAPI {

    private static final String ACCESS_TOKEN = PropertiesUtility.getInstance().getProperty("dropboxAccessToken");
    //TO_DO get folder from user info in db
    private static final String FOLDER = "/StegHash/";
    private DbxClientV2 client;

    public DropboxAPI() {
        DbxRequestConfig config = new DbxRequestConfig("dropbox/steghash", "en_US");
        this.client = new DbxClientV2(config, ACCESS_TOKEN);
    }

    public String upload(BufferedImage image, String name, String format) throws Exception {
        String path = "/" + name.hashCode();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, format, os); //e.g. png
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        FileMetadata metadata = this.client.files().uploadBuilder(path)
            .uploadAndFinish(is);
        return metadata.getPathDisplay();
    }

    public BufferedImage download(String path) throws Exception {
        DbxDownloader downloader = this.client.files().download(path);
        InputStream is = downloader.getInputStream();
        return ImageIO.read(is);
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
//            DbxDownloader downloader = client.files().download("/lion_steg.png");
//            InputStream is = downloader.getInputStream();
//            BufferedImage bufferedImage = ImageIO.read(is);
//            downloader.close();
//            System.out.println(LSBMethod.getMessage(bufferedImage));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
