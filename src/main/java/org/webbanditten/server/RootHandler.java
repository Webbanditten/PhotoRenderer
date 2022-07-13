package org.webbanditten.server;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.alexdev.photorenderer.PhotoRenderer;
import org.alexdev.photorenderer.RenderOption;
import org.alexdev.photorenderer.palettes.GreyscalePalette;

import javax.imageio.ImageIO;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

public class RootHandler implements HttpHandler {

    @Override

    public void handle(HttpExchange he) throws IOException {
        var queryParameters = Utils.queryToMap(he.getRequestURI().getQuery());
        var renderOption =  RenderOption.SEPIA;
        switch(queryParameters.get("style")) {
            case "greyscale":
                renderOption = RenderOption.GREYSCALE;
                break;
            case "sepia":
                renderOption = RenderOption.SEPIA;
                break;
        }
        PhotoRenderer photoViewer = new PhotoRenderer(GreyscalePalette.getPalette(), renderOption);
        OutputStream outputStream = null;
        try {
            var requestBytes = he.getRequestBody().readAllBytes();

            System.out.println("REQUEST RECEIVED " + new Date());

            var createdPhoto = photoViewer.createImage(requestBytes);

            // Write it to memory
            ByteArrayOutputStream tmp = new ByteArrayOutputStream();
            ImageIO.write(createdPhoto, "png", tmp);
            tmp.close();

            he.getResponseHeaders().set("Content-Type", "image/png");
            he.sendResponseHeaders(200, tmp.size());

            outputStream = he.getResponseBody();
            ImageIO.write(createdPhoto, "png", outputStream);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
        }
    }
}
