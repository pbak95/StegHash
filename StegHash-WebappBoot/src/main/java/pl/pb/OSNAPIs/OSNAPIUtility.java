package pl.pb.OSNAPIs;

import pl.pb.exceptions.DataConsistencyException;
import pl.pb.model.OSNType;

import java.util.List;
import java.util.Map;

/**
 * Created by Patryk on 11/2/2017.
 */
public interface OSNAPIUtility {

    Map<OSNType, Boolean> checkAvailableAccounts(String fromUser, List<String> toUser2) throws DataConsistencyException;
}


