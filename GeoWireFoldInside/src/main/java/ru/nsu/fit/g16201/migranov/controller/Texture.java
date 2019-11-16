package ru.nsu.fit.g16201.migranov.controller;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Texture {
    private BufferedImage textureImage;
    private int width;
    private int height;
    private static final double ANGLE_90 = Math.PI/2;
    private static final double ANGLE_180 = Math.PI;
    private static final double ANGLE_360 = Math.PI * 2;



    private Random random = new Random();

    Texture(BufferedImage image)
    {
        this.textureImage = image;
        width = textureImage.getWidth();
        height = textureImage.getHeight();
    }

    int getColorAt(double u, double v)
    {
        if (u >= ANGLE_360) {
            do u -= ANGLE_360; while (u >= ANGLE_360);
        } else if (u < 0) {
            do u += ANGLE_360; while (u < 0);
        }
        if (v >= ANGLE_180) {
            do v -= ANGLE_360; while (v >= ANGLE_180);
        } else if (v < -ANGLE_180) {
            do v += ANGLE_360; while (v < -ANGLE_180);
        }

        if (v >= ANGLE_90) {
            v = ANGLE_180 - v;
        } else if (v < -ANGLE_90) {
            v = -ANGLE_180 - v;
        }
        // iu: [0..360),  iv: [-90..90) - сферические координаты

        int picX = (int)((u/ANGLE_360) * width);
        if(picX > width - 1)
            picX = width - 1;
        int picY = (int)(((v + ANGLE_90)/ANGLE_180) * height);
        if(picY > height - 1)
            picY = height - 1;

        return textureImage.getRGB(picX, picY);
    }

}
