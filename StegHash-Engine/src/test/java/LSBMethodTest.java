import org.junit.Assert;
import org.junit.Test;
import pl.pb.steganography.LSB.LSBMethod;
import pl.pb.utils.ImageUtility;

import java.awt.image.BufferedImage;

/**
 * Created by Patryk on 10/12/2017.
 */
public class LSBMethodTest {

    @Test
    public void testIfMessageIsHiddenAndRetrieveProperly() {
        String msg = "Simple message7654";
        String result = "";
        try {
            BufferedImage image = ImageUtility.fetchImage("lion.png");
            BufferedImage steganogram = LSBMethod.setMessage(image, msg);
            result = LSBMethod.getMessage(steganogram);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Assert.assertEquals("Message hidden in picture should be equal: " + msg, msg, result);

    }
}
