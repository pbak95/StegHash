package pl.pb.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

/**
 * Created by Patryk on 10/29/2017.
 */
@Entity
@Table(name="FLICKR_ACCOUNTS")
public class FlickrAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TWITTER_ACCOUNT_ID", nullable = false)
    private long id;


    @Column(name = "ACCESS_TOKEN", nullable = false)
    private String accessToken;

    @Column(name = "ACCESS_TOKEN_SECRET", nullable = false)
    private String accessTokenSecret;

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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
