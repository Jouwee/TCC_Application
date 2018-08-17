/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.paim.commons.Image;
import org.paim.commons.ImageFactory;

/**
 *
 * @author Pichau
 */
public class ImageLoader {
        
    private static Image[] inputImage;
    private static Image[] expected;
    
    static {
        try {
            int s = 5; // 50
            inputImage = new Image[s];
            expected = new Image[s];
            for (int i = 0; i < s; i++) {
                inputImage[i] = ImageLoader.input(String.valueOf(i+1));
                expected[i] = ImageLoader.labeled(String.valueOf(i+1));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }   
    
    public static Image input(String name) throws IOException {
        return ImageFactory.buildRGBImage(inputBuffered(name));
    }
    
    public static BufferedImage inputBuffered(String name) throws IOException {
        return ImageIO.read(ImageComparer.class.getResource("/Test_RGB/" + name + ".bmp"));
    }
    
    public static Image[] allInputs() {
        return inputImage;
    }
    
    public static Image[] allExpecteds() {
        return expected;
    }
    
    public static Image labeled(String name) throws IOException {
        Image image = ImageFactory.buildRGBImage(labeledBuffered(name));
        Image newImage = ImageFactory.buildEmptyImage(image);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                newImage.set(0, x, y, !(image.get(0, x, y) == 255 && image.get(1, x, y) == 0 && image.get(2, x, y) == 0) ? 255 : 0);
                newImage.set(1, x, y, !(image.get(0, x, y) == 255 && image.get(1, x, y) == 0 && image.get(2, x, y) == 0) ? 255 : 0);
                newImage.set(2, x, y, !(image.get(0, x, y) == 255 && image.get(1, x, y) == 0 && image.get(2, x, y) == 0) ? 255 : 0);
            }
        }
        return newImage;
    }
    
    public static BufferedImage labeledBuffered(String name) throws IOException {
        return ImageIO.read(ImageComparer.class.getResource("/Test_Labels/" + name + ".bmp"));
    }
    
}
