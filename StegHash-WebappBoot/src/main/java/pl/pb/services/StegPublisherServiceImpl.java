package pl.pb.services;

import org.springframework.beans.factory.annotation.Autowired;
import pl.pb.OSNAPIs.OSNAPIWrapper;
import pl.pb.config.StegHashWebappApplicationConfig;
import pl.pb.utils.PropertiesUtility;

import java.util.List;

/**
 * Created by Patryk on 10/18/2017.
 */
public class StegPublisherServiceImpl implements StegPublisherService {

    @Autowired
    StegHashWebappApplicationConfig config;

    public boolean publishHiddenMessage(String message, String from, String to) {
        int maxMessageLength = PropertiesUtility.getInstance().getIntegerProperty("messageLength");
        int osnNumber = (int) Math.ceil(message.length() / maxMessageLength);
        List<OSNAPIWrapper> apiList = config.getOSNHolder().getAvailableOSNAPIs(from);
        return false;
    }


}
