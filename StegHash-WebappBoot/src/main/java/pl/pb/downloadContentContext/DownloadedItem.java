package pl.pb.downloadContentContext;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by Patryk on 11/12/2017.
 */
public class DownloadedItem {

    private BufferedImage bufferedImage;

    private List<String> hashtags;

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }
}
