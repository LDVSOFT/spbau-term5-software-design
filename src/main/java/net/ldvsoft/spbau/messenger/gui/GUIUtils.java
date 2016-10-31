package net.ldvsoft.spbau.messenger.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * GUI utilities
 */
final class GUIUtils {
    private static final Insets INSETS = new Insets(3, 3, 3, 3);

    private GUIUtils() {
    }

    static void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent,
                "Error: " + message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    static GridBagConstraints getConstraints(int y, int x) {
        return getConstraints(y, x, false);
    }

    static GridBagConstraints getConstraints(int y, int x, boolean fillx) {
        return getConstraints(y, x, fillx, 1);
    }

    static GridBagConstraints getConstraints(int y, int x, int spanx) {
        return getConstraints(y, x, spanx > 1, spanx);
    }

    static GridBagConstraints getConstraints(int y, int x, boolean fillx, int spanx) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = y;
        constraints.gridx = x;
        constraints.gridwidth = spanx;
        constraints.anchor = GridBagConstraints.LINE_START;
        if (fillx) {
            constraints.weightx = 0.5;
            constraints.fill = GridBagConstraints.HORIZONTAL;
        }
        constraints.insets = INSETS;
        return constraints;
    }

    static Border getBorder(String name) {
        return BorderFactory.createTitledBorder(name);
    }
}
