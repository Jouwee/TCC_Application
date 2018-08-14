/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import java.util.concurrent.CompletableFuture;
import org.paim.commons.Image;
import visnode.application.NodeNetwork;

/**
 *
 * @author Pichau
 */
public class NetworkEvaluator {
    
    private final Image inputImage;
    private final Image expeceted;

    public NetworkEvaluator(Image inputImage, Image expeceted) {
        this.inputImage = inputImage;
        this.expeceted = expeceted;
    }
    
    public CompletableFuture<ImageCompareResult> evaluate(NodeNetwork network) {
        CompletableFuture<ImageCompareResult> future = new CompletableFuture<>();
        try {
            new NetworkExecutor().run(network, inputImage).thenAccept((img) -> {
                future.complete(new ImageComparer().compare(img, expeceted));
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
        return future;
    }
    
}
