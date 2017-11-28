package pl.pb.controllers;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.pb.config.StegHashModelConfig;
import pl.pb.database_access.UserRepository;
import pl.pb.jsonMappings.User;

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces =
			 {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<User> login(@RequestBody User user) {
		List<pl.pb.model.User> users = userRepository.findByUsername(user.getUsername());
		boolean authorize = false;
		for (pl.pb.model.User userFromDb : users) {
			if (userFromDb.getUsername().equals(user.getUsername()) &&
					userFromDb.getPassword().equals(user.getPassword())) {
				authorize = true;
				break;
			}
		}

		return new ResponseEntity<>(user, authorize ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
	}

	@RequestMapping("/user")
	  public Principal user(Principal user) {
	    return user;
	  }

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public  void logout(HttpServletRequest request, HttpServletResponse response) {
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		if (auth != null) {
//			new SecurityContextLogoutHandler().logout(request, response, auth);
//		}
		// return (Principal) auth.getPrincipal();
	}
}
