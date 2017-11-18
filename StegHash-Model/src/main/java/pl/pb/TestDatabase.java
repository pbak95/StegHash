package pl.pb;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pl.pb.config.StegHashModelConfig;
import pl.pb.model.FlickrAccount;
import pl.pb.database_access.FlickrAccountDAO;
import pl.pb.model.User;
import pl.pb.database_access.UserDAO;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Patryk on 10/29/2017.
 */
public class TestDatabase {


    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = null;

        try {
            context = new AnnotationConfigApplicationContext(StegHashModelConfig.class);
            UserDAO userDAO = context.getBean(UserDAO.class);
            FlickrAccountDAO flickrAccountDAO = context.getBean(FlickrAccountDAO.class);
            User user = new User();
            user.setUsername("usr1");
            user.setPassword("usr1");
            user.setEnabled(1);
            user.setEmail("usr1@test.pl");
            user.setRole("USER");
            FlickrAccount flickrAccount = new FlickrAccount();
            flickrAccount.setAccessToken("72157688314019393-cf16a913c8441f3d");
            flickrAccount.setAccessSecret("4e404854da2c5262");
            flickrAccount.setUser(user);
            Set<FlickrAccount> accoountsSet = new HashSet();
            accoountsSet.add(flickrAccount);
            user.setFlickrAccountSet(accoountsSet);
            userDAO.addUser(user);
            flickrAccountDAO.addFlickrAccount(flickrAccount);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.close();
        }
    }
}
