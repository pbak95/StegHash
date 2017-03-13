package webapp.services;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import webapp.dto.UserDTO;

import webapp.model.IUserService;
import webapp.model.User;
import webapp.model.UserDAO;

@Service
public class UserService {

	@Autowired
	Validator validator;
	
	@Autowired
	IUserService userInterface;
	
	@Autowired
	UserDAO dao;
		
	@Transactional
	public void registerNewUserAccount(UserDTO userDTO) throws EmailExistsException{
		
		if(emailExists(userDTO.getEmail())){
			throw new EmailExistsException(
					"There is an account with that email adress: "
				              +  userDTO.getEmail());
		}
		
		User userToAdd = new User();
		userToAdd.setEmail(userDTO.getEmail());
		userToAdd.setUsername(userDTO.getUsername());
		userToAdd.setPassword(userDTO.getPassword());
		userToAdd.setRole("ROLE_USER");
		userToAdd.setEnabled(1);
		dao.addUser(userToAdd);
	}
	
	private boolean emailExists(String email){
		User user = userInterface.findByEmail(email);
		if(user != null)
			return true;
		return false;
	}
}
