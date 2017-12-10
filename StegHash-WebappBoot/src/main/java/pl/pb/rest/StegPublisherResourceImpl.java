package pl.pb.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pl.pb.config.StegHashModelConfig;
import pl.pb.database_access.MessagePublisher;
import pl.pb.exceptions.*;
import pl.pb.jsonMappings.*;
import pl.pb.model.User;
import pl.pb.publishContentContext.OSNIndicator;
import pl.pb.model.OSNAPI;
import pl.pb.OSNAPIs.OSNAPIUtility;
import pl.pb.config.StegHashWebappApplicationConfig;
import pl.pb.database_access.UserRepository;
import pl.pb.engine.HashTagChain;
import pl.pb.model.modelHelperEntities.EnqueuedMessage;
import pl.pb.model.*;
import pl.pb.publishContentContext.OSNIndicators;
import pl.pb.steganography.LSB.LSBMethod;
import pl.pb.utils.ImageUtility;
import pl.pb.utils.PropertiesUtility;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * Created by Patryk on 10/18/2017.
 */
public class StegPublisherResourceImpl implements StegPublisherResource {

    @Autowired
    StegHashWebappApplicationConfig stegHashWebappApplicationConfig;

    @Autowired
    StegHashModelConfig stegHashModelConfig;

    @Autowired
    private UserRepository userRepository;

    private final static int REDUNDANT_HASHTAGS = 0;

    private final static int VALUABLE_HASHTAGS = 1;

    private static volatile int enqueuedMessageObjectNumer = 0;

    private static  Set<EnqueuedMessage> enqueuedMessageBus = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static Logger LOGGER = LoggerFactory.getLogger(StegPublisherResourceImpl.class);


    public Response publishHiddenMessage(PublishMessage publishMessage) {
        int maxMessageLength = PropertiesUtility.getInstance().getIntegerProperty("messageLength");
        //number of OSN need to publish whole message
        int osnNumber = (int) Math.ceil((double) publishMessage.getMessage().length() / maxMessageLength);
        Map<OSNAPI, Boolean> sharedAPIs;
        User userFrom;
        EnqueuedMessage enrichedEnqueuedMessage;

        EnqueuedMessage enqueuedMessage = new EnqueuedMessage(enqueuedMessageObjectNumer++,
                publishMessage.getFrom(), Arrays.asList(publishMessage.getTo()));
        enqueuedMessageBus.add(enqueuedMessage);

        OSNAPIUtility osnapiUtility = stegHashWebappApplicationConfig.osnapiUtility();
        try {
            sharedAPIs = osnapiUtility.checkAvailableAccounts(publishMessage.getFrom(), Arrays.asList(
                    publishMessage.getTo()));

            switch(publishMessage.getImageOption()){
                case PROVIDED_BY_USER:
                    //TODO
                    break;
                case SELECTED_BY_USER:
                    //TODO
                    break;
                case RANDOM:
                    List<BufferedImage> images = ImageUtility.getRandomNumberOfImages(osnNumber);
                    userFrom = userRepository.findByUsername(publishMessage.getFrom()).get(0); //handling multiple accounts in sharedAPIs
                    LOGGER.info("[StegPublisher] Option: [RANDOM] Start publishing user message");
                    enrichedEnqueuedMessage = uploadContent(userFrom, sharedAPIs, Arrays.asList(publishMessage.getHashtags()),
                            images, osnNumber, publishMessage.getMessage(), enqueuedMessage);
                    LOGGER.info("[StegPublisher] Message published successfully");
                    //Everything ok, so we push data related with message to DB
                    MessagePublisher messagePublisher = stegHashModelConfig.messagePublisher();
                    messagePublisher.publishAllMessageElementsToDB(enrichedEnqueuedMessage);
                    LOGGER.info("[StegPublisher] Message metadata published to database");
                    break;
            }
        } catch (Exception e) {
            ResponseFromStegHash response = new ResponseFromStegHash();

            LOGGER.error("[StegReader] Error during publishing message for user: " + publishMessage.getFrom());
            LOGGER.error(e.getMessage());

            if (e instanceof DataConsistencyException) {
                response.setStatus(e.getMessage());
            } else if (e instanceof  ValidationException) {
                response.setStatus(e.getMessage());
            } else if (e instanceof FlickrException) {
                response.setStatus(e.getMessage());
            } else if (e instanceof TwitterException) {
                response.setStatus(e.getMessage());
            } else {
                response.setStatus("Unexpected error while publishing message. Try again later " +
                        "or contact with administrators.");
            }
            e.printStackTrace();
            enqueuedMessageBus.remove(enqueuedMessage);
            return Response.serverError().entity(e.getMessage()).build();
        }

        ResponseFromStegHash response = new ResponseFromStegHash();
        response.setStatus("Message published!");
        return Response.ok(response, MediaType.APPLICATION_JSON).build();
    }

    private EnqueuedMessage uploadContent(User user, Map<OSNAPI, Boolean> sharedAPIs,List<String> hashtagFromUser,
        List<BufferedImage> images, int osnNumber, String message, EnqueuedMessage enqueuedMessage) throws Exception {
        final Set<TwitterAccount> twitterAccountSet = new HashSet<>();
        final Set<FlickrAccount> flickrAccountSet = new HashSet<>();
        Map<Integer, List<String>> allHashtags = getHashtagPrefixAndPermutations(hashtagFromUser, osnNumber);
        List<List<String>> permutations = new LinkedList<>();
        List<String> hashtagsToBeMapped;
        OSNIndicators indicators;

        Map<Integer, List<String>> chainOfHashtags = HashTagChain.generateChainOfHashtags(
                allHashtags.get(VALUABLE_HASHTAGS));

        String messageWithoutNewLines = message.replace('\n', ' ');
        List <String> messageParts = new ArrayList<>(Arrays.asList(messageWithoutNewLines.split("(?<=\\G.{" +
                PropertiesUtility.getInstance().getIntegerProperty("messageLength") + "})")));
        //messageParts.forEach(System.out::println);

        chainOfHashtags.forEach((n,perm) -> permutations.add(perm));

        hashtagsToBeMapped = getLastHashtagsFromPermutations(permutations, messageParts.size());

        sharedAPIs.forEach((api, apiCheck) -> {
            if (apiCheck) {
                switch (api) {
                    case FLICKR:
                        user.getFlickrAccountSet().forEach(acc -> flickrAccountSet.add(acc));
                        break;
                    case TWITTER:
                        user.getTwitterAccountSet().forEach(acc -> twitterAccountSet.add(acc));
                        break;
                }
            }
        });

        if (twitterAccountSet.size() > 0 && flickrAccountSet.size() > 0) {
            List<OSNAccount> mergedAccounts = new ArrayList<>();
            int numberOfAccooutns1 = (int) Math.floor(osnNumber / 2);
            int numberOfAccooutns2 = osnNumber - numberOfAccooutns1;
            List<OSNAccount> availableTwitterAccounts = getRandomAccounts(twitterAccountSet,numberOfAccooutns1);
            List<OSNAccount> availableFlickrAccounts = getRandomAccounts(flickrAccountSet,numberOfAccooutns2);
            mergedAccounts.addAll(availableTwitterAccounts);
            mergedAccounts.addAll(availableFlickrAccounts);
            indicators = new OSNIndicators(hashtagsToBeMapped, mergedAccounts);
        } else if (twitterAccountSet.size() > 0 && flickrAccountSet.size() == 0) {
            indicators = new OSNIndicators(hashtagsToBeMapped, getRandomAccounts(twitterAccountSet,osnNumber));
        } else if (twitterAccountSet.size() == 0 && flickrAccountSet.size() > 0) {
            indicators = new OSNIndicators(hashtagsToBeMapped, getRandomAccounts(flickrAccountSet, osnNumber));
        } else {
            throw new UploadContentException("No accounts found, please provide some accounts");
        }

        OSNAccount nextOSNAccount = null;

        for (int i=0; i < messageParts.size(); i++) {
            BufferedImage steganogram = LSBMethod.setMessage(images.get(i), messageParts.get(i), i);
            List<String> hashtagPermutation = permutations.get(i);
            String mappingHashtag = getMappingHashtag(hashtagPermutation);
            enqueuedMessage.addPermutation(i, mergeHashtagLists(allHashtags.get(REDUNDANT_HASHTAGS), hashtagPermutation));
            String description = prepareDescription(allHashtags.get(REDUNDANT_HASHTAGS), hashtagPermutation);
            OSNIndicator indicator = indicators.getIndicatorByHashtag(mappingHashtag, nextOSNAccount);
            nextOSNAccount = indicator.getDestinationOSNAccount();
            publishContent(indicator.getOriginOSNAccount(), steganogram, description);
            addMapping(enqueuedMessage, indicator.getDestinationOSNAccount(), mappingHashtag);
        }
        return enqueuedMessage;
    }

    private void publishContent(OSNAccount account, BufferedImage steganogram, String description) throws Exception {
        if (account instanceof FlickrAccount) {
            stegHashWebappApplicationConfig.flickrAPI().publish(steganogram, description,
                    "png", ((FlickrAccount) account).getConsumerKey(), ((FlickrAccount) account).getConsumerSecret(),
                    account.getAccessToken(), account.getAccessSecret());
        } else if (account instanceof TwitterAccount) {
            stegHashWebappApplicationConfig.twitterAPI().publish(steganogram, description,
                    "png", ((TwitterAccount) account).getConsumerKey(), ((TwitterAccount) account).getConsumerSecret(),
                    account.getAccessToken(),account.getAccessSecret());
        }
    }

    private String getMappingHashtag(List<String> hashtagPermutation) {
        //get last hashtag -> mapping hashtag
        return hashtagPermutation.get(hashtagPermutation.size() - 1);
    }

    private void addMapping(EnqueuedMessage enqueuedMessage, OSNAccount account, String mappingHashtag) {
        if (account instanceof FlickrAccount) {
            enqueuedMessage.addOsnApiMapping(mappingHashtag, OSNAPI.FLICKR);
        } else if (account instanceof TwitterAccount) {
            enqueuedMessage.addOsnApiMapping(mappingHashtag, OSNAPI.TWITTER);
        }
    }

    private List<OSNAccount> getRandomAccounts(Set<? extends OSNAccount> osnAccounts, int osnNumber) {
        List<OSNAccount> accounts = new LinkedList<>();
        List<OSNAccount> osnAccountsTmp = new ArrayList<>(osnAccounts);
        for (int i=0; i < osnNumber; i++) {
            Collections.shuffle(osnAccountsTmp);
            accounts.add(osnAccountsTmp.get(0));
            osnAccountsTmp.remove(0);

            //not enough accounts, so we use existing accounts again
            if (osnAccountsTmp.size() == 0) {
                osnAccountsTmp.addAll(osnAccounts);
            }
        }
        return accounts;
    }

    private List<String> getLastHashtagsFromPermutations(List<List<String>> permutations, int hashtagNumber) {
        List<String> lastHashtags = new LinkedList<>();
        for (int i = 0; i < hashtagNumber; i++) {
            List<String> permutation = permutations.get(i);
            lastHashtags.add(permutation.get(permutation.size() -1));
        }
        return lastHashtags;
    }

    private Map<Integer, List<String>> getHashtagPrefixAndPermutations(List<String> hashtags,
                                                                       int osnNumber) throws ValidationException {
        Map<Integer, List<String>> result = new HashMap<>();
        List<String> redundantHashtags = new ArrayList<>();
        List<String> valuableHashtags = new ArrayList<>();
        int maxPermutationsNumber = IntStream.rangeClosed( 1, hashtags.size() )
                .reduce(1, ( int a, int b ) -> a * b);

        if (maxPermutationsNumber >= osnNumber) {
            int redundantHashtagsNumber = 0;
            for (int i = hashtags.size() - 1; i > 1; i--) {
                int temporaryPermutationsNumber = IntStream.rangeClosed( 1, i)
                        .reduce(1, ( int a, int b ) -> a * b);
                if (temporaryPermutationsNumber > osnNumber) {
                    redundantHashtagsNumber++;
                } else {
                    break;
                }
            }

            int maxIndex = hashtags.size() - 1;
            for (int i = maxIndex; i > redundantHashtagsNumber - 1; i--) {
                valuableHashtags.add(hashtags.get(i));
            }

            for (int j = 0; j < redundantHashtagsNumber; j++) {
                redundantHashtags.add(hashtags.get(j));
            }
        } else {
            throw new ValidationException("Incompatible number of hashtags to message size!");
        }

        result.put(REDUNDANT_HASHTAGS, redundantHashtags);
        result.put(VALUABLE_HASHTAGS, valuableHashtags);
        return result;
    }

    private String prepareDescription(List<String> redundantHashtags,  List<String> hashtagPermutation) {
        String description = "";

        for (int i = 0; i < redundantHashtags.size(); i++) {
            description += redundantHashtags.get(i) + " ";
        }

        for (int j = 0; j < hashtagPermutation.size(); j++) {
            description += hashtagPermutation.get(j) + " ";
        }

        return description;
    }

    private List<String> mergeHashtagLists(List<String> redundantHashtags,  List<String> hashtagPermutation) {
        List<String> mergedList = new LinkedList<>();
        mergedList.addAll(redundantHashtags);
        mergedList.addAll(hashtagPermutation);
        return mergedList;
    }
}
