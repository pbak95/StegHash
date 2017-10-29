package pl.pb.OSNAPIs.twitter;

import org.springframework.beans.factory.annotation.Autowired;
import pl.pb.OSNAPIs.OSNAPIWrapper;
import pl.pb.OSNAPIs.dropbox.DropboxAPI;
import pl.pb.config.StegHashWebappApplicationConfig;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import java.awt.image.BufferedImage;

/**
 * Created by Patryk on 10/22/2017.
 */
public class TwitterAPI implements OSNAPIWrapper {

    @Autowired
    StegHashWebappApplicationConfig config;

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

    @Override
    public void publish(BufferedImage image, String description, String format) throws Exception {
        DropboxAPI dbxAPI = config.getDropboxAPI();
        String pathToImage = dbxAPI.upload(image,description,format);
        String tweetMessage = description + "\n" + pathToImage;
        Status status = this.getTwitterInstance().updateStatus(tweetMessage);
        //debug
        System.out.println("Successfully updated the status to [" + status.getText() + "].");
    }

    private Twitter getTwitterInstance() {
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
        return twitter;
    }
}
