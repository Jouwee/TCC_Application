package com.github.jouwee.tcc_projeto;

import org.paim.commons.Image;

/**
 * Class for comparing two images
 */
public class ImageComparer {


    /**
     * Compares two images
     *
     * @param result
     * @param expected
     * @return ImageCompareResult
     */
    public ImageCompareResult compare(Image result, Image expected) {
        double r = pixelCompare(result, expected);
        if (r > 0) {
            r = Math.abs(r - 0.5) * 2;
        }
        return new ImageCompareResult(r);
    }
    
    /**
     * Compares two images
     *
     * @param result
     * @param expected
     * @return ImageCompareResult
     */
    private double pixelCompare(Image result, Image expected) {
        if (result.getWidth() != expected.getWidth() || result.getHeight() != expected.getHeight()) {
            return 0;
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
    
    /**
     * Compares two images
     *
     * @param result
     * @param expected
     * @return ImageCompareResult
     */
    private double pixelCompareWithWeightedBorders(Image result, Image expected) {
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
    private boolean isEdge(Image expected, int x, int y) {
        boolean c = expected.get(0, x, y) > 1;
        if (x > 0) {
            if (expected.get(0, x-1, y) > 1 != c) {
                return true;
            }
        }
        if (x < expected.getWidth() - 1) {
            if (expected.get(0, x+1, y) > 1 != c) {
                return true;
            }
        }
        if (y > 0) {
            if (expected.get(0, x, y-1) > 1 != c) {
                return true;
            }
        }
        if (y < expected.getHeight()- 1) {
            if (expected.get(0, x, y+1) > 1 != c) {
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
    private double sorensenDiceCompare(Image result, Image expected) {
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
