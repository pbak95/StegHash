package pl.pb.steganography.LSB;

import pl.pb.utils.PropertiesUtility;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Patryk on 2017-10-04.
 */
public class LSBMethod {

    private static String MAKRUP = "~#<";

    private static Logger LOGGER = LoggerFactory.getLogger(LSBMethod.class);


    public static BufferedImage setMessage(BufferedImage image , String message, int permutationNumber) throws Exception {  // <- String name
        byte[] messageInBytes = getBytes(addStartMarkup(message));
        int capacity = PropertiesUtility.getInstance().getIntegerProperty("capacity");
        int permutationNumberLength = PropertiesUtility.getInstance().getIntegerProperty("permutationNumberLength");
        if (capacity == -1) {
            throw new Exception("[StegHash Engine] Bad format of capacity parameter!");
        }
        LOGGER.info("[StegHash Engine] Carrier size: " + image.getWidth() + " x " + image.getHeight());
        //set number of bytes with message length
        LSBMethod.setMessageParam(image, messageInBytes.length, capacity, 0);
        //set permutation number
        LSBMethod.setMessageParam(image, permutationNumber, permutationNumberLength, capacity * 8);
        //used number of bytes, multiplied by 8 due to only least significant bit is used to store data
        encodeBytesInPixels(image,messageInBytes,(capacity + permutationNumberLength) * 8);
//        if (name != null) {
//            String[] baseName = name.split("\\.");
//            String steganogramName = baseName[0] + "_steg." + baseName[1];
//            System.out.println(steganogramName);
//            ImageUtility.saveImage(image, steganogramName);
//        }
        return image;
    }

    private static void setMessageParam(BufferedImage image, int param, int paramSize, int offset) throws Exception {
        String lengthStr = String.valueOf(param);
        byte[] paramInBytes = getBytes(lengthStr);
        if (paramInBytes.length <= paramSize) {
            byte[] emptyArr = new byte [paramSize - paramInBytes.length];
            byte[] tmp = paramInBytes;
            paramInBytes = new byte [tmp.length + emptyArr.length];
            System.arraycopy(emptyArr, 0, paramInBytes, 0, emptyArr.length);
            System.arraycopy(tmp, 0, paramInBytes, emptyArr.length, tmp.length);
        } else if (paramInBytes.length > paramSize) {
            throw new Exception("[StegHash Engine] Capacity is to small!");
        }
        encodeBytesInPixels(image, paramInBytes,offset);
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
                        throw new Exception("[StegHash Engine] Carrier capacity is to small!");
                    }
                    i++;
                }
            }
        }
    }

    public static HiddenData getHiddenData(BufferedImage image) throws Exception {
        //BufferedImage image = ImageUtility.fetchSteganogram(name);
        int capacity = PropertiesUtility.getInstance().getIntegerProperty("capacity");
        int permutationNumberLength = PropertiesUtility.getInstance().getIntegerProperty("permutationNumberLength");

        if (capacity == -1) {
            throw new Exception("[StegHash Engine] Bad format of capacity parameter!");
        }
        int messageLength = getMessageParam(image, capacity,0);
        int permutationNumber = getMessageParam(image, permutationNumberLength, capacity * 8);
        byte [] hiddenBytes = decodeBytesFromPixels(image, messageLength,(capacity + permutationNumberLength) * 8);
        String hiddenMessage = new String(hiddenBytes);
        if (!hiddenMessage.startsWith(LSBMethod.MAKRUP)) {
            throw new Exception("[StegHash Engine] Corrupted hidden message.");
        }
        return new HiddenData(removeMarkup(hiddenMessage), permutationNumber);
    }

    private static int getMessageParam(BufferedImage image, int capacity, int offset) throws Exception {
        byte [] hiddenBytes = decodeBytesFromPixels(image,capacity, offset);
        String paramStr = new String(hiddenBytes,"UTF-8");
        paramStr = paramStr.substring(paramStr.lastIndexOf(0) + 1);
        return PropertiesUtility.getInstance().tryParseInt(paramStr)
                ? Integer.parseInt(paramStr) : 0 ;
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
                        throw new Exception("[StegHash Engine] Carrier capacity is to small!");
                    }
                    i++;
                }
            }
        }
        return hiddenBytes;
    }

    private static byte[] getBytes(String s){
        byte [] arr = s.getBytes(Charset.forName("UTF-8"));
        return arr;
    }

    private static String addStartMarkup(String message) {
        return LSBMethod.MAKRUP + message;
    }

    private static String removeMarkup(String messageWithMarkup) {
        return messageWithMarkup.substring(LSBMethod.MAKRUP.length());
    }
}

