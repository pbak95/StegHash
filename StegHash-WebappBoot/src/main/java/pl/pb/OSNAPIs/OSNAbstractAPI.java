package pl.pb.OSNAPIs;

import pl.pb.downloadContentContext.DownloadedItem;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by Patryk on 1/11/2018.
 */
public abstract class OSNAbstractAPI {

    public abstract void publish(BufferedImage image, String description, String format,
                        String consumerToken, String consumerTokenSecret,
                        String accessToken, String accessTokenSecret) throws Exception;

    public abstract List<DownloadedItem> downloadImages(String hashtagPermutationStr, String consumerToken,
                                               String consumerTokenSecret, String accessToken,
                                               String accessTokenSecret, String userOwnerId) throws Exception;
}
