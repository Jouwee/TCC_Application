/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import org.paim.commons.Image;
import org.paim.commons.ImageFactory;
import visnode.application.NodeNetwork;
import visnode.executor.EditNodeDecorator;
import visnode.executor.Node;
import visnode.executor.ProcessNode;

/**
 *
 * @author Pichau
 */
public class NetworkExecutor {

    private final CompositeDisposable compositeSubscription;
    
    public NetworkExecutor() {
        compositeSubscription = new CompositeDisposable();
    }

    public CompletableFuture<Image> run(NodeNetwork network, Image inputImage) {
        try {
            Iterator<EditNodeDecorator> iterator = new ArrayList<>(network.getNodes()).iterator();
            return run(iterator, inputImage);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(ImageFactory.buildEmptyImage());
    }
    
    public CompletableFuture<Image> run(Iterator<EditNodeDecorator> iterator, Image inputImage) {
        CompletableFuture<Image> future = new CompletableFuture<>();
        Node n = iterator.next().getDecorated();
        if (!(n instanceof ProcessNode)) {
            run(iterator, inputImage).thenAccept((img2) -> future.complete(img2));
            return future;
        }
        ProcessNode node = (ProcessNode) n;
        node.setInput("image", inputImage);
        node.process((p) -> {
            Observable<Image> obs = node.getOutput("image");
            compositeSubscription.add(obs.subscribe((x) -> {
                if (x == null || !(x instanceof Image)) {
                    System.out.println("Invalid output: " + x);
                    future.complete(ImageFactory.buildBinaryImage(1, 1));
                    return;
                }
                if (x.getWidth() == 1) {
                    System.out.println("Received invalid image (width <= 1) from " + node.getProcessType());
                }
                Image img = (Image) x;
                if (iterator.hasNext()) {
                    run(iterator, img).thenAccept((img2) -> future.complete(img2));
                } else {
                    compositeSubscription.clear();
                    future.complete(img);
                }
            }));
        });
        return future;
    }
    
}
