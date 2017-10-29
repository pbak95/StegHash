package pl.pb.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Patryk on 10/29/2017.
 */
@Repository
@EnableTransactionManagement
public class TwitterAccountDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }


    @Transactional
    public void addTwitterAccount(TwitterAccount account){
        getSession().persist(account);
    }
}
