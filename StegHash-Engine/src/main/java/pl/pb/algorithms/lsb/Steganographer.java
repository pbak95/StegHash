/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.pb.algorithms.lsb;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Eric
 */
public abstract class Steganographer {
    
    /**
     * Applies steganography to encode the contents of byte[] data to the pixels
     *  of the BufferedImage object destination
     * @param data byte array containing data to be encoded
     * @param destination - image that will be written to
     */
    protected abstract void write( byte[] data, BufferedImage destination);

    protected abstract byte[] read(BufferedImage stegImage);
    
    public abstract void ApplySteganography(String imageFilePath, String dataFilePath);
    
    public abstract void ReadStegImage(String filepath);
    
    /**
     * Loads the image specified by path and then makes and returns a copy of it.
     * This copy is entirely editable and any changes made to it will not be 
     * reflected in the source image.
     * @param path
     * @return 
     */
    protected BufferedImage LoadImage_Copy(String path) {
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
                Logger.getLogger(Steganographer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    /**
     *
     * @param image
     * @return 
     */
    protected byte[] LoadImageBytes(BufferedImage image) {
        if (image != null) {
            WritableRaster wRaster = image.getRaster();
            DataBufferByte byteBuffer = (DataBufferByte) wRaster.getDataBuffer();
            return byteBuffer.getData();
        }
        return null;
    }
    
    protected boolean SaveImage(String path, BufferedImage stegImage){
        try {
            File f = new File(path + File.separatorChar + "stegImage.png");
            ImageIO.write(stegImage, "png", f);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Steganographer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    protected byte[] splitBits(byte val){
        byte[] bits = new byte[8];
        for(int i = 0; i < 8; i++){
            bits[i] = (byte)((val >>> (7 - i)) & 0x01);
        }
        return bits;
    }
    
    /**
     * Splits the size of the message into bytes and outputs them as an array
     * @param size
     * @return 
     */
    protected byte[] splitSize(int size){
        byte[] bytes = new byte[4];
        for(int i = 0; i < 4; i++){
            bytes[i] = (byte)(size >>> ((8 * (3 - i)) & 0xff));
        } 
        return bytes;
    }
    
}
