/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import org.paim.commons.BinaryImage;
import org.paim.commons.Image;
import org.paim.commons.ImageFactory;

/**
 *
 * @author Pichau
 */
public class ImageLoader {
        
    private static Supplier<Image>[] inputImage;
    private static Supplier<Image>[] expected;
    
    static {
        try {
            int s = 50;
            inputImage = new Supplier[s];
            expected = new Supplier[s];
            for (int i = 0; i < s; i++) {
                inputImage[i] = new MemorySafeLazyLoader("input", String.valueOf(i+1));
                expected[i] = new MemorySafeLazyLoader("labeled", String.valueOf(i+1));
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
    
    public static Supplier<Image>[] allInputs() {
        return inputImage;
    }
    
    public static Supplier<Image>[] allExpecteds() {
        return expected;
    }
    
    public static BinaryImage labeled(String name) throws IOException {
        Image image = ImageFactory.buildRGBImage(labeledBuffered(name));
        BinaryImage newImage = ImageFactory.buildBinaryImage(image.getWidth(), image.getHeight());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                newImage.set(x, y, image.get(1, x, y) == 255 || image.get(2, x, y) == 255);
            }
        }
        return newImage;
    }
    
    public static BufferedImage labeledBuffered(String name) throws IOException {
        try {
            return ImageIO.read(ImageComparer.class.getResource("/Test_Labels/" + name + "_mod.png"));
        } catch (Exception e) {
        }
        return ImageIO.read(ImageComparer.class.getResource("/Test_Labels/" + name + ".bmp"));
    }
    
    private static class MemorySafeLazyLoader implements Supplier<Image> {

        String type;
        String name;
        WeakReference<Image> weak;
        
        public MemorySafeLazyLoader(String type, String name) {
            this.type = type;
            this.name = name;
            this.weak = new WeakReference<>(null);
        }
        
        @Override
        public Image get() {
            try {
                Image image = weak.get();
                if (image == null) {
                    image = load();
                    weak = new WeakReference<>(image);
                }
                return image;
            } catch (Exception e) {
                return null;
            }
        }
        
        public Image load() throws IOException {
            if (type.equals("input")) {
                return ImageLoader.input(name);
            }
            return ImageLoader.labeled(name);
        }
        
    }
    
}
