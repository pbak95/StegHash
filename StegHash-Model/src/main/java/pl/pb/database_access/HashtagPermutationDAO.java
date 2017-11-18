package pl.pb.database_access;

import org.springframework.transaction.annotation.Transactional;
import pl.pb.model.HashtagPermutation;

/**
 * Created by Patryk on 11/11/2017.
 */
public class HashtagPermutationDAO extends GenericDAO {

    @Transactional
    public void addHashtagPermutation(HashtagPermutation permutation){
        getSession().persist(permutation);
    }
}
