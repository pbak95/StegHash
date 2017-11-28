package pl.pb.jsonMappings;

import pl.pb.downloadContentContext.MessageType;

import java.io.Serializable;

/**
 * Created by Patryk on 11/20/2017.
 */
public class UserMessagesRequest implements Serializable {

    private String username;

    private int pageNumber;

    private MessageType messageType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
