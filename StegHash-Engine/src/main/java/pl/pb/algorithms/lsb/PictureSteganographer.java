/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.pb.algorithms.lsb;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Eric
 */
public class PictureSteganographer extends Steganographer{

    @Override
    protected void write(byte[] data, BufferedImage destination) {
        byte[] destImage = LoadImageBytes(destination);
        int destIdx = 0;
        
        if(destImage.length < data.length){
            System.out.println("Size of destination image is too small");
            System.out.println(String.format("Image size is: %d", destImage.length));
            System.out.println(String.format("Data size is: %d", data.length));
            return;
        }
        
        //write the size of the data array to destination
        int s = data.length;
        byte[] size = splitSize(data.length);
        for(int sizeIdx = 0; sizeIdx < size.length; sizeIdx++){
            byte[] bits = splitBits(size[sizeIdx]);
            for(int bitIdx = 0; bitIdx < bits.length; bitIdx++){
                destImage[destIdx] = (byte)(destImage[destIdx] & 0xfe);
                destImage[destIdx] += (byte)bits[bitIdx];
                destIdx++;
            }
        }
        
        
        //write data to destination
        for(int dataIdx = 0; dataIdx < data.length; dataIdx++){
            byte[] bits = splitBits(data[dataIdx]);
            for(int bitIdx = 0; bitIdx < bits.length; bitIdx++){
                destImage[destIdx] = (byte)(destImage[destIdx] & 0xfe);
                destImage[destIdx] += (byte)bits[bitIdx];
                destIdx++;
            }
        }
        System.out.println("Successfully hidden " + (data.length  + 4) + " bytes");
    }

    @Override
    protected byte[] read(BufferedImage stegImage) {
        byte[] stegData = LoadImageBytes(stegImage);
        int size = 0;
        for(int i = 0; i < 32; i++){
            size = ((size << 1) | (stegData[i] & 1));
            
        }
        byte[] imageData = new byte[size];
        int stegIdx = 32;
        for( int textIdx = 0; textIdx < size; textIdx++){
            for(int i = 0; i < 8; i++){
                imageData[textIdx] = (byte)((imageData[textIdx] << 1) | (stegData[stegIdx++] & 1)) ;
            }
        }
        return imageData;
    }

    @Override
    public void ApplySteganography(String imageFilePath, String dataFilePath) {
        BufferedImage dataImage = LoadImage_Copy(dataFilePath);
        
        BufferedImage sourceImage = LoadImage_Copy(imageFilePath);
        int width = dataImage.getWidth();
        int height = dataImage.getHeight();
        System.out.println( height + " x " + width);
        byte[] dataImageBytes = GetBytesOfImgToHide(dataImage);
        
        //create a new array to hold the height, width, and Image data
        byte[] data = new byte[ dataImageBytes.length + 8];
        
        //copy the height, width, and image data in to the new array
        byte[] hBits = splitSize(height);
        byte[] wBits = splitSize(width);
        System.arraycopy(hBits, 0, data, 0, 4);
        System.arraycopy(wBits, 0, data, 4, 4);
        System.arraycopy(dataImageBytes, 0, data, 8, dataImageBytes.length);
        
        //begin writing the data to the source image
        write(data, sourceImage);
        
        String saveTo = imageFilePath.substring(0, imageFilePath.lastIndexOf(File.separatorChar));
        SaveImage(saveTo, sourceImage);
    }

    @Override
    public void ReadStegImage(String filepath) {
        BufferedImage sourceImage = LoadImage_Copy(filepath);
        byte[] encodedData = read(sourceImage);
        
        int height = 0, width = 0;
        //read height and width from encodedData
        for(int i = 0; i < 4; i++){
            byte[] hBits = splitBits(encodedData[i]);
            byte[] wBits = splitBits(encodedData[i+4]);
            for(int bIdx = 0; bIdx < hBits.length; bIdx++){
                height = ((height << 1) | (hBits[bIdx]));
                width = ((width << 1) | (wBits[bIdx]));
            }
        }
        
        byte[] data = new byte[encodedData.length - 8];
        System.arraycopy(encodedData, 8, data, 0, data.length);
        
        BufferedImage image = BytesToImage(data, height, width);
        
        SaveUncoveredImage(filepath.substring(0, filepath.lastIndexOf("\\") + 1), image);
        
    }
    
    protected BufferedImage BytesToImage(byte[] imageData, int height, int width){
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            
            
            return image;
        } catch (IOException ex) {
            Logger.getLogger(PictureSteganographer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    protected byte[] GetBytesOfImgToHide(BufferedImage image){
        try {
            ByteArrayOutputStream oStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", oStream);
            oStream.flush();
            return oStream.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(PictureSteganographer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    protected boolean SaveUncoveredImage(String path, BufferedImage uncoveredImage){
        try {
            File f = new File(path + File.separatorChar + "uncovered.png");
            ImageIO.write(uncoveredImage, "png", f);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Steganographer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
}
