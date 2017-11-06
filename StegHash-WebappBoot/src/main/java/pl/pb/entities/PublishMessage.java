package pl.pb.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Patryk on 11/6/2017.
 */
public class PublishMessage implements Serializable {

    private String from;

    private String to;

    private String message;

    private String[] hashtags;

    private ImageOption imageOption;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getHashtags() {
        return hashtags;
    }

    public void setHashtags(String[] hashtags) {
        this.hashtags = hashtags;
    }

    public ImageOption getImageOption() {
        return imageOption;
    }

    public void setImageOption(ImageOption imageOption) {
        this.imageOption = imageOption;
    }
}
