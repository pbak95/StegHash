package pl.pb.jsonMappings;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Patryk on 11/12/2017.
 */
public class ReceivedMessagesResponse {

    private List<ReceivedMessage> receivedMessages;

    private int currentPageNumber;

    private int lastPageNumber;

    public ReceivedMessagesResponse() {
        this.receivedMessages = new LinkedList<>();
    }

    public List<ReceivedMessage> getReceivedMessages() {
        return receivedMessages;
    }

    public void addReceivedMessage(ReceivedMessage receivedMessage) {
        this.receivedMessages.add(receivedMessage);
    }

    public void setReceivedMessages(List<ReceivedMessage> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public int getLastPageNumber() {
        return lastPageNumber;
    }

    public void setLastPageNumber(int lastPageNumber) {
        this.lastPageNumber = lastPageNumber;
    }
}
