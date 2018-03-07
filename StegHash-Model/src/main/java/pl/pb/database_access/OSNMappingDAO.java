package pl.pb.database_access;

import org.springframework.transaction.annotation.Transactional;
import pl.pb.model.OSNMapping;

/**
 * Created by Patryk on 11/11/2017.
 */
public class OSNMappingDAO extends GenericAbstractDAO {

    @Transactional
    public void addOSNMapping(OSNMapping mapping){
        getSession().persist(mapping);
    }
}
