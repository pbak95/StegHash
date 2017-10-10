package pl.pb.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Patryk on 2017-10-08.
 */
public class ImageUtility {


    public static BufferedImage fetchImage(String name) throws Exception{
        File inputFile = new File(PropertiesUtility.getInstance().getProperty("images") + name);
        BufferedImage img = ImageIO.read(inputFile);
        return img;
    }

    public static void saveImage(BufferedImage img, String name){
        try {
            File ouptutFile = new File(PropertiesUtility.getInstance().getProperty("steganograms") + name);
            ImageIO.write(img, "png", ouptutFile);
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static BufferedImage fetchSteganogram(String name) throws Exception{
        File inputFile = new File(PropertiesUtility.getInstance().getProperty("steganograms") + name);
        BufferedImage img = ImageIO.read(inputFile);
        return img;
    }
}
