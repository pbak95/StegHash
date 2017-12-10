package pl.pb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.pb.OSNAPIs.OSNAPIUtilityImpl;
import pl.pb.OSNAPIs.dropbox.DropboxAPI;
import pl.pb.OSNAPIs.flickr.FlickrAPI;
import pl.pb.OSNAPIs.twitter.TwitterAPI;
import pl.pb.rest.*;

/**
 * Created by Patryk on 10/18/2017.
 */
@Configuration
public class StegHashWebappApplicationConfig {



    @Bean
    public StegPublisherResource stegPublisherResource() {
        return new StegPublisherResourceImpl();
    }

    @Bean
    public UserResource userResource() {
        return new UserResourceImpl();
    }

    @Bean
    public StegReaderResource stegReaderResource() {
        return new StegReaderResourceImpl();
    }

    @Bean
    public DropboxAPI dropboxAPI() {
        return new DropboxAPI();
    }

    @Bean
    public TwitterAPI twitterAPI() {
        return new TwitterAPI();
    }

    @Bean
    public FlickrAPI flickrAPI() {
        return new FlickrAPI();
    }

    @Bean
    public OSNAPIUtilityImpl osnapiUtility() {
        return new OSNAPIUtilityImpl();
    }
}
