package pl.pb.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pl.pb.database_access.*;
import pl.pb.utils.PropertiesUtility;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Patryk on 10/29/2017.
 */
@Configuration
@ComponentScan(value={"pl.pb"})
@EnableTransactionManagement
public class StegHashModelConfig {

    private static  String USERNAME = PropertiesUtility.getInstance().getProperty("oracle.username");

    private String PASSWORD = PropertiesUtility.getInstance().getProperty("oracle.password");

    private String URL = PropertiesUtility.getInstance().getProperty("oracle.url");

    @Bean
    DataSource dataSource() throws SQLException {
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setUser(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setURL(URL);
        dataSource.setImplicitCachingEnabled(true);
        dataSource.setFastConnectionFailoverEnabled(true);
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory()throws SQLException {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(new String[]{"pl.pb.model"});
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    private Properties hibernateProperties(){
        Properties properties = new Properties();
        properties.put("hibernate.dialect", PropertiesUtility.getInstance().getProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", PropertiesUtility.getInstance().getProperty("hibernate.show_sql"));
        properties.put("hibernate.hbm2ddl.auto", PropertiesUtility.getInstance().getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.current.session.context.class", PropertiesUtility.getInstance().getProperty(
                "hibernate.current.session.context.class"));
        return properties;
    }


    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory){
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }

    @Bean
    public FlickrAccountDAO flickrAccountDAO() {
        return new FlickrAccountDAO();
    }

    @Bean
    public TwitterAccountDAO twitterAccountDAO() {
        return new TwitterAccountDAO();
    }

    @Bean
    public UserDAO userDAO() {
        return new UserDAO();
    }

    @Bean
    public HashtagPermutationDAO hashtagPermutationDAO() {
        return new HashtagPermutationDAO();
    }

    @Bean
    public MessageDAO messageDAO() {
        return new MessageDAO();
    }

    @Bean
    public OSNMappingDAO osnMappingDAO() {
        return new OSNMappingDAO();
    }

    @Bean
    public MessagePublisher messagePublisher() {
        return new MessagePublisher();
    }

    @Bean
    public MessageReader messageReader() {
        return new MessageReader();
    }

}
