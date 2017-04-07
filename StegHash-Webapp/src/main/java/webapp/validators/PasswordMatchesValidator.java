package webapp.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import webapp.dto.UserDTO;

@Component
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserDTO>{


	public void initialize(PasswordMatches constraintAnnotation) {
	}

	public boolean isValid(UserDTO value, ConstraintValidatorContext context) {
		
		if(value == null)
			return true;
		//UserDTO user = (UserDTO) value;
		return value.getEmail().equals(value.getMatchingPassword());
	}

	
	

}
