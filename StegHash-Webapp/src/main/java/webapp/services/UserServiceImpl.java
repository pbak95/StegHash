package webapp.services;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import webapp.dto.UserDTO;
import webapp.model.User;
import webapp.model.UserDAO;
import webapp.model.UserInterface;

@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);


	@Autowired
	UserDAO userRepository;

	@Autowired
	UserInterface userRepository2;

	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	@Transactional
	public void save(UserDTO userDTO){
		User user = new User();
		user.setEmail(userDTO.getEmail());
		user.setUsername(userDTO.getUsername());
		user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
		user.setRole("ROLE_USER");
		user.setEnabled(1);
		userRepository.addUser(user);
		logger.debug("User REGISTERED!");

	}

	@Transactional
	public User findByUsername(String username) {
		return userRepository2.findByUsername(username);
	}
	

}
