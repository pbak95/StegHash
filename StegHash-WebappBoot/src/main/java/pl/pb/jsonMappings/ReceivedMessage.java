package pl.pb.jsonMappings;

/**
 * Created by Patryk on 11/12/2017.
 */
public class ReceivedMessage {

    private String userFrom;

    private String date;

    private String message;

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
