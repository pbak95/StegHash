package pl.pb.database_access;

import org.springframework.transaction.annotation.Transactional;
import pl.pb.model.TwitterAccount;

/**
 * Created by Patryk on 10/29/2017.
 */
public class TwitterAccountDAO extends GenericDAO {

    @Transactional
    public void addTwitterAccount(TwitterAccount account){
        getSession().persist(account);
    }
}
