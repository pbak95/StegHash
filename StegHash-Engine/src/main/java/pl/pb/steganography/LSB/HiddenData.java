package pl.pb.steganography.LSB;

/**
 * Created by Patryk on 11/10/2017.
 */
public class HiddenData {

    private String message;

    private int permutationNumber;

    public HiddenData(String message, int permutationNumber) {
        this.message = message;
        this.permutationNumber = permutationNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPermutationNumber() {
        return permutationNumber;
    }

    public void setPermutationNumber(int permutationNumber) {
        this.permutationNumber = permutationNumber;
    }
}
