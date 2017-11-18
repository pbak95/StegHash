package pl.pb.OSNAPIs.twitter;

import org.springframework.beans.factory.annotation.Autowired;
import pl.pb.OSNAPIs.dropbox.DropboxAPI;
import pl.pb.config.StegHashWebappApplicationConfig;
import pl.pb.downloadContentContext.DownloadedItem;
import pl.pb.exceptions.*;
import pl.pb.exceptions.TwitterException;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Patryk on 10/22/2017.
 */
public class TwitterAPI {

    @Autowired
    StegHashWebappApplicationConfig stegHashWebappApplicationConfig;

    public static void main(String args[]) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        //GET this properties from USER
        // Twitter Consumer key & Consumer Secret
        twitter.setOAuthConsumer("R6xZsRTF9wmqwZGoAdytHN5U3", "OJAb8F1FUj1rjuwhhiahA6hKIXf4oyEp17a7oI0LEX34d0iyhQ");
        // Twitter Access token & Access token Secret
        twitter.setOAuthAccessToken(new AccessToken("922092323294580742-CIMeCwx93S4SYCYDb9aGqngjvgQhsFn",
                "UdVicIeLD8Db5pxkgND6fW2XQmsgqqikQmGRCRShwtjxW"));
        try {
            // Getting Twitter Timeline using Twitter4j API
//            List<Status> statuses = twitter.getHomeTimeline();
//            System.out.println("Showing home timeline.");
//            for (Status status : statuses) {
//                System.out.println(status.getUser().getName() + ":" +
//                        status.getText());
//            }
                //Post a Tweet using Twitter4j API
//            String message = "#StegHash #Test \n" +
//                    "https://www.dropbox.com/s/rximzlcnnokabtn/lion_steg.png?dl=0";
//            Status status = twitter.updateStatus(message);
//            System.out.println("Successfully updated the status to [" + status.getText() + "].");
            Query query = new Query("#StegHash #Test");
            QueryResult result = twitter.search(query);
            for (Status status : result.getTweets()) {
                System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
            }
        } catch (Exception e) {
        }
    }

    public void publish(BufferedImage image, String description, String format,
                        String consumerToken, String consumerTokenSecret,
                        String accessToken, String accessTokenSecret) throws TwitterException {
        DropboxAPI dbxAPI = stegHashWebappApplicationConfig.dropboxAPI();
        try {
            String pathToImage = dbxAPI.upload(image,description,format);
            String tweetMessage = description + "\n" + pathToImage;
            Status status = this.getTwitterInstance(consumerToken, consumerTokenSecret,
                    accessToken, accessTokenSecret).updateStatus(tweetMessage);
            //debug
            System.out.println("Successfully updated the status to [" + status.getText() + "].");
        } catch (DropboxException dbxException) {
            throw new TwitterException("[Twitter] There are problems with publishing content, try again later or " +
                    "contact with administrators.");
        } catch (twitter4j.TwitterException twitterexception) {
            throw new TwitterException("[Twitter] Authentication credentials error, ensure that you have " +
                    "provide valid Access Token/Access Token Secret or Consumer Key/Consumer Secret. " +
                    "In case there are valid, just refresh them :).");
        }

    }

    public List<DownloadedItem> downloadImages(String hashtagPermutationStr,String consumerToken,
                                               String consumerTokenSecret, String accessToken,
                                               String accessTokenSecret ) throws TwitterException {
        List<DownloadedItem> downloadedItems = new ArrayList<>();
        Twitter twitter =  this.getTwitterInstance(consumerToken, consumerTokenSecret,
                accessToken, accessTokenSecret);
        DropboxAPI dbxAPI = stegHashWebappApplicationConfig.dropboxAPI();

        Query query = new Query(hashtagPermutationStr);
        try {
            QueryResult result = twitter.search(query);
            for (Status status : result.getTweets()) {
                DownloadedItem downloadedItem = new DownloadedItem();
                List<String> hashtags = new LinkedList<>();

                HashtagEntity[] hashtagEntities = status.getHashtagEntities();
                List<HashtagEntity> hashtagEntityList = Arrays.asList(hashtagEntities);
                hashtagEntityList.forEach(hashtagEntity -> hashtags.add(hashtagEntity.getText()));
                downloadedItem.setHashtags(hashtags);

                BufferedImage image = dbxAPI.downloadByPath(getDropboxPath(status.getText()));
                downloadedItem.setBufferedImage(image);

                downloadedItems.add(downloadedItem);
            }
        } catch (twitter4j.TwitterException twitterexception) {
            new TwitterException("[Twitter] Problem with downloading content from Twitter, ensure that you have " +
                    "provide valid Access Token/Access Token Secret or Consumer Key/Consumer Secret. " +
                    "In case there are valid, just refresh them :).");
        } catch (DropboxException e) {
            new TwitterException("[Twitter] Problem with downloading content from Twitter, please contact with administrator.");
        }

        return downloadedItems;
    }



    private Twitter getTwitterInstance(String consumerToken, String consumerTokenSecret,
                                       String accessToken, String accessTokenSecret) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        // Twitter Consumer key & Consumer Secret
        twitter.setOAuthConsumer(consumerToken, consumerTokenSecret);
        // Twitter Access token & Access token Secret
        twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));
        return twitter;
    }

    private String getDropboxPath(String statusText) {
        String[] statusParts = statusText.split("\n");
        return statusParts[statusParts.length -1];
    }
}
