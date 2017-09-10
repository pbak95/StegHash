package pl.pb.model;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@Repository
@EnableTransactionManagement
public class UserDAO {

	@Autowired
	private DataSource dataSource;
	
	@PersistenceContext
    EntityManager entityManager;
	
	
	@Transactional
	public void addUser(User user){
		entityManager.persist(user);
	}
}