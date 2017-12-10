package pl.pb.downloadContentContext;

import pl.pb.model.Message;
import pl.pb.model.User;

import java.util.UUID;

/**
 * Created by Patryk on 11/20/2017.
 */
public class RequestMessageObject {

    private Message message;

    private MessageType messageType;

    private UUID uuid;

    public RequestMessageObject(Message message, MessageType messageType, UUID uuid) {
        this.message = message;
        this.messageType = messageType;
        this.uuid = uuid;
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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
