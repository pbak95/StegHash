package webapp.services;

public class EmailExistsException extends Exception {
	public EmailExistsException(String msg){
		super(msg);
	}
}
