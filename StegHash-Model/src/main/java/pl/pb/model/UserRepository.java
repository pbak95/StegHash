package pl.pb.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by Patryk on 11/2/2017.
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findById(Long id);

    List<User> findByUsername(String username);
}
