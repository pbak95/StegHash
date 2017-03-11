package webapp.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import webapp.dto.UserDTO;
import webapp.model.User;
import webapp.model.UserDAO;
import webapp.services.EmailExistsException;
import webapp.services.UserService;

@Controller
public class StegController {
	
	@Autowired
	UserDAO dao;
	
	@Autowired
	UserService userService;

	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	@Secured("ROLE_USER")
	@RequestMapping("/home")
	public String home() {
		return "home";
	}
	
//	@RequestMapping(value="/registration", method = RequestMethod.GET)
//	public String register(){
//		return "registration";
//	}
	
	@RequestMapping(value="/registration")
	public String registerValidation(HttpServletRequest request,@ModelAttribute("userDTO") @Valid UserDTO userDTO, BindingResult result){
		if(request.getMethod().equalsIgnoreCase("post") && !result.hasErrors()){
			
			try {
				userService.registerNewUserAccount(userDTO);
			} catch (EmailExistsException e) {
				result.rejectValue("email", "This e-mail address exists");
				return "registration";
			}
			return "redirect:/home";	
		}
		return "registration";
	}
}
