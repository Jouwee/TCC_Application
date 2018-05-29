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
        if (result.getWidth() != expected.getWidth() || result.getHeight() != expected.getHeight()) {
            return new ImageCompareResult(0);
        }
        double correct = 0;
        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                boolean r = result.get(0, x, y) > 0;
                boolean e = !(expected.get(0, x, y) == 255 && expected.get(1, x, y) == 0 && expected.get(2, x, y) == 0);
                if (r == e) {
                    correct++;
                }
            }
        }
        correct = (correct / (result.getWidth() * result.getHeight()));
        return new ImageCompareResult(correct);

    }

}
