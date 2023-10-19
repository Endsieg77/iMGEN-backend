package com.example.server.imgen.Utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;
import lombok.Data;

@Data
public class ImagePadder {
    private Integer maxWidth;
    private Integer maxHeight;

    public ImagePadder(Integer maxWidth, Integer maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    public BufferedImage pad(BufferedImage image, Integer currentWidth, Integer currentHeight) {
            Integer padding       = (maxWidth - currentWidth) / 2;
            BufferedImage resized = Scalr.resize(image, Mode.FIT_EXACT, currentWidth, currentHeight);
            BufferedImage canvas  = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graph      = (Graphics2D) canvas.getGraphics();

            graph.setColor(new Color(0, 0, 0, 0));
            graph.drawRect(0, 0, maxWidth, maxHeight);
            graph.drawImage(resized, padding, maxHeight - currentHeight, null);
            graph.dispose();

            return canvas;
    }

    public BufferedImage combine(BufferedImage background, BufferedImage foreground)
    {
        BufferedImage combined = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) combined.getGraphics();
        g.drawImage(background, 0, 0, null);
        g.drawImage(foreground, 0, 0, null);

        return combined;
    }

    public BufferedImage combine(BufferedImage background, BufferedImage foreground, int x, int y)
    {
        BufferedImage combined = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) combined.getGraphics();
        g.drawImage(background, x, y, null);
        g.drawImage(foreground, x, y, null);

        return combined;
    }

    public BufferedImage combinedTo(BufferedImage background, BufferedImage foreground)
    {
        BufferedImage combined = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) combined.getGraphics();
        g.drawImage(background, 0, 0, null);
        g.drawImage(foreground, 0, 0, null);

        return combined;
    }

    public BufferedImage combinedTo(BufferedImage background, BufferedImage foreground, int x, int y)
    {
        BufferedImage combined = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) combined.getGraphics();
        g.drawImage(background, 0, 0, null);
        g.drawImage(foreground, x, y, null);

        return combined;
    }
}
