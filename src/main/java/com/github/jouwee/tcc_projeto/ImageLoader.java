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
    
    public static Image input(String name) throws IOException {
        return ImageFactory.buildRGBImage(inputBuffered(name));
    }
    
    public static BufferedImage inputBuffered(String name) throws IOException {
        return ImageIO.read(ImageComparer.class.getResource("/Test_RGB/" + name + ".bmp"));
    }
    
    public static Image labeled(String name) throws IOException {
        Image image = ImageFactory.buildRGBImage(labeledBuffered(name));
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.set(0, x, y, !(image.get(0, x, y) == 255 && image.get(1, x, y) == 0 && image.get(2, x, y) == 0) ? 1 : 0);
            }
        }
        return image;
    }
    
    public static BufferedImage labeledBuffered(String name) throws IOException {
        return ImageIO.read(ImageComparer.class.getResource("/Test_Labels/" + name + ".bmp"));
    }
    
}