package pl.pb.downloadContentContext;

import pl.pb.model.Message;
import pl.pb.model.User;

/**
 * Created by Patryk on 11/20/2017.
 */
public class RequestMessageObject {

    private Message message;

    private MessageType messageType;

    public RequestMessageObject(Message message, MessageType messageType) {
        this.message = message;
        this.messageType = messageType;
    }


    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
