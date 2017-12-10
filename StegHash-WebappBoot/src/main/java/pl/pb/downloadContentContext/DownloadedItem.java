package pl.pb.downloadContentContext;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof DownloadedItem)) {
            return false;
        }
        DownloadedItem downloadedItem = (DownloadedItem) o;
        boolean hashtagEquality = true;
        for (int i = 0; i < this.hashtags.size(); i++) {
            try {
                if (!this.hashtags.get(i).equals(downloadedItem.getHashtags().get(i))) {
                    hashtagEquality = false;
                    break;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return hashtagEquality; //equals for BufferedImage doesn't work properly
    }
}
