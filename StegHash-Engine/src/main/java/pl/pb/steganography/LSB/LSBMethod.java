package pl.pb.steganography.LSB;

import pl.pb.utils.ImageUtility;
import pl.pb.utils.PropertiesUtility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;


/**
 * Created by Patryk on 2017-10-04.
 */
public class LSBMethod {



    public byte[] getBytes(String s){
        byte [] arr = s.getBytes(Charset.forName("UTF-8"));
        return arr;
    }

    public void setMessage(String name , String message) throws Exception {
        BufferedImage image = ImageUtility.fetchImage(name);
        byte[] messageInBytes = this.getBytes(message);
        int imgWidthPixelSize = image.getWidth() - 1; //array from 0 to width -1
        int imgHeightPixelSize = image.getHeight() - 1; //array from 0 to width -1
        //Add condition and throw exception
        int capacity = PropertiesUtility.getInstance().getIntegerProperty("capacity");
        if (capacity == -1) {
            throw new Exception("Bad format of capacity parameter!");
        }
        System.out.println("Size: " + image.getWidth() + " x " + image.getHeight());
        this.setMessageLength(image, messageInBytes.length, capacity);
        int i = 0;
        int j = 0 + (capacity * 8); //capacity - number of bytes, multiplied by 8 due to only lsb is used to store data
        for(byte b : messageInBytes){
            for(int k=7;k>=0;k--){
                Color c = new Color(image.getRGB(j,i));
                byte blue = (byte)c.getBlue();
                //System.out.println("Blue : "+c.getBlue());
                int red = c.getRed();
                int green = c.getGreen();
                //System.out.println("Red Green Blue : " + red + " "+ green + " "+blue);
                //System.out.println("Sum : "+ (red+green+(int)blue));
                int bitVal = (b >>> k) & 1;
                blue = (byte)((blue & 0xFE)| bitVal);
                //System.out.println("New Blue: " + (int)(blue & 0xFF) +" "+ blue);
                //System.out.println(j+" "+i);
                Color newColor = new Color(red,
                        green,(blue & 0xFF));
                image.setRGB(j,i,newColor.getRGB());
                j++;
                if (j > imgWidthPixelSize) {
                    j = 0;
                    if (i == imgHeightPixelSize && (b == messageInBytes[messageInBytes.length -1] && k != 0)) {
                        throw new Exception("Carrier capacity is to small!");
                    }
                    i++;
                }
            }
        }
        String[] baseName = name.split("\\.");
        String steganogramName = baseName[0] + "_steg." + baseName[1];
        System.out.println(steganogramName);
        ImageUtility.saveImage(image,steganogramName);

    }

    private void setMessageLength(BufferedImage image, int length, int capacity) throws Exception {
        String lengthStr = String.valueOf(length);
        byte[] lengthInBytes = this.getBytes(lengthStr);
        if (lengthInBytes.length < capacity) {
            byte[] emptyArr = new byte [capacity - lengthInBytes.length];
            byte[] tmp = lengthInBytes;
            lengthInBytes = new byte [tmp.length + emptyArr.length];
            System.arraycopy(emptyArr, 0, lengthInBytes, 0, emptyArr.length);
            System.arraycopy(tmp, 0, lengthInBytes, emptyArr.length, tmp.length);
        } else if (lengthInBytes.length > capacity) {
            throw new Exception("Capacity is to small!");
        }
        int imgWidthPixelSize = image.getWidth() - 1; //array from 0 to width -1
        int imgHeightPixelSize = image.getHeight() - 1; //array from 0 to width -1
        int i = 0;
        int j = 0;
        for (byte b : lengthInBytes) {
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
                    if (i == imgHeightPixelSize && (b == lengthInBytes[lengthInBytes.length -1] && k != 0)) {
                        throw new Exception("Carrier capacity is to small!");
                    }
                    i++;
                }
            }
        }
    }

    public String getMessage(String name) throws Exception {
        BufferedImage image = ImageUtility.fetchSteganogram(name);
        int capacity = PropertiesUtility.getInstance().getIntegerProperty("capacity");
        if (capacity == -1) {
            throw new Exception("Bad format of capacity parameter!");
        }
        int imgWidthPixelSize = image.getWidth() - 1; //array from 0 to width -1
        int imgHeightPixelSize = image.getHeight() - 1; //array from 0 to width -1
        int messageLength = this.getMessageLength(image, capacity);

        int i = 0;
        int j = 0 + (capacity * 8);
        byte [] hiddenBytes = new byte[messageLength];

        for(int l=0;l<messageLength;l++){
            for(int k=0 ; k<8 ; k++){
                Color c = new Color(image.getRGB(j,i));
                byte blue = (byte)c.getBlue();
                hiddenBytes[l] = (byte) ((hiddenBytes[l]<<1)|(blue&1));
                j++;
                if (j > imgWidthPixelSize) {
                    j = 0;
                    if (i == imgHeightPixelSize && l == messageLength - 1 && k < 7) {
                        throw new Exception("Carrier capacity is to small!");
                    }
                    i++;
                }
            }
        }
        return new String(hiddenBytes);
    }

    private int getMessageLength (BufferedImage image, int capacity) throws Exception {
        byte [] hiddenBytes = new byte[capacity];
        int imgWidthPixelSize = image.getWidth() - 1; //array from 0 to width -1
        int i = 0;
        int j = 0;

        for(int l=0;l<capacity;l++){
            for(int k=0 ; k<8 ; k++){
                Color c = new Color(image.getRGB(j,i));
                byte blue = (byte)c.getBlue();
                hiddenBytes[l] = (byte) ((hiddenBytes[l]<<1)|(blue&1));
                j++;
                if (j > imgWidthPixelSize) {
                    j = 0;
                    i++;
                }
            }
        }

        String lengthStr = new String(hiddenBytes,"UTF-8");
        lengthStr = lengthStr.substring(lengthStr.lastIndexOf(0) + 1);
        return PropertiesUtility.getInstance().tryParseInt(lengthStr)
                ? Integer.parseInt(lengthStr) : 0 ;
    }

//    private void convertToString(StringBuilder binary){
//        try{
//            BigInteger val = new BigInteger(""+binary, 2);
//            byte [] imageInByte = val.toByteArray();
//            String msg = new String(imageInByte);
//            System.out.println(msg);
//        }
//        catch(Exception ex){
//            System.out.println(ex);
//        }
//    }

//    private void convertToBinary(String s){
//        byte [] arr = s.getBytes(Charset.forName("UTF-8"));
//        StringBuilder binary = new StringBuilder();
//        for (byte b : arr){
//            int val = b;
//            for (int i = 0; i < 8; i++){
//                binary.append((val & 128) == 0 ? 0 : 1);
//                val <<= 1;
//            }
//        }
//        System.out.println("'" + s + "' to binary: " + binary);
//        convertToString(binary);
//    }
}


class ImageProcess {
    BufferedImage fetchImage() throws Exception{
        File f = new File("D:\\dyplom\\stegTests\\images\\lion.png");
        BufferedImage img = ImageIO.read(f);
        return img;
    }

    void hideText(BufferedImage img , byte [] txt) throws Exception{

        int i = 0;
        int j = 0;
        for(byte b : txt){
            for(int k=7;k>=0;k--){
                Color c = new Color(img.getRGB(j,i));
                byte blue = (byte)c.getBlue();
                //System.out.println("Blue : "+c.getBlue());
                int red = c.getRed();
                int green = c.getGreen();
                //System.out.println("Red Green Blue : " + red + " "+ green + " "+blue);
                //System.out.println("Sum : "+ (red+green+(int)blue));
                int bitVal = (b >>> k) & 1;
                blue = (byte)((blue & 0xFE)| bitVal);
                //System.out.println("New Blue: " + (int)(blue & 0xFF) +" "+ blue);
                //System.out.println(j+" "+i);
                Color newColor = new Color(red,
                        green,(blue & 0xFF));
                img.setRGB(j,i,newColor.getRGB());
                j++;
            }
            i++;
        }

        System.out.println("Text Hidden");
        createImgWithMsg(img);
        System.out.println("Decode? Y or N");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        switch(in.readLine().trim()){
            case "Y":
            case "y":
            {
                System.out.println("Text length is: " + txt.length);
                String k = revealMsg(txt.length,0);
                System.out.println("Text is: " + k);
            }
            break;
            default:
                System.out.println("Program is now ending");
                break;
        }
    }

    void createImgWithMsg(BufferedImage img){
        try{
            File ouptut = new File("D:\\dyplom\\stegTests\\steganograms\\lion.png");
            ImageIO.write(img, "png", ouptut);
        }
        catch(Exception ex)
        {}
    }

    BufferedImage newImageFetch(){
        File f = new File("D:\\dyplom\\stegTests\\steganograms\\lion_steg.png");
        BufferedImage img = null;
        try{
            img = ImageIO.read(f);
        }
        catch(Exception ex)
        {}
        return img;
    }

    public String revealMsg(int msgLen , int offset){
        BufferedImage img = newImageFetch();
        byte [] msgBytes = extractHiddenBytes(img,msgLen,offset);
        if(msgBytes == null)
            return null;
        String msg = new String(msgBytes);
        return (msg);
    }

    byte[] extractHiddenBytes(BufferedImage img , int size , int offset){
        int i = 0;
        int j = 0 + 4;
        byte [] hiddenBytes = new byte[size];

        for(int l=0;l<size;l++){
            for(int k=0 ; k<8 ; k++){
                Color c = new Color(img.getRGB(j,i));
                byte blue = (byte)c.getBlue();
//                System.out.println("Blue : "+c.getBlue()+" "+blue);
//                int red = c.getRed();
//                int green = c.getGreen();
//                System.out.println("Red : "+c.getRed());
//                System.out.println("Green : "+c.getGreen());
                //System.out.println("blue : "+blue);
                //System.out.println("Hidden byte<<1: "+(hiddenBytes[l]<<1));
                //System.out.println("blue&1 : "+(blue&1));
                //System.out.println("(hiddenBytes[l]<<1)|(blue&1) : "+ ((hiddenBytes[l]<<1)|(blue&1)));
                hiddenBytes[l] = (byte) ((hiddenBytes[l]<<1)|(blue&1));
                //System.out.println("Hidden byte"+ l +" : "+hiddenBytes[l]);
                //System.out.println(j+" "+i);
                j++;
            }
            i++;
        }
        return hiddenBytes;
    }
}
