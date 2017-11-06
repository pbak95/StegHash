package pl.pb.steganography.LSB;

import pl.pb.utils.ImageUtility;
import pl.pb.utils.PropertiesUtility;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.charset.Charset;


/**
 * Created by Patryk on 2017-10-04.
 */
public class LSBMethod {

    private static byte[] getBytes(String s){
        byte [] arr = s.getBytes(Charset.forName("UTF-8"));
        return arr;
    }

    public static BufferedImage setMessage(BufferedImage image , String message) throws Exception {  // <- String name
        byte[] messageInBytes = getBytes(message);
        int capacity = PropertiesUtility.getInstance().getIntegerProperty("capacity");
        if (capacity == -1) {
            throw new Exception("Bad format of capacity parameter!");
        }
        System.out.println("Size: " + image.getWidth() + " x " + image.getHeight());
        LSBMethod.setMessageLength(image, messageInBytes.length, capacity);
        encodeBytesInPixels(image,messageInBytes,capacity * 8); //capacity - number of bytes, multiplied by 8 due to only lsb is used to store data
//        if (name != null) {
//            String[] baseName = name.split("\\.");
//            String steganogramName = baseName[0] + "_steg." + baseName[1];
//            System.out.println(steganogramName);
//            ImageUtility.saveImage(image, steganogramName);
//        }
        return image;
    }

    private static void setMessageLength(BufferedImage image, int length, int capacity) throws Exception {
        String lengthStr = String.valueOf(length);
        byte[] lengthInBytes = getBytes(lengthStr);
        if (lengthInBytes.length < capacity) {
            byte[] emptyArr = new byte [capacity - lengthInBytes.length];
            byte[] tmp = lengthInBytes;
            lengthInBytes = new byte [tmp.length + emptyArr.length];
            System.arraycopy(emptyArr, 0, lengthInBytes, 0, emptyArr.length);
            System.arraycopy(tmp, 0, lengthInBytes, emptyArr.length, tmp.length);
        } else if (lengthInBytes.length > capacity) {
            throw new Exception("Capacity is to small!");
        }
        encodeBytesInPixels(image, lengthInBytes,0);
    }

    private static void encodeBytesInPixels(BufferedImage image, byte[] data, int offset) throws Exception{
        int imgWidthPixelSize = image.getWidth() - 1; //array from 0 to width -1
        int imgHeightPixelSize = image.getHeight() - 1; //array from 0 to width -1
        int i = 0;
        int j = 0 + offset;
        for (byte b : data) {
            for(int k=7;k>=0;k--){
                Color c = new Color(image.getRGB(j,i));
                byte blue = (byte)c.getBlue();
                int red = c.getRed();
                int green = c.getGreen();
                int bitVal = (b >>> k) & 1;
                blue = (byte)((blue & 0xFE)| bitVal);
                Color newColor = new Color(red,
                        green,(blue & 0xFF));
                image.setRGB(j,i,newColor.getRGB());
                j++;
                if (j > imgWidthPixelSize) {
                    j = 0;
                    if (i == imgHeightPixelSize && (b == data[data.length -1] && k != 0)) {
                        throw new Exception("Carrier capacity is to small!");
                    }
                    i++;
                }
            }
        }
    }

    public static String getMessage(BufferedImage image) throws Exception {
        //BufferedImage image = ImageUtility.fetchSteganogram(name);
        int capacity = PropertiesUtility.getInstance().getIntegerProperty("capacity");
        if (capacity == -1) {
            throw new Exception("Bad format of capacity parameter!");
        }
        int messageLength = getMessageLength(image, capacity);
        byte [] hiddenBytes = decodeBytesFromPixels(image, messageLength,capacity * 8);
        return new String(hiddenBytes);
    }

    private static int getMessageLength (BufferedImage image, int capacity) throws Exception {
        byte [] hiddenBytes = decodeBytesFromPixels(image,capacity, 0);
        String lengthStr = new String(hiddenBytes,"UTF-8");
        lengthStr = lengthStr.substring(lengthStr.lastIndexOf(0) + 1);
        return PropertiesUtility.getInstance().tryParseInt(lengthStr)
                ? Integer.parseInt(lengthStr) : 0 ;
    }

    private static byte[] decodeBytesFromPixels(BufferedImage image, int capacity, int offset) throws Exception {
        byte [] hiddenBytes = new byte[capacity];
        int imgWidthPixelSize = image.getWidth() - 1; //array from 0 to width -1
        int imgHeightPixelSize = image.getHeight() - 1; //array from 0 to width -1
        int i = 0;
        int j = 0 + offset;
        for(int l=0;l<capacity;l++){
            for(int k=0 ; k<8 ; k++){
                Color c = new Color(image.getRGB(j,i));
                byte blue = (byte)c.getBlue();
                hiddenBytes[l] = (byte) ((hiddenBytes[l]<<1)|(blue&1));
                j++;
                if (j > imgWidthPixelSize) {
                    j = 0;
                    if (i == imgHeightPixelSize && l == capacity - 1 && k < 7) {
                        throw new Exception("Carrier capacity is to small!");
                    }
                    i++;
                }
            }
        }
        return hiddenBytes;
    }
}

