package pl.pb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;

/**
 * Created by Patryk on 11/11/2017.
 */
@Entity
@Table(name="MESSAGES")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_ID", nullable = false)
    long id;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="USER_RECEIVED_MESSAGES",joinColumns=@JoinColumn(name = "MESSAGE_ID"),
            inverseJoinColumns=@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID"))
    private Set<User> usersTo;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", nullable = false, referencedColumnName = "USER_ID")
    private User userFrom;

    @Column(name = "MESSAGE_DATE", nullable = false)
    private String messageDate;

    @JsonBackReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "message")
    private Set<OSNMapping> mappings;

    @JsonBackReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "message")
    private Set<HashtagPermutation> hashtagPermutations;


    public long getId() {
        return id;
    }

    public Set<User> getUsersTo() {
        return usersTo;
    }

    public void setUsersTo(Set<User> usersTo) {
        this.usersTo = usersTo;
    }

    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String date) {
        this.messageDate = date;
    }

    public Set<OSNMapping> getMappings() {
        return mappings;
    }

    public void setMappings(Set<OSNMapping> mappings) {
        this.mappings = mappings;
    }

    public void addHashtagPermutation(HashtagPermutation permutation) {
        this.hashtagPermutations.add(permutation);
    }

    public Set<HashtagPermutation> getHashtagPermutations() {
        return hashtagPermutations;
    }

    public void setHashtagPermutations(Set<HashtagPermutation> hashtagPermutations) {
        this.hashtagPermutations = hashtagPermutations;
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


    public static final Comparator<Message> MESSAGE_COMPARATOR = (message1, message2) -> {
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = dateFormat.parse(message1.getMessageDate());
            date2 = dateFormat.parse(message2.getMessageDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1.compareTo(date2);
    };
}
