package com.github.jouwee.tcc_projeto;

import java.io.File;
import java.util.Random;
import java.util.function.BiFunction;
import javax.imageio.ImageIO;
import org.paim.commons.Image;
import org.paim.commons.ImageConverter;
import org.paim.commons.ImageFactory;
import org.paim.pdi.ThresholdProcess;
import visnode.pdi.process.DilationProcess;
import visnode.pdi.process.ErosionProcess;
import visnode.pdi.process.InvertColorProcess;
import visnode.pdi.process.SobelProcess;

/**
 * Class for comparing two images
 */
public class ImageComparer {

    public static void main(String[] args) throws Exception {
     
        BiFunction<Image, Image, Double> comparer = (i, i2) -> myCompare2(i, i2);
//        BiFunction<Image, Image, Double> comparer = (i, i2) -> pixelCompare(i, i2);
        
        Image input = ImageLoader.input("26");
        Image expected = ImageLoader.labeled("26");
        Image pureBlack = ImageFactory.buildEmptyImage(expected);
        Image pureWhite = ImageFactory.buildEmptyImage(expected);
        Image pureNoise = ImageFactory.buildEmptyImage(expected);
        Image matchWithNoise = new Image(expected);
        Image reduced = new Image(expected);
        Image amplified = new Image(expected);
        
        for (int i = 0; i < 4; i++) {
            DilationProcess dil = new DilationProcess(amplified);
            ErosionProcess ero = new ErosionProcess(reduced);
            dil.process();
            ero.process();
            amplified = dil.getImage();
            reduced = ero.getImage();
        }
        
        SobelProcess sob = new SobelProcess(input);
        sob.process();
        ThresholdProcess tsob = new ThresholdProcess(sob.getImage(), 40);
        tsob.process();
        Image sobel = tsob.getOutput();
        
        ThresholdProcess t = new ThresholdProcess(input, 128);
        t.process();
        InvertColorProcess i = new InvertColorProcess(t.getOutput());
        i.process();
        Image thresholded = i.getImage();
        Random r = new Random(0);
        for (int x = 0; x < expected.getWidth(); x++) {
            for (int y = 0; y < expected.getHeight(); y++) {
                pureWhite.set(0, x, y, 255);
                pureNoise.set(0, x, y, r.nextBoolean() ? 0 : 255);
                if (r.nextDouble() < 0.05) matchWithNoise.set(0, x, y, r.nextBoolean() ? 0 : 255);
                pureWhite.set(1, x, y, 255);
                pureNoise.set(1, x, y, r.nextBoolean() ? 0 : 255);
                if (r.nextDouble() < 0.05) matchWithNoise.set(1, x, y, r.nextBoolean() ? 0 : 255);
                pureWhite.set(2, x, y, 255);
                pureNoise.set(2, x, y, r.nextBoolean() ? 0 : 255);
                if (r.nextDouble() < 0.05) matchWithNoise.set(2, x, y, r.nextBoolean() ? 0 : 255);
            }
        }
        /*
        ImageIO.write(ImageConverter.toBufferedImage(input), "png", new File("c:\\users\\pichau\\desktop\\input.png"));
        ImageIO.write(ImageConverter.toBufferedImage(expected), "png", new File("c:\\users\\pichau\\desktop\\expected.png"));
        ImageIO.write(ImageConverter.toBufferedImage(pureBlack), "png", new File("c:\\users\\pichau\\desktop\\pureBlack.png"));
        ImageIO.write(ImageConverter.toBufferedImage(pureWhite), "png", new File("c:\\users\\pichau\\desktop\\pureWhite.png"));
        ImageIO.write(ImageConverter.toBufferedImage(pureNoise), "png", new File("c:\\users\\pichau\\desktop\\pureNoise.png"));
        ImageIO.write(ImageConverter.toBufferedImage(matchWithNoise), "png", new File("c:\\users\\pichau\\desktop\\matchWithNoise.png"));
        ImageIO.write(ImageConverter.toBufferedImage(reduced), "png", new File("c:\\users\\pichau\\desktop\\reduced.png"));
        ImageIO.write(ImageConverter.toBufferedImage(amplified), "png", new File("c:\\users\\pichau\\desktop\\amplified.png"));
        ImageIO.write(ImageConverter.toBufferedImage(thresholded), "png", new File("c:\\users\\pichau\\desktop\\thresholded.png"));
        ImageIO.write(ImageConverter.toBufferedImage(sobel), "png", new File("c:\\users\\pichau\\desktop\\sobel.png"));
        */
        System.out.println("Perfect match: " + comparer.apply(expected, expected));
        System.out.println("Perfect with noise: " + comparer.apply(matchWithNoise, expected));
        System.out.println("Perfect reduced: " + comparer.apply(reduced, expected));
        System.out.println("Perfect amplified: " + comparer.apply(amplified, expected));
        System.out.println("Thresholded: " + comparer.apply(thresholded, expected));
        System.out.println("Sobel: " + comparer.apply(sobel, expected));
        System.out.println("Pure black: " + comparer.apply(pureBlack, expected));
        System.out.println("Pure white: " + comparer.apply(pureWhite, expected));
        System.out.println("Pure noise: " + comparer.apply(pureNoise, expected));
        
        
    }    

    /**
     * Compares two images
     *
     * @param result
     * @param expected
     * @return ImageCompareResult
     */
    public ImageCompareResult compare(Image result, Image expected) {
        double r = myCompare2(result, expected);
//        if (r > 0) {
//            r = Math.abs(r - 0.5) * 2;
//        }
        return new ImageCompareResult(r);
    }
    
    /**
     * Compares two images
     *
     * @param result
     * @param expected
     * @return ImageCompareResult
     */
    private static double pixelCompare(Image result, Image expected) {
        if (result.getWidth() != expected.getWidth() || result.getHeight() != expected.getHeight()) {
            return 0d;
        }
        double correct = 0;
        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                boolean r = result.get(0, x, y) > 0;
                boolean e = expected.get(0, x, y) > 0;
                if (r == e) {
                    correct++;
                }
            }
        }
        correct = (correct / (result.getWidth() * result.getHeight()));
        return correct;
    }
    
    private static double myCompare(Image result, Image expected) {
        if (result.getWidth() != expected.getWidth() || result.getHeight() != expected.getHeight()) {
            System.out.println("\tFAIL "+result.getWidth() +' '+ expected.getWidth() +'x'+  result.getHeight() +' '+ expected.getHeight());
            return 0d;
        }
        int perimeterExpected = 0;
        int perimeterResult = 0;
        int edgeCorrect = 0;
        int edgeTotal = 0;
        double correct = 0;
        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                boolean r = result.get(0, x, y) > 0;
                boolean e = expected.get(0, x, y) > 0;
                if (r == e) {
                    correct++;
                }
                boolean edgeE = isEdge(expected, x, y);
                boolean edgeR = isEdge(result, x, y);
                if (edgeE) {
                    perimeterExpected++;
                }
                if (edgeR) {
                    perimeterResult++;
                }
                if (edgeE) {
                    if (edgeR) {
                        edgeCorrect++;
                    }
                    edgeTotal++;
                }
            }
        }
        double perimeterRatio = 1 - Math.min(Math.abs(perimeterExpected - perimeterResult) / perimeterExpected, 1);
        double pixelRatio = (correct / (result.getWidth() * result.getHeight()));
        double edgeRatio = (double) edgeCorrect / edgeTotal;
        double r = pixelRatio * 0.50 + edgeRatio * 0.25 + sorensenDiceCompare(result, expected) * 0.25;
        return r * r;
    }
    
    private static double myCompare2(Image result, Image expected) {
        if (result.getWidth() != expected.getWidth() || result.getHeight() != expected.getHeight()) {
            System.out.println("\tFAIL "+result.getWidth() +' '+ expected.getWidth() +'x'+  result.getHeight() +' '+ expected.getHeight());
            return 0d;
        }
        double tp = 0;
        double tn = 0;
        double fn = 0;
        double fp = 0;
        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                boolean r = result.get(0, x, y) > 0;
                boolean e = expected.get(0, x, y) > 0;
                if (r && e) {
                    tp++;
                }
                if (!r && !e) {
                    tn++;
                }
                if (e && !r) {
                    fn++;
                }                
                if (!e && r) {
                    fp++;
                }
            }
        }
        double sor = (2 * tp) / ( 2 * tp + fn + fp);
        double isor = (2 * tn) / ( 2 * tn + fn + fp);
        return Math.min(sor, isor);
    }
    
    /**
     * Compares two images
     *
     * @param result
     * @param expected
     * @return ImageCompareResult
     */
    private static double pixelCompareWithWeightedBorders(Image result, Image expected) {
        if (result.getWidth() != expected.getWidth() || result.getHeight() != expected.getHeight()) {
            return 0;
        }
        double total = 0;
        double correct = 0;
        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                boolean r = result.get(0, x, y) > 0;
                boolean e = expected.get(0, x, y) > 0;
                int weight = 1;
                if (isEdge(expected, x, y)) {
                    weight = 50;
                }
                if (r == e) {
                    correct += weight;
                }
                total += weight;
            }
        }
        correct = (correct / total);
        return correct;
    }
    private static boolean isEdge(Image expected, int x, int y) {
        boolean c = expected.get(0, x, y) >= 1;
        if (x > 0) {
            if (expected.get(0, x-1, y) >= 1 != c) {
                return true;
            }
        }
        if (x < expected.getWidth() - 1) {
            if (expected.get(0, x+1, y) >= 1 != c) {
                return true;
            }
        }
        if (y > 0) {
            if (expected.get(0, x, y-1) >= 1 != c) {
                return true;
            }
        }
        if (y < expected.getHeight()- 1) {
            if (expected.get(0, x, y+1) >= 1 != c) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Compares two images
     *
     * @param result
     * @param expected
     * @return ImageCompareResult
     */
    private static double sorensenDiceCompare(Image result, Image expected) {
        if (result.getWidth() != expected.getWidth() || result.getHeight() != expected.getHeight()) {
            return 0;
        }
        double tp = 0;
        double fp = 0;
        double fn = 0;
        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                boolean r = result.get(0, x, y) > 0;
                boolean e = expected.get(0, x, y) > 0;
                
                if (r && e) {
                    tp++;
                }
                if (e && !r) {
                    fn++;
                }                
                if (!e && r) {
                    fp++;
                }
            }
        }
        return (2 * tp) / ( 2 * tp + fn + fp);
    }

}
