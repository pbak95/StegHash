package pl.pb.database_access;

import org.springframework.beans.factory.annotation.Autowired;
import pl.pb.model.Message;
import pl.pb.model.User;

import java.util.*;

/**
 * Created by Patryk on 11/12/2017.
 */
public class MessageReader {


    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    public List<Message> getUserReceivedMessages(String username) {
        List<Message> userMessages = new LinkedList<>();

        User targetUser = userRepository.findByUsername(username).get(0);

        Set<Message> messages = targetUser.getUserMessagesReceived();

        userMessages.addAll(messages);

        Collections.sort(userMessages, Message.MESSAGE_COMPARATOR);

        return userMessages;
    }

    public List<Message> getUserSentMessages(String username) {
        User userFrom = userRepository.findByUsername(username).get(0);
        return messageRepository.findByUserFrom(userFrom);
    }
}
