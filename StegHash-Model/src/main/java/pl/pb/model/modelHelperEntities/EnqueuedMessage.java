package pl.pb.model.modelHelperEntities;


import pl.pb.model.OSNType;
import pl.pb.model.OSNMapping;

import java.util.*;

/**
 * Created by Patryk on 11/10/2017.
 */
public class EnqueuedMessage {

    private int id;

    private String userFrom;

    private List<String> userTo;

    private Map<Integer, List<String>> permutations;

    private List<OSNMapping> osnApiMappings;

    public EnqueuedMessage(int id, String userFrom, List<String> userTo) {
        this.id = id;
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.permutations = new HashMap<>();
        this.osnApiMappings = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public List<String> getUserTo() {
        return userTo;
    }

    public Map<Integer, List<String>> getPermutations() {
        return permutations;
    }

    public void addPermutation(int permNumber, List<String> permutation) {
        this.permutations.put(permNumber, permutation);
    }

    public List<OSNMapping> getOsnApiMappings() {
        return osnApiMappings;
    }

    public void addOsnApiMapping(String hashtag, OSNType api) {
        OSNMapping osnMapping = new OSNMapping();
        osnMapping.setHashtag(hashtag);
        osnMapping.setOsnType(api);
        this.osnApiMappings.add(osnMapping);
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof EnqueuedMessage)) {
            return false;
        }

        EnqueuedMessage enqueuedMessage = (EnqueuedMessage) o;
        return id == enqueuedMessage.id &&
                Objects.equals(userFrom, enqueuedMessage.userFrom) &&
                Objects.equals(userTo, enqueuedMessage.userTo);
    }
}
