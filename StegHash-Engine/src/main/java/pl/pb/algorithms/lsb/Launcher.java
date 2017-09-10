/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.pb.algorithms.lsb;

import java.nio.file.Paths;

/**
 *
 * @author Eric
 */
public class Launcher {
    public static void main(String[] args) {
        Steganographer test = new BadTextSteganographer();
        String imgPath = Paths.get("D:\\dyplom\\stegTests\\images\\test.png").toString();
        String msgPath = Paths.get("D:\\dyplom\\stegTests\\texts\\testMessage.txt").toString();
        String stegPath =  Paths.get("D:\\dyplom\\stegTests\\steganograms\\stegImage.png").toString();
        //test.ApplySteganography(imgPath, msgPath);
        test.ReadStegImage(stegPath);
    }
}
