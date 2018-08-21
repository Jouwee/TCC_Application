package com.github.jouwee.tcc_projeto;

import org.paim.commons.Color;
import org.paim.commons.Point;
import org.paim.pdi.FloodFillProcess;
import visnode.application.NodeNetwork;
import visnode.commons.Threshold;
import visnode.executor.EditNodeDecorator;
import visnode.executor.Node;
import visnode.executor.OutputNode;
import visnode.executor.ProcessNode;
import visnode.pdi.process.BrightnessProcess;
import visnode.pdi.process.ContrastProcess;
import visnode.pdi.process.GaussianBlurProcess;
import visnode.pdi.process.InputProcess;
import visnode.pdi.process.SnakeProcess;
import visnode.pdi.process.ThresholdProcess;
import visnode.pdi.process.WeightedGrayscaleProcess;

/**
 * Conversor entre cromossomos e a rede
 */
public class ChromossomeNetworkConverter {

    /** Exporta para o usuário ou só para simulação */
    private final boolean forUser;
    int x;
    
    public ChromossomeNetworkConverter() {
        this(false);
    }
    
    public ChromossomeNetworkConverter(boolean forUser) {
        this.forUser = forUser;
    }
    
    /**
     * Converte um gene em uma rede de nodos
     * 
     * @param chromossome
     * @return NodeNetwork
     */
    public NodeNetwork convert(Chromossome chromossome) {
        NodeNetwork network = new NodeNetwork();
        EditNodeDecorator last = null;
        if (forUser) {
            last = createNode(InputProcess.class, last);
            network.add(last);
        }
        boolean binary = false;
        Gene[] genes = chromossome.getGenes();
        for (int i = 0; i < genes.length;) {
            ProcessTypeGene pgene = (ProcessTypeGene) genes[i++];
            NumericGene[] params = new NumericGene[5];
            for (int j = 0; j < params.length; j++) {
                params[j] = (NumericGene) genes[i++];
            }
            Class c = pgene.value();
            if (c == null) {
                break;
            }
            if (c == ThresholdProcess.class || c == SnakeProcess.class) {
                binary = true;
            }
            EditNodeDecorator node = createProcess(c, last, params);
            network.add(node);
            last = node;
        }
        if (!binary) {
            last = createNode(ThresholdProcess.class, last);
            network.add(last);
        }
        if (forUser) {
            System.out.println("output");
            last = createNode(new OutputNode(), last);
            network.add(last);
        }
        return network;
    }
    
    public EditNodeDecorator createProcess(Class c, EditNodeDecorator last, NumericGene... params) {
        EditNodeDecorator node = createNode(c, last);
        
        if (c == ThresholdProcess.class) {
            node.setInput("threshold", new Threshold((int) (params[0].value() * 256)));
        }
        
        if (c == BrightnessProcess.class) {
            node.setInput("brightness", (int) (params[0].value() * 512 - 256));
        }
        
        if (c == ContrastProcess.class) {
            node.setInput("contrast", params[0].value() * 3);
        }
        
        if (c == GaussianBlurProcess.class) {
            node.setInput("sigma", params[0].value() * 3);
            node.setInput("maskSize", (int)(params[1].value() * 4 + 1) * 2 + 1);
        }
        
        if (c == WeightedGrayscaleProcess.class) {
            node.setInput("redWeight", params[0].value());
            node.setInput("greenWeight", params[1].value());
            node.setInput("blueWeight", params[2].value());
        }
        
        if (c == FloodFillProcess.class) {
            node.setInput("seed", Point.CENTER);
            node.setInput("replacement", new Color((int)(params[0].value * 255)));
        }
        
        return node;
    }
    
    public EditNodeDecorator createNode(Class c, EditNodeDecorator last) {
        return createNode(new ProcessNode(c), last);
    }
    
    public EditNodeDecorator createNode(Node n, EditNodeDecorator last) {
        EditNodeDecorator node = new EditNodeDecorator(n);
        node.setPosition(new java.awt.Point(x, 0));
        x += 250;
        if (last != null) {
            if (n instanceof OutputNode) {
                node.addConnection("value", last, "image");
            } else {
                node.addConnection("image", last, "image");
            }
        }
        return node;
    }
    
}
