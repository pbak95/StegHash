package pl.pb.jsonMappings;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Patryk on 11/12/2017.
 */
public class MessagesAggregate {

    private List<SingleMessage> messages;

    private int currentPageNumber;

    private int lastPageNumber;

    public MessagesAggregate() {
        this.messages = new LinkedList<>();
    }

    public List<SingleMessage> getMessages() {
        return messages;
    }

    public void addMessage(SingleMessage singleMessage) {
        this.messages.add(singleMessage);
    }

    public void setMessages(List<SingleMessage> messages) {
        this.messages = messages;
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
