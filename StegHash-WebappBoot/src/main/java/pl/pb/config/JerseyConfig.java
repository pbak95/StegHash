package pl.pb.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import pl.pb.rest.StegPublisherResourceImpl;
import pl.pb.rest.StegReaderResourceImpl;
import pl.pb.rest.UserResourceImpl;
import pl.pb.security.CorsFilter;

/**
 * Created by Patryk on 11/5/2017.
 */
@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        registerEndpoints();
    }

    private void registerEndpoints() {
         register(StegPublisherResourceImpl.class);
         register(StegReaderResourceImpl.class);
         register(CorsFilter.class);
         register(UserResourceImpl.class);
    }
}
