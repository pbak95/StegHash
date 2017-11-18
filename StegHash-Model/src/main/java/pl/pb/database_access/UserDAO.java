package pl.pb.database_access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.exceptions.DataConsistencyException;
import pl.pb.model.OSNAPI;
import pl.pb.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO extends GenericDAO {

	@Autowired
	UserRepository userRepository;

	@Transactional
	public void addUser(User user){
		getSession().persist(user);
	}

	@Transactional
	public void updateUser(User user) {
		getSession().merge(user);
	}

	public Map<OSNAPI, Boolean> getAccountsForUser(String username) throws DataConsistencyException {
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
