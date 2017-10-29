package pl.pb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.pb.OSNAPIs.OSNHolder;
import pl.pb.OSNAPIs.OSNHolderImpl;
import pl.pb.OSNAPIs.dropbox.DropboxAPI;
import pl.pb.services.StegPublisherService;
import pl.pb.services.StegPublisherServiceImpl;

/**
 * Created by Patryk on 10/18/2017.
 */
@Configuration
public class StegHashWebappApplicationConfig {

    @Bean
    public StegPublisherService getStegPublisherService() {
        return new StegPublisherServiceImpl();
    }

    @Bean
    public OSNHolder getOSNHolder() {
        return new OSNHolderImpl();
    }

    @Bean
    public DropboxAPI getDropboxAPI() {
        return new DropboxAPI();
    }
}
