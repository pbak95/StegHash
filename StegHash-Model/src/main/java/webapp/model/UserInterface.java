package webapp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInterface extends JpaRepository<User, Long>{

	public User findByEmail(String email);
	
	public User findByUsername(String email);
}
