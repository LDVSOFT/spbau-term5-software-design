package net.ldvsoft.spbau.messenger.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Main class of GUI.
 * Just starts main frame.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            /* Just don't like how swing works by default, urgh */
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            UIManager.getLookAndFeelDefaults().put("defaultFont", new Font(Font.SERIF, Font.BOLD, 14));
            new StartFrame().setVisible(true);
        });
    }
}
