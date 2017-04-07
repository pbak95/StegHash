package webapp.services;

import webapp.dto.UserDTO;
import webapp.model.User;

public interface UserService {
	
	public void save(UserDTO userDTO);
	
	public User findByUsername(String username);
	
	
}
