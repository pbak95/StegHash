import org.junit.Assert;
import org.junit.Test;
import pl.pb.steganography.LSB.LSBMethod;

/**
 * Created by Patryk on 10/12/2017.
 */
public class LSBMethodTest {

    @Test
    public void testIfMessageIsHiddenAndRetrieveProperly() {
        String msg = "Simple message7654";
        String result = "";
        try {
            LSBMethod.setMessage("lion.png", msg);
            result = LSBMethod.getMessage("lion_steg.png");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Assert.assertEquals("Message hidden in picture should be equal: " + msg, msg, result);

    }
}
