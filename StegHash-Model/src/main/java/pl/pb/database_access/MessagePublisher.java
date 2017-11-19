package pl.pb.database_access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.config.StegHashModelConfig;
import pl.pb.model.*;
import pl.pb.model.modelHelperEntities.EnqueuedMessage;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Patryk on 11/11/2017.
 */

public class MessagePublisher {

    @Autowired
    StegHashModelConfig stegHashModelConfig;

    @Autowired
    UserRepository userRepository;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Transactional
    public void publishAllMessageElementsToDB(EnqueuedMessage enqueuedMessage) {
        UserDAO userDAO = stegHashModelConfig.userDAO();
        MessageDAO messageDAO = stegHashModelConfig.messageDAO();
        OSNMappingDAO osnMappingDAO = stegHashModelConfig.osnMappingDAO();
        HashtagPermutationDAO hashtagPermutationDAO = stegHashModelConfig.hashtagPermutationDAO();

        User userFrom = userRepository.findByUsername(enqueuedMessage.getUserFrom()).get(0);
        List<User> userTo = userRepository.findByUsername(enqueuedMessage.getUserTo());

        Message newMessage = new Message();

        List<HashtagPermutation> hashtagPermutationsList = new ArrayList<>();
        enqueuedMessage.getPermutations().forEach((number, permutations) -> {
            HashtagPermutation newHashtagPermutation = new HashtagPermutation();
            newHashtagPermutation.setMessage(newMessage);
            newHashtagPermutation.setPermutationNumber(number);
            String onelinePermutation = "";
            for (String perm: permutations) {
                onelinePermutation += perm + " ";
            }
            newHashtagPermutation.setHashtagPermuation(onelinePermutation);
            hashtagPermutationsList.add(newHashtagPermutation);
        });

        //add reference to message
        enqueuedMessage.getOsnApiMappings().forEach(mapping -> {
            mapping.setMessage(newMessage);
        });

        newMessage.setMessageDate(getCurrentDate());
        newMessage.setUsersTo(new HashSet<>(userRepository.findByUsername(enqueuedMessage.getUserTo())));
        newMessage.setUserFrom(userRepository.findByUsername(enqueuedMessage.getUserFrom()).get(0));
        newMessage.setHashtagPermutations(new HashSet<>(hashtagPermutationsList));

        userFrom.addUserMessageSent(newMessage);
        userTo.forEach(user -> user.addUserMessageReceived(newMessage));

        //Add do DB
        userDAO.updateUser(userFrom);
        userTo.forEach(userToUpdate -> userDAO.updateUser(userToUpdate));
        messageDAO.addMessage(newMessage);
        enqueuedMessage.getOsnApiMappings().forEach(mapping -> osnMappingDAO.addOSNMapping(mapping));
        hashtagPermutationsList.forEach(permutation ->
                hashtagPermutationDAO.addHashtagPermutation(permutation));
    }

    private String getCurrentDate() {
        Date date = new Date();
        return dateFormat.format(date);
    }
}
