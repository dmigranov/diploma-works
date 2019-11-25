package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

public class LabelTextField extends JPanel {
    LabelTextField(String s, JTextField textField, KeyListener listener)
    {
        textField.setPreferredSize(new Dimension(100, 30));
        JLabel label = new JLabel(s);
        add(label);
        add(textField);
        textField.addKeyListener(listener);
        setAlignmentX(LEFT_ALIGNMENT);
    }

}
