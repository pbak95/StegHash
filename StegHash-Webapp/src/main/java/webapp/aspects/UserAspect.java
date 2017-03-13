package webapp.aspects;




import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class UserAspect {


	
	@Before("execution(public String home())")
	public void userHomeRedirected(){
		System.out.println("Executing Advice on home() from StegController");
	}
}
