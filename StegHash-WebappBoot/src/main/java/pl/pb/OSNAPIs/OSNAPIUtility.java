package pl.pb.OSNAPIs;

import pl.pb.exceptions.DataConsistencyException;

import java.util.Map;

/**
 * Created by Patryk on 11/2/2017.
 */
public interface OSNAPIUtility {

    Map<OSNAPI, Boolean> checkAvailableAccounts(String username1, String username2) throws DataConsistencyException;
}


