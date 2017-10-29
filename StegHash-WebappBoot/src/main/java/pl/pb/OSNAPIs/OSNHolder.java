package pl.pb.OSNAPIs;

import java.util.List;

/**
 * Created by Patryk on 10/18/2017.
 */
public interface OSNHolder {

    List<OSNAPIWrapper> getAvailableOSNAPIs (String username);
}
