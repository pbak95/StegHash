package pl.pb.database_access;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Patryk on 11/11/2017.
 */
public abstract class GenericAbstractDAO {

    @Autowired
    protected SessionFactory sessionFactory;

    protected Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }
}
