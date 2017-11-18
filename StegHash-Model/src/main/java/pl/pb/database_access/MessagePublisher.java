package pl.pb.database_access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.config.StegHashModelConfig;
import pl.pb.model.*;

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
        List<OSNMapping> newOSNMappings = new ArrayList<>();

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

        enqueuedMessage.getOsnApiMappings().forEach((hashtag, api) -> {
            OSNMapping newOSNMapping = new OSNMapping();
            newOSNMapping.setHashtag(hashtag);
            newOSNMapping.setOsnApi(api);
            newOSNMapping.setMessage(newMessage);
            newOSNMappings.add(newOSNMapping);
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
        newOSNMappings.forEach(mapping -> osnMappingDAO.addOSNMapping(mapping));
        hashtagPermutationsList.forEach(permutation ->
                hashtagPermutationDAO.addHashtagPermutation(permutation));
    }

    private String getCurrentDate() {
        Date date = new Date();
        return dateFormat.format(date);
    }
}
