/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import org.paim.commons.BinaryImage;
import org.paim.commons.Image;
import org.paim.commons.ImageConverter;

/**
 *
 * @author Pichau
 */
public class Desenhador extends JFrame {

    Image input;
    BinaryImage labeled;
    float alpha;
    int size = 3;
    int zoom = 1;
    
    public static void main(String[] args) throws Exception {
        new Desenhador().setVisible(true);
    }

    public Desenhador() throws Exception {
        super();
        input = ImageLoader.input("1");
        labeled = ImageLoader.labeled("1");
        alpha = 0.5f;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        ImageCoisa coisa = new ImageCoisa();
        getContentPane().setLayout(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] opcoes = new String[ImageLoader.allExpecteds().length];
        for (int i = 0; i < opcoes.length; i++) {
            opcoes[i] = String.valueOf(i + 1);
        }
        JComboBox<String> combo = new JComboBox<>(opcoes);
        combo.addItemListener((ItemEvent e) -> {
            try {
                input = ImageLoader.input(combo.getSelectedItem().toString());
                labeled = ImageLoader.labeled(combo.getSelectedItem().toString());
                coisa.repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        top.add(combo);
        JSlider slider = new JSlider(0, 100, 50);
        slider.addChangeListener((e) -> {
            alpha = (float) slider.getValue() / 100f;
            coisa.repaint();
        });
        top.add(slider);
        JSlider slider2 = new JSlider(1, 10, 2);
        slider2.addChangeListener((e) -> {
            size = slider2.getValue() * 2 - 1;
            coisa.repaint();
        });
        top.add(slider2);
        top.add(new JButton(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ImageIO.write(ImageConverter.toBufferedImage(labeled), "png", new File("D:\\Projects\\TCC_Application\\src\\main\\resources\\Test_Labels\\" + combo.getSelectedItem() + "_mod.png"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }));
        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(coisa));
        
        
    }
    
    public class ImageCoisa extends JComponent {

        public ImageCoisa() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    draw(e);
                }
            });
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    draw(e);
                }
            });
            addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    zoom = Math.max(1, zoom  - e.getWheelRotation());
                    repaint();
                }
            });
        }
        
        public void draw(MouseEvent e) {
            int px = e.getPoint().x / zoom;
            int py = e.getPoint().y / zoom;
            int half = size / 2;
            for(int x = px - half; x < px + half; x++) {
                for(int y = py - half; y < py + half; y++) {
                    int fx = Math.max(0, Math.min(input.getWidth() - 1, x));
                    int fy = Math.max(0, Math.min(input.getHeight() - 1, y));
                    labeled.set(fx, fy, !e.isShiftDown());
                }
            }
            repaint();
        }

        @Override
        public Dimension getSize() {
            return new Dimension((int)(labeled.getWidth() * zoom), labeled.getHeight() * zoom);
        }
        
        
        
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.scale(zoom, zoom);
            BufferedImage bInput = ImageConverter.toBufferedImage(input);
            BufferedImage bLabeled = ImageConverter.toBufferedImage(labeled);
            g2d.drawImage(bInput, 0, 0, null);
            int rule = AlphaComposite.SRC_OVER;
            Composite comp = AlphaComposite.getInstance(rule , alpha);
            g2d.setComposite(comp);
            g2d.drawImage(bLabeled, 0, 0, null);
        }
        
    }
    
    
    
}
