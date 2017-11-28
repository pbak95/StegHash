package pl.pb.database_access;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.model.Message;
import pl.pb.model.User;

import java.util.List;

/**
 * Created by Patryk on 11/20/2017.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByUserFrom(User user);
}
