import org.junit.Assert;
import org.junit.Test;
import pl.pb.steganography.LSB.HiddenData;
import pl.pb.steganography.LSB.LSBMethod;
import pl.pb.utils.ImageUtility;

import java.awt.image.BufferedImage;

/**
 * Created by Patryk on 10/12/2017.
 */
public class LSBMethodTest {

    @Test
    public void testIfMessageIsHiddenAndRetrieveProperly() {
        String msg = "Simple message";
        HiddenData result;
        try {
            BufferedImage image = ImageUtility.fetchImage("sky.png");
            BufferedImage steganogram = LSBMethod.setMessage(image, msg, 23);
            result = LSBMethod.getHiddenData(steganogram);
            ImageUtility.saveImage(steganogram, "sky_steg.png");
            Assert.assertEquals("Message hidden in picture should be equal: " + msg, msg, result.getMessage());
            Assert.assertEquals("Hidden permutation number should be equal 23", 23, result.getPermutationNumber());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
}
