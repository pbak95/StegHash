/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.pb.algorithms.lsb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eric
 */
public class BadTextSteganographer extends Steganographer{

    @Override
    protected void write(byte[] data, BufferedImage destination) {
        byte[] dest = LoadImageBytes(destination);
        //position index of the dest array
        int destPos = 0;
        
        //splits data.length into 4 bytes
        byte[] size = splitSize(data.length);
        
        //will write the size byte array into the lsb of the first 32 bytes of dest
        for(int idx = 0; idx < size.length; idx++){
            dest[destPos++] = size[idx];
        }
        
        //writes the data array to the lsb of the dest array starting after the 
        //first 32 bytes of the dest array
        for(int idx = 0; idx < data.length; idx++){
            dest[destPos++] = data[idx];
        }
    }

    @Override
    protected byte[] read(BufferedImage stegImage) {
        byte[] imageData = LoadImageBytes(stegImage);
        int size = 0;
        for(int i = 0; i < 4; i++){
            size +=  (imageData[i]);
            
        }
        byte[] textData = new byte[size];
        int imageIdx = 4;
        for( int textIdx = 0; textIdx < size; textIdx++){
            textData[textIdx] = imageData[imageIdx++];
        }
        return textData;
    }

    @Override
    public void ApplySteganography(String imageFilePath, String dataFilePath) {
        if(imageFilePath != null && dataFilePath != null){
            try {
                BufferedImage source = LoadImage_Copy(imageFilePath);
                byte[] data = Files.readAllBytes(new File(dataFilePath).toPath());
                write(data, source);
                String saveTo = imageFilePath.substring(0, imageFilePath.lastIndexOf(File.separatorChar));
                SaveImage(saveTo, source);
            } catch (IOException ex) {
                Logger.getLogger(TextSteganographer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void ReadStegImage(String filepath) {
        if(filepath != null ){
            BufferedImage source = LoadImage_Copy(filepath);
            byte[] messageData = read(source);
            String message = new String(messageData);
            System.out.println(message);
        }
    }
    
}
