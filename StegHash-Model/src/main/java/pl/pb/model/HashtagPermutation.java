package pl.pb.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Comparator;

/**
 * Created by Patryk on 11/11/2017.
 */
@Entity
@Table(name="HASHTAG_PERMUTATIONS")
public class HashtagPermutation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HASHTAG_PERMUTATION_ID", nullable = false)
    private long id;

    @Column(name = "PERMUTATION_NUMBER", nullable = false)
    private int permutationNumber;

    @Column(name = "HASHTAG_PERMUTATION", nullable = false)
    private String hashtagPermuation;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MESSAGE_ID", nullable = false)
    private Message message;

    public long getId() {
        return id;
    }

    public int getPermutationNumber() {
        return permutationNumber;
    }

    public void setPermutationNumber(int permutationNumber) {
        this.permutationNumber = permutationNumber;
    }

    public String getHashtagPermuation() {
        return hashtagPermuation;
    }

    public void setHashtagPermuation(String hashtagPermuation) {
        this.hashtagPermuation = hashtagPermuation;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public static final Comparator<HashtagPermutation> HASHTAG_PERMUTATION_COMPARATOR = (perm1, perm2) -> {
      return perm1.permutationNumber - perm2.permutationNumber;
    };
}
