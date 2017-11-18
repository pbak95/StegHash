package pl.pb.rest;

import org.springframework.beans.factory.annotation.Autowired;
import pl.pb.config.StegHashModelConfig;
import pl.pb.config.StegHashWebappApplicationConfig;
import pl.pb.database_access.MessageReader;
import pl.pb.database_access.UserRepository;
import pl.pb.downloadContentContext.DownloadedItem;
import pl.pb.exceptions.FlickrException;
import pl.pb.exceptions.TwitterException;
import pl.pb.jsonMappings.ReceivedMessage;
import pl.pb.jsonMappings.ReceivedMessagesResponse;
import pl.pb.jsonMappings.ResponseFromStegHash;
import pl.pb.model.*;
import pl.pb.steganography.LSB.HiddenData;
import pl.pb.steganography.LSB.LSBMethod;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;


/**
 * Created by Patryk on 11/12/2017.
 */
public class StegReaderResourceImpl implements StegReaderResource {


    @Autowired
    private StegHashModelConfig stegHashModelConfig;

    @Autowired
    private StegHashWebappApplicationConfig stegHashWebappApplicationConfig;

    @Autowired
    UserRepository userRepository;

    private static int NUMBER_OF_MESSAGES_PER_PAGE = 5; //TO_DO

    @Override
    public Response getUserMessages(String username, int pageNumber) {
        ReceivedMessagesResponse receivedMessagesResponse = new ReceivedMessagesResponse();
        MessageReader messageReader = stegHashModelConfig.messageReader();
        List<Message> userMessages = messageReader.getUserMessages(username);
        List<ReceivedMessage> receivedMessages = new LinkedList<>();
        try {
            for (Message userMessage : userMessages) {
                receivedMessages.add(getMessageFromOSNs(userMessage, username));
            }
            receivedMessagesResponse.setReceivedMessages(receivedMessages);
        } catch (Exception e) {
            ResponseFromStegHash response = new ResponseFromStegHash();

            if (e instanceof FlickrException) {
                response.setStatus(e.getMessage());
            } else if (e instanceof TwitterException) {
                response.setStatus(e.getMessage());
            } else {
                response.setStatus("Message broken or no messages. Please contact with administrators.");
            }
            e.printStackTrace();
            return  Response.serverError().entity(response).build();
        }

        return Response.ok(receivedMessagesResponse, MediaType.APPLICATION_JSON).build();
    }

    private ReceivedMessage getMessageFromOSNs(Message message,
                                               String username) throws Exception {
        User user = userRepository.findByUsername(username).get(0);
        ReceivedMessage receivedMessage = new ReceivedMessage();
        receivedMessage.setDate(message.getMessageDate());
        receivedMessage.setUserFrom(message.getUserFrom().getUsername());

        List<OSNMapping> osnMappings = new LinkedList<>(message.getMappings());
        Collections.sort(osnMappings, OSNMapping.MAPPING_COMPARATOR);
        List<HashtagPermutation> hashtagPermutations =  new LinkedList<>(message.getHashtagPermutations());
        Collections.sort(hashtagPermutations, HashtagPermutation.HASHTAG_PERMUTATION_COMPARATOR);

        OSNAPI nextOsn = null;
        int nextPermutationNumber = 0;
        StringBuffer hiddenMessage = new StringBuffer("");

        for (int i = 0; i < osnMappings.size(); i++) {
            if (i == 0) {
                OSNAPI osnapi = getFirstOriginAPI(hashtagPermutations, osnMappings);
                HashtagPermutation hashtagPermutation = getHashtagPermutationByNumber(hashtagPermutations, i);
                DownloadedItem downloadedItem = getCorrectDownloadedItem(hashtagPermutation.getHashtagPermuation(),
                        osnapi, user);

                HiddenData hiddenData = LSBMethod.getHiddenData(downloadedItem.getBufferedImage());
                hiddenMessage.append(hiddenData.getMessage());

                nextPermutationNumber = hiddenData.getPermutationNumber();
                nextOsn = getOSNAPIByLastHashtag(downloadedItem.getHashtags(), osnMappings);
            } else if (i == osnMappings.size() - 1) {
                HashtagPermutation hashtagPermutation = getHashtagPermutationByNumber(hashtagPermutations,
                        nextPermutationNumber);
                DownloadedItem downloadedItem = getCorrectDownloadedItem(hashtagPermutation.getHashtagPermuation(),
                        nextOsn, user);

                HiddenData hiddenData = LSBMethod.getHiddenData(downloadedItem.getBufferedImage());
                hiddenMessage.append(hiddenData.getMessage());
            } else {
                HashtagPermutation hashtagPermutation = getHashtagPermutationByNumber(hashtagPermutations,
                        nextPermutationNumber);
                DownloadedItem downloadedItem = getCorrectDownloadedItem(hashtagPermutation.getHashtagPermuation(),
                        nextOsn, user);

                HiddenData hiddenData = LSBMethod.getHiddenData(downloadedItem.getBufferedImage());
                hiddenMessage.append(hiddenData.getMessage());

                nextPermutationNumber = hiddenData.getPermutationNumber();
                nextOsn = getOSNAPIByLastHashtag(downloadedItem.getHashtags(), osnMappings);
            }
        }

        receivedMessage.setMessage(hiddenMessage.toString());

        return receivedMessage;
    }

    //last mapping hashtag of last permutation indicate to to first origin api
    private OSNAPI getFirstOriginAPI(List<HashtagPermutation> hashtagPermutations, List<OSNMapping> osnMappings) {
        HashtagPermutation hashtagPermutation = hashtagPermutations.get(hashtagPermutations.size() -1);
        List<String> hastags = Arrays.asList(hashtagPermutation.getHashtagPermuation().split(" "));
        return getOSNAPIByLastHashtag(hastags, osnMappings);
    }

    private OSNAPI getOSNAPIByLastHashtag(List<String> hashtagsFromOSN, List<OSNMapping> osnMappings) {
        String mappingHashtag = hashtagsFromOSN.get(hashtagsFromOSN.size() -1);
        //Check if '#' is present, some apis returns tags without '#'
        if (!mappingHashtag.startsWith("#")) {
            mappingHashtag = "#" + mappingHashtag;
        }
        OSNAPI api = null;
        for (OSNMapping mapping : osnMappings) {
            if (mapping.getHashtag().equals(mappingHashtag)) {
                api = mapping.getOsnApi();
            } else if (mapping.getHashtag().toLowerCase().equals(mappingHashtag)) {
                api = mapping.getOsnApi();
            }
        }
        return api;
    }

    private HashtagPermutation getHashtagPermutationByNumber(List<HashtagPermutation> permutations, int number) {
        HashtagPermutation permutation = null;
        for (HashtagPermutation perm : permutations) {
            if (perm.getPermutationNumber() == number) {
                permutation = perm;
                break;
            }
        }
        return permutation;
    }

    private DownloadedItem getCorrectDownloadedItem(String hashtagPermutationStr, OSNAPI osnapi,
                                                    User user) throws Exception {
        DownloadedItem correctItem = null;
        List<DownloadedItem> downloadedItems = getDownloadedItems(hashtagPermutationStr,
                osnapi, user);
        for (DownloadedItem item : downloadedItems) {
            try {
                LSBMethod.getHiddenData(item.getBufferedImage());
                correctItem = item;
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (correctItem == null) {
            throw new Exception("Message broken or no messages. Please contact with administrators.");
        }
        return correctItem;
    }

    private List<DownloadedItem> getDownloadedItems(String hashtagpermutationStr, OSNAPI osnapi,
                                         User user) throws FlickrException, TwitterException {
        List<DownloadedItem> downloadedItems = new ArrayList<>();
        switch (osnapi) {
            case FLICKR:
               FlickrAccount flickrAccount = (FlickrAccount) getRandomAccount(user.getFlickrAccountSet());
               downloadedItems = stegHashWebappApplicationConfig.flickrAPI()
                       .downloadImages(hashtagpermutationStr, flickrAccount.getConsumerKey(),
                               flickrAccount.getConsumerSecret(),
                               flickrAccount.getAccessToken(), flickrAccount.getAccessSecret());
                break;
            case TWITTER:
                TwitterAccount twitterAccount = (TwitterAccount) getRandomAccount(user.getTwitterAccountSet());
                downloadedItems = stegHashWebappApplicationConfig.twitterAPI()
                        .downloadImages(hashtagpermutationStr, twitterAccount.getConsumerKey(),
                                twitterAccount.getConsumerSecret(), twitterAccount.getAccessToken(),
                                twitterAccount.getAccessSecret());
                break;
        }

        return downloadedItems;
    }

    private OSNAccount getRandomAccount(Set<? extends OSNAccount> osnAccounts) {
        List<OSNAccount> accounts = new LinkedList<>();
        accounts.addAll(osnAccounts);
        return accounts.get((int) (Math.random() * accounts.size()));
    }

}
