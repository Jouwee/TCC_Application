package com.github.jouwee.tcc_projeto.endpoints;

import com.github.jouwee.tcc_projeto.GeneticAlgorithmController;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

public class LoadAndSave extends HttpServlet {

    Pattern URI_MATCHING = Pattern.compile("/rest/simulation/(save|load|new)");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Matcher matcher = URI_MATCHING.matcher(request.getRequestURI());
        if (matcher.find()) {
            if (matcher.group(1).equals("new")) {
                GeneticAlgorithmController.get().initialize();
            } else {
                save(response);
            }
        } else {
            response.setStatus(400);
        }
    }

    public void save(HttpServletResponse response) throws IOException {
        response.addHeader("Content-Disposition", "attachment; filename=Model.model");
        response.addHeader("Content-Type", "application/octet-stream");
        response.addHeader("Content-Transfer-Encoding", "binary");
        response.addHeader("Accept-Ranges", "bytes");
        response.addHeader("Cache-Control", "private");
        response.addHeader("Pragma", "private");
        response.addHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");

        response.getOutputStream().write(GeneticAlgorithmController.get().save());
        response.getOutputStream().close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Matcher matcher = URI_MATCHING.matcher(request.getRequestURI());
        if (matcher.find()) {
            load(request.getInputStream());
            response.setStatus(200);
        } else {
            response.setStatus(400);
        }
    }

    public void load(InputStream stream) {
        try {
            System.out.println("loading 1...");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.out.println("loading 2...");
            IOUtils.copy(stream, baos);
            System.out.println("loading 3...");
            GeneticAlgorithmController.get().load(Base64.getDecoder().decode(baos.toByteArray()));
            System.out.println("loading done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
