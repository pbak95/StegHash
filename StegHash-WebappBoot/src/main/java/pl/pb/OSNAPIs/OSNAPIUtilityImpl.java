package pl.pb.OSNAPIs;

import org.springframework.beans.factory.annotation.Autowired;
import pl.pb.config.StegHashModelConfig;
import pl.pb.database_access.UserDAO;
import pl.pb.exceptions.DataConsistencyException;
import pl.pb.model.OSNAPI;
import pl.pb.model.User;
import pl.pb.database_access.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Patryk on 11/5/2017.
 */
public class OSNAPIUtilityImpl implements OSNAPIUtility{

    @Autowired
    private StegHashModelConfig stegHashModelConfig;

    @Override
    public Map<OSNAPI, Boolean> checkAvailableAccounts(String fromUser, List<String> toUser) throws DataConsistencyException {
        Map<OSNAPI, Boolean> availableAccounts = new HashMap<>();

        UserDAO userDAO = stegHashModelConfig.userDAO();
        Map<OSNAPI, Boolean> fromAccounts = userDAO.getAccountsForUser(fromUser);
        List<Map<OSNAPI, Boolean>> toUsersAvailableAPIs = new ArrayList<>();
        for (String username : toUser) {
            toUsersAvailableAPIs.add(userDAO.getAccountsForUser(username));
        }
        Map<OSNAPI, Boolean> toAccounts = getMapWithTheLowestNumberOfAccounts(toUsersAvailableAPIs);
        toAccounts.forEach((toApi,toApiCheck) -> {
            if (fromAccounts.get(toApi) == toApiCheck == true) {
                availableAccounts.put(toApi,toApiCheck);
            }
        });

        return availableAccounts;
    }

    private Map<OSNAPI, Boolean> getMapWithTheLowestNumberOfAccounts(List<Map<OSNAPI, Boolean>> toUsersAvailableAPIs) {
        Map<OSNAPI, Boolean> result = toUsersAvailableAPIs.get(0);
        for (Map<OSNAPI, Boolean> map : toUsersAvailableAPIs) {
            if (map.size() < result.size()) {
                result = map;
            }
        }
        return result;
    }
}
