package webapp.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object>{


	public void initialize(PasswordMatches constraintAnnotation) {
	}

	public boolean isValid(Object value, ConstraintValidatorContext context) {
		
		if(value == null)
			return true;
		UserDTO user = (UserDTO) value;
		return user.getEmail().equals(user.getMatchingPassword());
	}

	
	

}
