/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.imageio.ImageIO;
import org.paim.commons.Image;
import org.paim.commons.ImageFactory;
import visnode.application.NodeNetwork;

/**
 *
 * @author Pichau
 */
public class IndividualEvaluator {
    
    static ScheduledThreadPoolExecutor delayer = new ScheduledThreadPoolExecutor(16);
    
    static Image[] inputImage;
    static Image[] expected;
    static {
        try {
            inputImage = new Image[] {
                /*ImageFactory.buildRGBImage(ImageIO.read(ImageComparer.class.getResource("/Test_RGB/26.bmp"))),
                ImageFactory.buildRGBImage(ImageIO.read(ImageComparer.class.getResource("/Test_RGB/45.bmp"))),
                ImageFactory.buildRGBImage(ImageIO.read(ImageComparer.class.getResource("/Test_RGB/49.bmp"))),*/
            };
            expected = new Image[] {
                /*ImageFactory.buildRGBImage(ImageIO.read(ImageComparer.class.getResource("/Test_Labels/26.bmp"))),
                ImageFactory.buildRGBImage(ImageIO.read(ImageComparer.class.getResource("/Test_Labels/45.bmp"))),
                ImageFactory.buildRGBImage(ImageIO.read(ImageComparer.class.getResource("/Test_Labels/49.bmp"))),*/
            };
        } catch(Exception e) {
            e.printStackTrace();
        }
    }    
    
    public CompletableFuture<IndividualResult> evaluate(Chromossome chromossome) {
        try {
            CompletableFuture<ImageCompareResult>[] results = new CompletableFuture[inputImage.length];
            for (int i = 0; i < inputImage.length; i++) {
                NodeNetwork network = new ChromossomeNetworkConverter().convert(chromossome);
                results[i] = new NetworkEvaluator(inputImage[i], expected[i]).evaluate(network);
            }
            AtomicDouble sum = new AtomicDouble();
            for (CompletableFuture<ImageCompareResult> result : results) {
                result.acceptEither(timeoutAfter(1, TimeUnit.SECONDS), (res) -> {
                    sum.addAndGet(res.getCorrectPercentage());
                });
            }
            CompletableFuture<IndividualResult> averageResult = new CompletableFuture<>();
            try {
                Thread.sleep(300);
            } catch (Exception e) {
                
            }
            CompletableFuture.allOf(results).thenAccept((v) -> {
                averageResult.complete(new IndividualResult(Math.random()));
            });
            return averageResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CompletableFuture<>();
    }
    
    public <T> CompletableFuture<T> timeoutAfter(long timeout, TimeUnit unit) {
        CompletableFuture<T> result = new CompletableFuture<>();
        delayer.schedule(() -> result.completeExceptionally(new TimeoutException()), timeout, unit);
        return result;
    }
    
}
