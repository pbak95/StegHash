package pl.pb.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Patryk on 11/10/2017.
 */
public class EnqueuedMessage {

    private int id;

    private String userFrom;

    private List<String> userTo;

    private Map<Integer, List<String>> permutations;

    private Map<String, OSNAPI> osnApiMappings;

    public EnqueuedMessage(int id, String userFrom, List<String> userTo) {
        this.id = id;
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.permutations = new HashMap<>();
        this.osnApiMappings = new HashMap<>();
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

    public Map<String, OSNAPI> getOsnApiMappings() {
        return osnApiMappings;
    }

    public void addOsnApiMapping(String hashtag, OSNAPI api) {
        this.osnApiMappings.put(hashtag, api);
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
