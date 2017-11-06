package pl.pb.OSNAPIs;

import org.springframework.beans.factory.annotation.Autowired;
import pl.pb.exceptions.DataConsistencyException;
import pl.pb.model.User;
import pl.pb.model.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Patryk on 11/5/2017.
 */
public class OSNAPIUtilityImpl implements OSNAPIUtility{

    @Autowired
    private UserRepository userRepository;

    @Override
    public Map<OSNAPI, Boolean> checkAvailableAccounts(String fromUser, String toUser) throws DataConsistencyException {
        Map<OSNAPI, Boolean> availableAccounts = new HashMap<>();

        Map<OSNAPI, Boolean> fromAccounts = getAccountsForUser(fromUser);
        Map<OSNAPI, Boolean> toAccounts = getAccountsForUser(toUser);

        toAccounts.forEach((toApi,toApiCheck) -> {
            if (fromAccounts.get(toApi) == toApiCheck == true) {
                availableAccounts.put(toApi,toApiCheck);
            }
        });

        return availableAccounts;
    }

    private Map<OSNAPI, Boolean> getAccountsForUser(String username) throws DataConsistencyException {
        Map<OSNAPI, Boolean> accountsMap = new HashMap<>();
        List<User> users = userRepository.findByUsername(username);

        if (users.size() > 1) {
            throw new DataConsistencyException("Multiple result for this username!");
        } else if (users.isEmpty()) {
            return accountsMap;
        }

        User user = users.get(0);

        if (user.getTwitterAccountSet().size() > 0) {
            accountsMap.put(OSNAPI.TWITTER, true);
        } else {
            accountsMap.put(OSNAPI.TWITTER, false);
        }

        if (user.getFlickrAccountSet().size() > 0) {
            accountsMap.put(OSNAPI.FLICKR, true);
        } else {
            accountsMap.put(OSNAPI.FLICKR, false);
        }
        return accountsMap;
    }
}
