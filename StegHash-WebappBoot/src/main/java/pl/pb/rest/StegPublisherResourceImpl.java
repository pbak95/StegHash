package pl.pb.rest;

import org.springframework.beans.factory.annotation.Autowired;
import pl.pb.OSNAPIs.OSNAPI;
import pl.pb.OSNAPIs.OSNAPIUtility;
import pl.pb.config.StegHashWebappApplicationConfig;
import pl.pb.engine.HashTagChain;
import pl.pb.entities.PublishMessage;
import pl.pb.entities.ResponseFromStegHash;
import pl.pb.exceptions.DataConsistencyException;
import pl.pb.exceptions.UploadContentException;
import pl.pb.exceptions.ValidationException;
import pl.pb.model.*;
import pl.pb.steganography.LSB.LSBMethod;
import pl.pb.utils.ImageUtility;
import pl.pb.utils.PropertiesUtility;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Created by Patryk on 10/18/2017.
 */
public class StegPublisherResourceImpl implements StegPublisherResource {

    @Autowired
    StegHashWebappApplicationConfig stegHashWebappApplicationConfig;

    @Autowired
    private UserRepository userRepository;

    private final static int REDUNDANT_HASHTAGS = 0;

    private final static int VALUABLE_HASHTAGS = 1;

    public Response publishHiddenMessage(PublishMessage publishMessage) {
        int maxMessageLength = PropertiesUtility.getInstance().getIntegerProperty("messageLength");
        int osnNumber = (int) Math.ceil((double) publishMessage.getMessage().length() / maxMessageLength);
        Map<OSNAPI, Boolean> sharedAPIs;
        User userFrom;

        OSNAPIUtility osnapiUtility = stegHashWebappApplicationConfig.osnapiUtility();
        try {
            sharedAPIs = osnapiUtility.checkAvailableAccounts(publishMessage.getFrom(), publishMessage.getTo());

            switch(publishMessage.getImageOption()){
                case PROVIDED_BY_USER:
                    //TO_DO
                    break;
                case SELECTED_BY_USER:
                    //TO_DO
                    break;
                case RANDOM:
                    List<BufferedImage> images = ImageUtility.getRandomNumberOfImages(osnNumber);

//                    if (images.size() != publishMessage.getHashtags().size()) {
//                        throw new ValidationException("Incompatible number of hashtags to message size!");
//                    }

                    userFrom = userRepository.findByUsername(publishMessage.getFrom()).get(0); //handling multiple accounts in sharedAPIs
                    uploadContent(userFrom, sharedAPIs, Arrays.asList(publishMessage.getHashtags()), images, osnNumber,
                            publishMessage.getMessage());
                    break;
            }

        } catch (DataConsistencyException e) {
            e.printStackTrace();
            return  Response.serverError().entity(e.getMessage()).build();
        } catch (ValidationException e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
        ResponseFromStegHash response = new ResponseFromStegHash();
        response.setStatus("Message published!");
        return Response.ok(response, MediaType.APPLICATION_JSON).build();
    }

    private void uploadContent(User user, Map<OSNAPI, Boolean> sharedAPIs,List<String> hashtagFromUser,
        List<BufferedImage> images, int osnNumber, String message) throws Exception {
        final Set<TwitterAccount> twitterAccountSet = new HashSet<>();
        final Set<FlickrAccount> flickrAccountSet = new HashSet<>();
        Map<Integer, List<String>> allHashtags = getHashtagPrefixAndPermutations(hashtagFromUser, osnNumber);
        List<List<String>> permutations = new LinkedList<>();
        Map<Integer, List<String>> chainOfHashtags = HashTagChain.generateChainOfHashtags(
                allHashtags.get(VALUABLE_HASHTAGS));
        chainOfHashtags.forEach((n,perm) -> permutations.add(perm));
        Map<String, OSNAccount> mappedAccounts;

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
            mappedAccounts = linkHashtagWithAccount(allHashtags.get(VALUABLE_HASHTAGS), mergedAccounts);
        } else if (twitterAccountSet.size() > 0 && flickrAccountSet.size() == 0) {
            mappedAccounts = linkHashtagWithAccount(allHashtags.get(VALUABLE_HASHTAGS), getRandomAccounts(twitterAccountSet,osnNumber));
        } else if (twitterAccountSet.size() == 0 && flickrAccountSet.size() > 0) {
            mappedAccounts = linkHashtagWithAccount(allHashtags.get(VALUABLE_HASHTAGS), getRandomAccounts(flickrAccountSet, osnNumber));
        } else {
            throw new UploadContentException("No accounts found, please provide some accounts");
        }

        List <String> messageParts = new ArrayList<>(Arrays.asList(message.split("(?<=\\G.{" +
                PropertiesUtility.getInstance().getIntegerProperty("messageLength") + "})")));
        messageParts.forEach(System.out::println);

        for (int i=0; i < messageParts.size(); i++) {
            BufferedImage steganogram = LSBMethod.setMessage(images.get(i), message);
            //push chains to DB with previous mapped hashtags
            List<String> hashtagPermutation = permutations.get(i);
            String description = prepareDescription(allHashtags.get(REDUNDANT_HASHTAGS), hashtagPermutation);
            OSNAccount account = mappedAccounts.get(hashtagPermutation.get(hashtagPermutation.size() - 1)); //get last hashtag -> mapping hashtag
            if (account instanceof FlickrAccount) {
                stegHashWebappApplicationConfig.flickrAPI().publish(steganogram, description,
                        "png", account.getAccessToken(), account.getAccessSecret());
            } else if (account instanceof TwitterAccount) {
                stegHashWebappApplicationConfig.twitterAPI().publish(steganogram, description,
                        "png", ((TwitterAccount) account).getConsumerKey(), ((TwitterAccount) account).getConsumerSecret(),
                        account.getAccessToken(),account.getAccessSecret());
            }
        }
    }

    private void getApiAndUpload(List<List<String>> permutations, Map<String, OSNAccount> mappedAccounts,
                                 String message,  List<BufferedImage> images) {
//        List <String> messageParts = new ArrayList<>(Arrays.asList(message.split("(?<=\\G.{" + images.size() + "})")));
//        messageParts.forEach(System.out::println);
//
//        for (int i=0; i < messageParts.size(); i++) {
//            List<String> hashtagPermitation = permutations.get(i);
//            OSNAccount account = mappedAccounts.get(hashtagPermitation.get(hashtagPermitation.size() - 1)); //get last hashtag -> mapping hashtag
//            if (account instanceof FlickrAccount) {
//                stegHashWebappApplicationConfig
//            } else if (account instanceof TwitterAccount) {
//
//            }
//        }

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

    private Map<String, OSNAccount> linkHashtagWithAccount(List<String> hashtags, List<OSNAccount> accounts) {
        Map<String, OSNAccount> mappedAccounts = new HashMap<>();
        for (int i=0; i < hashtags.size(); i++) {
            mappedAccounts.put(hashtags.get(i), accounts.get(i));
        }
        //map hashtag to account and persist to DB !!!!!!!!!!

        return mappedAccounts;
    }

    private Map<Integer, List<String>> getHashtagPrefixAndPermutations(List<String> hashtags,
                                                                       int osnNumber) throws ValidationException {
        Map<Integer, List<String>> result = new HashMap<>();
        List<String> redundantHashtags = new ArrayList<>();
        List<String> valuableHashtags = new ArrayList<>();
        if (hashtags.size() > osnNumber) {
            int maxIndex = hashtags.size() - 1;

            for (int i = 0; i < osnNumber; i++) {
                valuableHashtags.add(hashtags.get(maxIndex--));
            }

            for (int j = 0; j <= maxIndex; j++) {
                redundantHashtags.add(hashtags.get(j));
            }
        } else if (hashtags.size() == osnNumber) {
            valuableHashtags.addAll(hashtags);
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

    public String test() {
        return "Hello";
    }
}
