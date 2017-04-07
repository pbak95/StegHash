package webapp.services;

import javax.servlet.http.HttpServletRequest;

public interface SecurityService {

	String findLoggedInUsername();

    void autologin(String username, String password, HttpServletRequest request);
}
