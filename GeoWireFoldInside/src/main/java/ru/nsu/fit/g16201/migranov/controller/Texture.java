package ru.nsu.fit.g16201.migranov.controller;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Texture {
    private BufferedImage textureImage;
    private int width;
    private int height;

    private Random random = new Random();

    Texture(BufferedImage image)
    {
        this.textureImage = image;
        width = textureImage.getWidth();
        height = textureImage.getHeight();
    }

    int getColorAt(double u, double v)
    {
        if (u >= 360) {
            do u -= 360; while (u >= 360);
        } else if (u < 0) {
            do u += 360; while (u < 0);
        }
        if (v >= 180) {
            do v -= 360; while (v >= 180);
        } else if (v < -180) {
            do v += 360; while (v < -180);
        }

        if (v >= 90) {
            v = 180 - v;
        } else if (v < -90) {
            v = -180 - v;
        }
        // iu: [0..360),  iv: [-90..90) - сферические координаты

        int picX = (int)((u/360) * width);
        if(picX > width - 1)
            picX = width - 1;
        int picY = (int)(((v + 90)/180) * height);
        if(picY > height - 1)
            picY = height - 1;
        //todo: exception

        return textureImage.getRGB(picX, picY);
    }

}
