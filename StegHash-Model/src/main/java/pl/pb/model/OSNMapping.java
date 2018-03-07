package pl.pb.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Comparator;

/**
 * Created by Patryk on 11/11/2017.
 */
@Entity
@Table(name="OSN_MAPPINGS")
public class OSNMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OSN_MAPPING_ID", nullable = false)
    private long id;

    @Column(name = "MAPPING_HASHTAG", nullable = false)
    private String hashtag;

    @Column(name = "OSN_TYPE_ID", nullable = false)
    private OSNType osnType;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MESSAGE_ID", nullable = false)
    private Message message;

    public long getId() {
        return id;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    @Enumerated(EnumType.ORDINAL)
    public OSNType getOsnType() {
        return osnType;
    }

    public void setOsnType(OSNType osnType) {
        this.osnType = osnType;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public static final Comparator<OSNMapping> MAPPING_COMPARATOR = (mapping1, mapping2) -> {
        return Long.valueOf(mapping1.id).compareTo(Long.valueOf(mapping2.id));
    };
}
