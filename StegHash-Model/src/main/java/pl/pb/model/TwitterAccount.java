package pl.pb.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

/**
 * Created by Patryk on 10/29/2017.
 */
@Entity
@Table(name="TWITTER_ACCOUNTS")
public class TwitterAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TWITTER_ACCOUNT_ID", nullable = false)
    private long id;

    @Column(name = "CONSUMER_KEY", nullable = false)
    private String consumerKey;

    @Column(name = "CONSUMER_SECRET", nullable = false)
    private String consumerSecret;

    @Column(name = "ACCESS_TOKEN", nullable = false)
    private String accessToken;

    @Column(name = "ACCESS_SECRET", nullable = false)
    private String accessSecret;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
