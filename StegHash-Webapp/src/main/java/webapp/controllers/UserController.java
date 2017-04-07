package webapp.controllers;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import webapp.dto.UserDTO;
import webapp.model.UserDAO;
import webapp.services.SecurityService;
import webapp.services.UserService;
import webapp.validators.UserValidator;

@Controller
public class UserController {

	private static final Logger logger = Logger.getLogger(UserController.class);

	@Autowired
	UserDAO dao;

	@Autowired
	UserService userService;

	@Autowired
	UserValidator userValidator;

	@Autowired
	private SecurityService securityService;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView loginGet(@RequestParam(value = "error", required = false) String error) {

		ModelAndView model = new ModelAndView();
		if (error != null) {
			model.addObject("error", "Invalid Credentials provided.");
		}

		model.setViewName("login");
		return model;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginPost() {
		return "home";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			logger.debug("logout procedure with role: " + auth);
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login?logout";
	}

	@Secured("ROLE_USER")
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String home() {	
		return "home";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String register(Model model) {
		model.addAttribute("userDTO", new UserDTO());
		return "registration";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String registerValidation(HttpServletRequest request, @ModelAttribute("userDTO") @Valid UserDTO userDTO,
			BindingResult result) {
		userValidator.validate(userDTO, result);

		if (result.hasErrors()) {
			return "registration";
		}

		userService.save(userDTO);

		securityService.autologin(userDTO.getUsername(), userDTO.getMatchingPassword(), request);

		return "redirect:/home";
	}
}
