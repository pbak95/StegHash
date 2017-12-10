package pl.pb.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Patryk on 2017-10-08.
 */
public class ImageUtility {


    public static BufferedImage fetchImage(String name) throws Exception {
        File inputFile = new File(PropertiesUtility.getInstance().getProperty("images") + name);
        BufferedImage img = ImageIO.read(inputFile);
        return img;
    }

    public static List<BufferedImage> getRandomNumberOfImages(int number) {
        List<BufferedImage> images = new ArrayList<>();
        File directory = new File(PropertiesUtility.getInstance().getProperty("images"));
        List<String> fileNames = new ArrayList<String>(Arrays.asList(directory.list()));
        List<String> randomNames = new ArrayList<>();
        for (int i=0; i < number; i++) {
            Collections.shuffle(fileNames);
            randomNames.add(fileNames.get(0));
        }

        randomNames.forEach(name -> {
            try {
                images.add(ImageUtility.fetchImage(name));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return images;
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
