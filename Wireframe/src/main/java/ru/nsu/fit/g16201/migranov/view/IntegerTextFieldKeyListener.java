package ru.nsu.fit.g16201.migranov.view;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class IntegerTextFieldKeyListener extends KeyAdapter {
    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
        char c = e.getKeyChar();
        if(!((c >= '0' && c <= '9') || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE))
        {
            e.consume();
        }
    }
}