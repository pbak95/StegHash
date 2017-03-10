package webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import webapp.model.UserDAO;

@Controller
public class StegController {
	
	@Autowired
	UserDAO dao;

	@RequestMapping("/login")
	public String login(Model model) {
		return "login";
	}

	@Secured("ROLE_USER")
	@RequestMapping("/home")
	public String home(Model model) {
		return "home";
	}
}
