package pl.pb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name="USERS")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_ID", nullable = false)
	private long id;
	@Column(name = "USERNAME",nullable = false)
    private String username;
	@Column(name = "PASSWORD", nullable = false)
    private String password;
	@Column(name = "ROLE", nullable = false)
    private String role;
	@Column(name = "ENABLED", nullable = false)
	private int enabled;
	@Column(name = "EMAIL", nullable = false)
	private String email;
	@JsonBackReference
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private Set<TwitterAccount> twitterAccountSet = new HashSet<>();
	@JsonBackReference
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private Set<FlickrAccount> flickrAccountSet = new HashSet<>();
	@JsonBackReference
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "userFrom")
	private Set<Message> userMessagesSent = new HashSet<>();
	@JsonBackReference
	@ManyToMany(mappedBy="usersTo")
	private Set<Message> userMessagesReceived = new HashSet<>();
	
	public long getId() {
		return id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public int getEnabled() {
		return enabled;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public Set<TwitterAccount> getTwitterAccountSet() {
		return twitterAccountSet;
	}

	public void setTwitterAccountSet(Set<TwitterAccount> twitterAccountSet) {
		this.twitterAccountSet = twitterAccountSet;
	}

	public Set<FlickrAccount> getFlickrAccountSet() {
		return flickrAccountSet;
	}

	public void setFlickrAccountSet(Set<FlickrAccount> flickrAccountSet) {
		this.flickrAccountSet = flickrAccountSet;
	}

	public Set<Message> getUserMessagesSent() {
		return userMessagesSent;
	}

	public void addUserMessageSent(Message message) {
		this.userMessagesSent.add(message);
	}

	public void setUserMessagesSent(Set<Message> userMessagesSent) {
		this.userMessagesSent = userMessagesSent;
	}

	public Set<Message> getUserMessagesReceived() {
		return userMessagesReceived;
	}

	public void addUserMessageReceived(Message message) {
		this.userMessagesReceived.add(message);
	}

	public void setUserMessagesReceived(Set<Message> userMessagesReceived) {
		this.userMessagesReceived = userMessagesReceived;
	}
}
