package pl.pb.database_access;

import org.springframework.transaction.annotation.Transactional;
import pl.pb.model.Message;

/**
 * Created by Patryk on 11/11/2017.
 */
public class MessageDAO extends GenericDAO {

    @Transactional
    public void addMessage(Message message){
        getSession().persist(message);
    }
}
