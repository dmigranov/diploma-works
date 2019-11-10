package ru.nsu.fit.g16201.migranov.controller;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Texture {
    private BufferedImage textureImage;

    private Random random = new Random();

    Texture(BufferedImage image)
    {
        this.textureImage = image;
    }

    int getColorAt(double u, double v)
    {
        //todo: по u v вычислять пиксель и возвращать его цвет (для этого надо знать u/vMin и u/vMax)
        return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat()).getRGB();
    }

}
