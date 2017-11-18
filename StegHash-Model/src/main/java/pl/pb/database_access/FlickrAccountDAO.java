package pl.pb.database_access;

import org.springframework.transaction.annotation.Transactional;
import pl.pb.model.FlickrAccount;

/**
 * Created by Patryk on 10/29/2017.
 */
public class FlickrAccountDAO extends GenericDAO {

    @Transactional
    public void addFlickrAccount(FlickrAccount account){
        getSession().persist(account);
    }
}
