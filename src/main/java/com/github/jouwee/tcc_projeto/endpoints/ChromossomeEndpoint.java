package com.github.jouwee.tcc_projeto.endpoints;

import com.github.jouwee.tcc_projeto.Chromossome;
import com.github.jouwee.tcc_projeto.ChromossomeNetworkConverter;
import com.github.jouwee.tcc_projeto.JsonHelper;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import visnode.application.NodeNetwork;
import visnode.application.parser.NodeNetworkParser;

public class ChromossomeEndpoint extends HttpServlet {

    Pattern URI_MATCHING = Pattern.compile("/rest/chromossome/(download)");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Matcher matcher = URI_MATCHING.matcher(request.getRequestURI());
        if (matcher.find()) {
            String chromossome = URLDecoder.decode(request.getQueryString(), "UTF-8");
            download(chromossome, response);
            response.setStatus(200);
        } else {
            response.setStatus(400);
        }
    }

    public void download(String json, HttpServletResponse response) {
        try {
            
            NodeNetwork network = new ChromossomeNetworkConverter(true).convert(JsonHelper.get().fromJson(json, Chromossome.class));
            NodeNetworkParser parser = new NodeNetworkParser();

            response.addHeader("Content-Disposition", "attachment; filename=Project.vnp");
            response.addHeader("Content-Type", "application/octet-stream");
            response.addHeader("Cache-Control", "private");
            response.addHeader("Pragma", "private");
            response.addHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");

            response.getOutputStream().write(parser.toJson(network).getBytes());
            response.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

}
