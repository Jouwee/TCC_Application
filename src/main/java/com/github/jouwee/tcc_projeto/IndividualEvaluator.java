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
import org.paim.commons.Image;
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
                ImageLoader.input("26"),
//                ImageLoader.input("45"),
                ImageLoader.input("49")
            };
            expected = new Image[] {
                ImageLoader.labeled("26"),
//                ImageLoader.labeled("45"),
                ImageLoader.labeled("49")
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
            CompletableFuture.allOf(results).thenAccept((v) -> {
                averageResult.complete(new IndividualResult(chromossome, sum.get() / inputImage.length));
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
