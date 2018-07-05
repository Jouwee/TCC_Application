package com.github.jouwee.tcc_projeto;

import java.util.Random;
import java.util.function.BiFunction;
import org.paim.commons.Image;
import org.paim.commons.ImageFactory;
import org.paim.pdi.ThresholdProcess;
import visnode.pdi.process.DilationProcess;
import visnode.pdi.process.ErosionProcess;

/**
 * Class for comparing two images
 */
public class ImageComparer {

    public static void main(String[] args) throws Exception {
     
        BiFunction<Image, Image, Double> comparer = (i, i2) -> myCompare(i, i2);
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
        
        ThresholdProcess t = new ThresholdProcess(input, 128);
        t.process();
        Image thresholded = t.getOutput();
        Random r = new Random(0);
        for (int x = 0; x < expected.getWidth(); x++) {
            for (int y = 0; y < expected.getHeight(); y++) {
                pureWhite.set(0, x, y, 1);
                pureNoise.set(0, x, y, r.nextBoolean() ? 0 : 1);
                if (r.nextDouble() < 0.05) matchWithNoise.set(0, x, y, r.nextBoolean() ? 0 : 1);
            }
        }
        
        System.out.println("Perfect match: " + comparer.apply(expected, expected));
        System.out.println("Perfect with noise: " + comparer.apply(matchWithNoise, expected));
        System.out.println("Perfect reduced: " + comparer.apply(reduced, expected));
        System.out.println("Perfect amplified: " + comparer.apply(amplified, expected));
        System.out.println("Thresholded: " + comparer.apply(thresholded, expected));
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
        double r = myCompare(result, expected);
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
        return pixelRatio * 0.50 + edgeRatio * 0.25 + perimeterRatio * 0.25;
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
