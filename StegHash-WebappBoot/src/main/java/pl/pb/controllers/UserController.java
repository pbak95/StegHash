package pl.pb.controllers;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.pb.entities.User;

@RestController
public class UserController {



	@RequestMapping(value = "/login-post", method = RequestMethod.POST, produces =
			 {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<User> login(@RequestBody User user) {
		//Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
		//SecurityContextHolder.getContext().setAuthentication(auth);
		return new ResponseEntity<User>(user, HttpStatus.OK);
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
