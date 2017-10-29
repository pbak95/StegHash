package pl.pb.OSNAPIs;

import java.awt.image.BufferedImage;

/**
 * Created by Patryk on 10/18/2017.
 */
public interface OSNAPIWrapper {

    void publish(BufferedImage image, String description, String format) throws Exception;

}
