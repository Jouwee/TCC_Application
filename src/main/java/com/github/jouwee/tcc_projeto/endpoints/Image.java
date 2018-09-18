package com.github.jouwee.tcc_projeto.endpoints;

import com.github.jouwee.tcc_projeto.Chromossome;
import com.github.jouwee.tcc_projeto.ChromossomeNetworkConverter;
import com.github.jouwee.tcc_projeto.ImageLoader;
import com.github.jouwee.tcc_projeto.JsonHelper;
import com.github.jouwee.tcc_projeto.Message;
import com.github.jouwee.tcc_projeto.NetworkExecutor;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.paim.commons.ImageConverter;
import visnode.application.NodeNetwork;

public class Image extends HttpServlet {

    Pattern URI_MATCHING = Pattern.compile("/rest/image/(.*?)/(.*?)$");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String chromossome = URLDecoder.decode(request.getQueryString(), "UTF-8");
        Matcher matcher = URI_MATCHING.matcher(request.getRequestURI());
        if (matcher.find()) {
            out.println(getImage(matcher.group(1), matcher.group(2), chromossome));
            response.setStatus(200);
        } else {
            response.setStatus(400);
        }
        out.close();
    }

 
    public String getImage(String group, String id, String chromossome) {
        Message result;
        try {
            BufferedImage image = null;
            if (group.equals("original")) {
                image = ImageLoader.inputBuffered(id);
            }
            if (group.equals("processed")) {
                Chromossome chr = JsonHelper.get().fromJson(chromossome, Chromossome.class);
                NodeNetwork network = new ChromossomeNetworkConverter().convert(chr);
                CompletableFuture<org.paim.commons.Image> future = new NetworkExecutor().run(network, ImageLoader.input(id));
                future.join();
                org.paim.commons.Image img = future.get();
                image = ImageConverter.toBufferedImage(img);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            result = new Message("image", new String(Base64.getEncoder().encode(bytes), "UTF-8"));
        } catch (Throwable e) {
            result = new Message("error", e.getMessage());
        }
        return JsonHelper.get().toJson(result);
    }
    
}
