package pl.pb.steganography;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


/**
 * https://github.com/varunon9/Image-Stegano
 * @author varun
 */
public class LSBEncoding {

    ImageUtilityToReuse imageUtilityToReuse;

    public LSBEncoding() {
        imageUtilityToReuse = new ImageUtilityToReuse();
    }

    private byte[] encodeText(BufferedImage coverImage,
                           String message, int bitArray[]) {
        byte image[] = imageUtilityToReuse.getByteData(coverImage);
        byte payload[] = message.getBytes();
        int offset = 0;
        int imageLength = image.length;
        boolean data[] = convertToBits(payload);
        int dataLength = data.length;
        int dataOverFlag = 0;
        for (int i = 0; i < imageLength && dataOverFlag == 0; i++) {
            for (int j = 7; j >= 0  && dataOverFlag == 0; j--) {
                if (bitArray[j] == 1) {
                    int mask = returnMask(j);
                    image[i] = (byte) ((image[i] & mask));
                    if (data[offset++]) {
                        image[i] = (byte) (image[i] | ~mask);
                    }
                    if (offset >= dataLength) {
                        dataOverFlag = 1;
                    }
                }
            }
        }

        return image;
    }

    private void writeImageToFile (byte[] encodedImage) {
        String stegPath =  Paths.get("D:\\dyplom\\stegTests\\steganograms").toString();
        ByteArrayInputStream in = new ByteArrayInputStream(encodedImage);
        try {
            BufferedImage stegImage = ImageIO.read(in);
            System.out.println(stegPath + File.separatorChar + "stegImage.png");
            File fileToWrite =  new File(stegPath + File.separatorChar + "stegImage.png");
            ImageIO.write(stegImage, "png", fileToWrite);
        } catch (IOException e) {
            System.out.println(e.getMessage());

        }
    }

    public void doSteganography () {
        String imgPath = Paths.get("D:\\dyplom\\stegTests\\images\\test.png").toString();
        this.applySteganography(imgPath);
    }

    public void applySteganography(String imageFilePath) {
        if(imageFilePath != null ){
            try {
                BufferedImage source = loadImageToCopy(imageFilePath);
                int[] mask = {0,0,0,0,0,0,0,1};
                this.writeImageToFile(this.encodeText(source,"Test", mask));
                System.out.println("Passed");
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    protected BufferedImage loadImageToCopy(String path) {
        File picture = new File(path);
        if (picture.exists()) {
            try {
                BufferedImage image = ImageIO.read(picture);
                //create a new buffered image of with the same size and an 8bit 3 color type
                BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

                //copies the image into the
                newImage.getGraphics().drawImage(image, 0, 0, null);
                return newImage;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return null;
    }

    private int returnMask(int bit) {
        int mask = 0xFF;
        switch (bit) {
            case 0:
                mask = 0xFE;
                break;
            case 1:
                mask = 0xFD;
                break;
            case 2:
                mask = 0xFB;
                break;
            case 3:
                mask = 0xF7;
                break;
            case 4:
                mask = 0xEF;
                break;
            case 5:
                mask = 0xDF;
                break;
            case 6:
                mask = 0xBF;
                break;
            case 7:
                mask = 0x7F;
                break;
        }
        return mask;
    }

    private boolean[] convertToBits(byte payload[]) {
        boolean result[] = new boolean[8 * payload.length];
        int offset = 0;
        for (byte b: payload) {
            for (int i = 7; i >= 0; i--) {
                int singleBit = (b >> i) & 1;
                if (singleBit == 1) {
                    result[offset++] = true;
                } else {
                    result[offset++] = false;
                }
            }
        }
        return result;
    }

    public String decodeText(BufferedImage coverImage, int bitArray[]) {
        byte image[] = imageUtilityToReuse.getByteData(coverImage);
        int offset = 0;
        int imageLength = image.length;

        // counting how many bits are modified per byte
        int count = 0;
        for (int i = 0; i < bitArray.length; i++) {
            if (bitArray[i] == 1) {
                count++;
            }
        }

        boolean data[] = new boolean[imageLength * count];
        for (int i = 0; i < imageLength; i++) {
            for (int j = 7; j >= 0; j--) {
                if (bitArray[j] == 1) {
                    int singleBit = (image[i] >> j) & 1;
                    if (singleBit == 1) {
                        data[offset++] = true;
                    } else {
                        data[offset++] = false;
                    }
                }
            }
        }

        // converting boolean array to byte array
        int secretMessageLength = (imageLength * count) / 8;
        byte secretMessage[] = new byte[secretMessageLength];
        for (int i = 0; i < secretMessageLength; i++) {
            for (int bit = 0; bit < 8; bit++) {
                if (data[i * 8 + bit]) {
                    secretMessage[i] |= (128 >> bit);
                }
            }
        }
        try {
            return new String(secretMessage, "ASCII");
        } catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
