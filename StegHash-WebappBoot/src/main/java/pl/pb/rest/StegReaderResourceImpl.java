package pl.pb.rest;

import org.springframework.beans.factory.annotation.Autowired;
import pl.pb.config.StegHashModelConfig;
import pl.pb.config.StegHashWebappApplicationConfig;
import pl.pb.database_access.MessageReader;
import pl.pb.database_access.UserRepository;
import pl.pb.downloadContentContext.DownloadedItem;
import pl.pb.downloadContentContext.MessageType;
import pl.pb.downloadContentContext.RequestMessageObject;
import pl.pb.exceptions.BrokenMessageException;
import pl.pb.exceptions.FlickrException;
import pl.pb.exceptions.TwitterException;
import pl.pb.jsonMappings.SingleMessage;
import pl.pb.jsonMappings.MessagesAggregate;
import pl.pb.jsonMappings.ResponseFromStegHash;
import pl.pb.jsonMappings.UserMessagesRequest;
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

    private static int NUMBER_OF_MESSAGES_PER_PAGE = 3; //TODO

    @Override
    public Response getUserMessages(UserMessagesRequest userMessagesRequest) {
        MessagesAggregate messagesResponse = new MessagesAggregate();
        List<Message> userMessages = getMessages(userMessagesRequest);
        List<SingleMessage> messages = new LinkedList<>();

        for (Message userMessage : userMessages) {
            try {
                messages.add(getMessageFromOSNs(userMessage, generateRequestMessageObject(
                        userMessagesRequest, userMessage)));

            } catch (Exception e) {
                ResponseFromStegHash response = new ResponseFromStegHash();

                if (e instanceof FlickrException) {
                    response.setStatus(e.getMessage());
                    e.printStackTrace();
                    return  Response.serverError().entity(response).build();
                } else if (e instanceof TwitterException) {
                    response.setStatus(e.getMessage());
                    e.printStackTrace();
                    return  Response.serverError().entity(response).build();
                }  else if (e instanceof BrokenMessageException) {
                    SingleMessage brokenMessage = new SingleMessage();
                    brokenMessage.setDate(userMessage.getMessageDate());
                    brokenMessage.setUserFrom(userMessage.getUserFrom().getUsername());
                    brokenMessage.setUserTo(userMessage.getUsersTo());
                    brokenMessage.setContent(e.getMessage());
                    messages.add(brokenMessage);
                } else {
                    response.setStatus("Internal server error, try again later or contact with site administrators.");
                    e.printStackTrace();
                    return  Response.serverError().entity(response).build();
                }
            }
        }

        messagesResponse.setMessages(messages);
        messagesResponse.setCurrentPageNumber(1);
        messagesResponse.setLastPageNumber(1);
        return Response.ok(messagesResponse, MediaType.APPLICATION_JSON).build();
    }

    private  List<Message> getMessages(UserMessagesRequest userMessagesRequest) {
        MessageReader messageReader = stegHashModelConfig.messageReader();
        if (userMessagesRequest.getMessageType().equals(MessageType.RECEIVED)) {
            return messageReader.getUserReceivedMessages(userMessagesRequest.getUsername());
        } else {
            return messageReader.getUserSentMessages(userMessagesRequest.getUsername());
        }
    }

    private RequestMessageObject generateRequestMessageObject(UserMessagesRequest userMessagesRequest,
                                                              Message message) {
        return new RequestMessageObject(message, userMessagesRequest.getMessageType());
    }


    private SingleMessage getMessageFromOSNs(Message message,
                                             RequestMessageObject requestMessageObject) throws Exception {

        SingleMessage receivedMessage = new SingleMessage();
        receivedMessage.setDate(message.getMessageDate());
        receivedMessage.setUserFrom(message.getUserFrom().getUsername());
        receivedMessage.setUserTo(message.getUsersTo());

        List<OSNMapping> osnMappings = new LinkedList<>(message.getMappings());
        Collections.sort(osnMappings, OSNMapping.MAPPING_COMPARATOR);
        List<HashtagPermutation> hashtagPermutations =  new LinkedList<>(message.getHashtagPermutations());
        Collections.sort(hashtagPermutations, HashtagPermutation.HASHTAG_PERMUTATION_COMPARATOR);

        List<OSNAPI> nextOsn = null;
        int nextPermutationNumber = 0;
        StringBuffer hiddenMessage = new StringBuffer("");

        for (int i = 0; i < osnMappings.size(); i++) {
            if (i == 0) {
                List<OSNAPI> osnapis = getFirstOriginAPI(hashtagPermutations, osnMappings);
                HashtagPermutation hashtagPermutation = getHashtagPermutationByNumber(hashtagPermutations, i);
                DownloadedItem downloadedItem = downloadedItemFromOSN(hashtagPermutation.getHashtagPermuation(),
                        osnapis, requestMessageObject, 0);

                HiddenData hiddenData = LSBMethod.getHiddenData(downloadedItem.getBufferedImage());
                hiddenMessage.append(hiddenData.getMessage());

                nextPermutationNumber = hiddenData.getPermutationNumber() + 1;
                nextOsn = getOSNAPIByLastHashtag(downloadedItem.getHashtags(), osnMappings);
            }  else {
                HashtagPermutation hashtagPermutation = getHashtagPermutationByNumber(hashtagPermutations,
                        nextPermutationNumber);
                DownloadedItem downloadedItem = downloadedItemFromOSN(hashtagPermutation.getHashtagPermuation(),
                        nextOsn, requestMessageObject, nextPermutationNumber);

                HiddenData hiddenData = LSBMethod.getHiddenData(downloadedItem.getBufferedImage());
                hiddenMessage.append(hiddenData.getMessage());
                if (i != osnMappings.size() - 1) {
                    nextPermutationNumber = hiddenData.getPermutationNumber() + 1;
                    nextOsn = getOSNAPIByLastHashtag(downloadedItem.getHashtags(), osnMappings);
                }
            }
        }

        receivedMessage.setContent(hiddenMessage.toString());

        return receivedMessage;
    }

    private DownloadedItem downloadedItemFromOSN(String hashtagPermutationStr, List<OSNAPI> osnapis,
                                                 RequestMessageObject requestMessageObject,
                                                 int expectedPermutationNumber) throws Exception {
        DownloadedItem downloadedItem = null;

        for (int i = 0; i < osnapis.size(); i++) {
            try {
                downloadedItem = getCorrectDownloadedItem(hashtagPermutationStr,
                        osnapis.get(i), requestMessageObject, expectedPermutationNumber);
                if (downloadedItem != null) {
                    return downloadedItem;
                }
            } catch (BrokenMessageException e) {
                if (i == osnapis.size() - 1) {
                    throw new BrokenMessageException(e.getMessage());
                }
            }
        }

        if (downloadedItem == null) {
            throw new BrokenMessageException("Message broken or no messages.");
        }

        return downloadedItem;
    }

    //last mapping hashtag of last permutation indicate to to first origin api
    private List<OSNAPI> getFirstOriginAPI(List<HashtagPermutation> hashtagPermutations, List<OSNMapping> osnMappings) {
        HashtagPermutation hashtagPermutation = hashtagPermutations.get(hashtagPermutations.size() -1);
        List<String> hastags = Arrays.asList(hashtagPermutation.getHashtagPermuation().split(" "));
        return getOSNAPIByLastHashtag(hastags, osnMappings);
    }

    private List<OSNAPI> getOSNAPIByLastHashtag(List<String> hashtagsFromOSN, List<OSNMapping> osnMappings) {
        String mappingHashtag = hashtagsFromOSN.get(hashtagsFromOSN.size() -1);
        List<OSNAPI> osnapis = new ArrayList<>();
        //Check if '#' is present, some apis returns tags without '#'
        if (!mappingHashtag.startsWith("#")) {
            mappingHashtag = "#" + mappingHashtag;
        }
        for (OSNMapping mapping : osnMappings) {
            if (mapping.getHashtag().equals(mappingHashtag)) {
                osnapis.add(mapping.getOsnApi());
            } else if (mapping.getHashtag().toLowerCase().equals(mappingHashtag)) {
                osnapis.add(mapping.getOsnApi());
            }
        }
        return osnapis;
    }

    private HashtagPermutation getHashtagPermutationByNumber(List<HashtagPermutation> permutations, int number) {
        HashtagPermutation permutation = null;
        for (HashtagPermutation perm : permutations) {
            if (perm.getPermutationNumber() == number) {
                permutation = perm;
                break;
            }
        }
        //get last if perm number to big
        if (permutation == null) {
            permutation = permutations.get(permutations.size() - 1);
        }

        return permutation;
    }

    private DownloadedItem getCorrectDownloadedItem(String hashtagPermutationStr, OSNAPI osnapi,
                                                    RequestMessageObject requestMessageObject,
                                                    int expectedPermutationNumber) throws Exception {
        DownloadedItem correctItem = null;
        List<DownloadedItem> downloadedItems = getDownloadedItems(hashtagPermutationStr,
                osnapi, requestMessageObject);
        for (DownloadedItem item : downloadedItems) {
            try {
                HiddenData hiddenData = LSBMethod.getHiddenData(item.getBufferedImage());
                if (hiddenData.getPermutationNumber() == expectedPermutationNumber) {
                    correctItem = item;
                    break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (correctItem == null) {
            if (downloadedItems.size() == 0) {
                throw new BrokenMessageException("Message not found, probably published content has not been indexed yet, " +
                        "try again later.");
            } else {
                throw new BrokenMessageException("Message broken or no messages.");
            }
        }
        return correctItem;
    }

    private List<DownloadedItem> getDownloadedItems(String hashtagpermutationStr, OSNAPI osnapi,
                                                    RequestMessageObject requestMessageObject) throws FlickrException, TwitterException {
        List<DownloadedItem> downloadedItems = new ArrayList<>();
        User accountProvider;
        User contentOwner = requestMessageObject.getMessage().getUserFrom();

        if (requestMessageObject.equals(MessageType.RECEIVED)) {
            accountProvider = new ArrayList<>(requestMessageObject.getMessage().getUsersTo()).get(0);
        } else {
            accountProvider = requestMessageObject.getMessage().getUserFrom();
        }


        switch (osnapi) {
            case FLICKR:
               FlickrAccount flickrAccount = (FlickrAccount) getRandomAccount(accountProvider.getFlickrAccountSet());
               List<String> ownerAccountsIds = getFlickrUsersAccountsIds(contentOwner);
                for (String userFromId : ownerAccountsIds) {
                    downloadedItems = stegHashWebappApplicationConfig.flickrAPI()
                                    .downloadImages(hashtagpermutationStr, flickrAccount.getConsumerKey(),
                                            flickrAccount.getConsumerSecret(), flickrAccount.getAccessToken(),
                                            flickrAccount.getAccessSecret(), userFromId);
                    /**
                     * break if find any items, because it will be inefficient
                     * searching for all possible user accounts -> in case when
                     * results will not satisfying, remove break and loop for all
                     */
                    if (downloadedItems.size() > 0) {
                        break;
                    }
                }
                break;
            case TWITTER:
                TwitterAccount twitterAccount = (TwitterAccount) getRandomAccount(accountProvider.getTwitterAccountSet());
                downloadedItems = stegHashWebappApplicationConfig.twitterAPI()
                        .downloadImages(hashtagpermutationStr, twitterAccount.getConsumerKey(),
                                twitterAccount.getConsumerSecret(), twitterAccount.getAccessToken(),
                                twitterAccount.getAccessSecret());
                break;
        }

        return downloadedItems;
    }

    private List<String> getFlickrUsersAccountsIds(User user) {
        List<String> usersAccountsIds = new ArrayList<>();
        List<FlickrAccount> userFlickrAccounts = new ArrayList<>(user.getFlickrAccountSet());
        userFlickrAccounts.forEach(acc -> usersAccountsIds.add(acc.getUserId()));
        return usersAccountsIds;
    }

    private OSNAccount getRandomAccount(Set<? extends OSNAccount> osnAccounts) {
        List<OSNAccount> accounts = new LinkedList<>();
        accounts.addAll(osnAccounts);
        return accounts.get((int) (Math.random() * accounts.size()));
    }

}
