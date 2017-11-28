package pl.pb.jsonMappings;

import pl.pb.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Patryk on 11/12/2017.
 */
public class SingleMessage {

    private String userFrom;

    private List<String> userTo;

    private String date;

    private String content;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getUserTo() {
        return userTo;
    }

    public void setUserTo(Set<User> usersTo) {
        List<String> usernames = new ArrayList<>();
        usersTo.forEach(usr -> usernames.add(usr.getUsername()));
        this.userTo = usernames;
    }
}
