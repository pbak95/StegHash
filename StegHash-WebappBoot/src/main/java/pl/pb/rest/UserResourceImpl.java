package pl.pb.rest;

import org.springframework.beans.factory.annotation.Autowired;
import pl.pb.database_access.UserRepository;
import pl.pb.model.User;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Patryk on 11/20/2017.
 */
public class UserResourceImpl implements UserResource {

    @Autowired
    private UserRepository userRepository;

    public Response login(pl.pb.jsonMappings.User user) {
        List<User> users = userRepository.findByUsername(user.getUsername());
        boolean authorize = false;
        for (pl.pb.model.User userFromDb : users) {
            if (userFromDb.getUsername().equals(user.getUsername()) &&
                    userFromDb.getPassword().equals(user.getPassword())) {
                authorize = true;
                break;
            }
        }
        if (authorize) {
            return Response.ok(user, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}
