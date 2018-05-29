/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import com.google.common.util.concurrent.AtomicDouble;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
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
public class NetworkEvaluator {
    
    private final Image inputImage;
    private final Image expeceted;
    private final CompositeDisposable compositeSubscription;

    public NetworkEvaluator(Image inputImage, Image expeceted) {
        this.inputImage = inputImage;
        this.expeceted = expeceted;
        compositeSubscription = new CompositeDisposable();
    }
    
    
    
    public CompletableFuture<ImageCompareResult> evaluate(NodeNetwork network) {
        CompletableFuture<ImageCompareResult> future = new CompletableFuture<>();
        try {
            Iterator<EditNodeDecorator> iterator = new ArrayList<>(network.getNodes()).iterator();
            run(iterator, inputImage).thenAccept((img) -> {
                future.complete(new ImageComparer().compare(img, expeceted));
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
        return future;
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
        node.process((p) -> {});
        Observable<Image> obs = node.getOutput("image");
        compositeSubscription.add(obs.subscribe((x) -> {
            if (x == null || !(x instanceof Image)) {
                System.out.println("Invalid output: " + x);
                future.complete(ImageFactory.buildBinaryImage(1, 1));
                return;
            }
            Image img = (Image) x;
            if (iterator.hasNext()) {
                run(iterator, img).thenAccept((img2) -> future.complete(img2));
            } else {
                compositeSubscription.clear();
                future.complete(img);
            }
        }));
        return future;
    }
    
}
