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
import visnode.application.NodeNetwork;

/**
 *
 * @author Pichau
 */
public class IndividualEvaluator {
    
    static ScheduledThreadPoolExecutor delayer = new ScheduledThreadPoolExecutor(16);
    
    public CompletableFuture<IndividualResult> evaluate(Chromossome chromossome) {
        try {
            CompletableFuture<Void>[] results = new CompletableFuture[ImageLoader.allInputs().length];
            AtomicDouble sum = new AtomicDouble();
            for (int i = 0; i < ImageLoader.allInputs().length; i++) {
                NodeNetwork network = new ChromossomeNetworkConverter().convert(chromossome);
                results[i] = new NetworkEvaluator(ImageLoader.allInputs()[i].get(), ImageLoader.allExpecteds()[i].get()).evaluate(network).thenAccept((res) -> {
                    sum.addAndGet(res.getCorrectPercentage());
                });
            }
            CompletableFuture<IndividualResult> averageResult = new CompletableFuture<>();
            CompletableFuture.allOf(results).thenAccept((v) -> {
                averageResult.complete(new IndividualResult(chromossome, sum.get() / ImageLoader.allInputs().length));
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
