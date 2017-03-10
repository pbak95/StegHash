package webapp.dto;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class UserDTO {
	
	@NotEmpty
    @Size(min=3, max=60)
	String username;
	
	@NotEmpty
	@Size(min=3, max=60)
	String password;
	
	@NotEmpty
	@Email
	String email;
}
