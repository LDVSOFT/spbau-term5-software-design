package net.ldvsoft.spbau.messenger.gui;

import net.ldvsoft.spbau.messenger.Starter;
import net.ldvsoft.spbau.messenger.protocol.Connection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Main frame of GUI.
 * User selects connection settings and presses Start.
 */
class StartFrame extends JFrame {
    /**
     * This action is used in server-client radio buttons.
     */
    private class IsServerAction extends AbstractAction {
        boolean value;

        private IsServerAction(boolean value) {
            super();
            putValue(NAME, value ? "Server" : "Client");
            this.value = value;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            isServer = value;
            hostField.setEnabled(!isServer);
        }
    }

    private JTextField nameField;
    private JTextField hostField;
    private JTextField portField;
    private boolean isServer;

    /**
     * This action describes start button behaviour.
     */
    private Action startAction = new AbstractAction() {
        private static final String START = "Start";
        private static final String CONNECTING = "Connecting...";
        private static final String CHATTING = "Chatting...";

        {
            putValue(NAME, START);
        }

        private void start() {
            try {
                int port;
                port = Integer.parseInt(portField.getText());
                Connection connection;
                if (isServer) {
                    connection = Starter.startServer(port);
                } else {
                    connection = Starter.startClient(hostField.getText(), port);
                }
                putValue(NAME, CHATTING);
                new ChatDialog(StartFrame.this).chat(nameField.getText(), connection);
                connection.close();
            } catch (NumberFormatException | IOException e) {
                GUIUtils.showErrorDialog(StartFrame.this, e.getMessage());
            }
            putValue(NAME, START);
            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            putValue(NAME, CONNECTING);
            setEnabled(false);
            SwingUtilities.invokeLater(this::start);
        }
    };

    /**
     * Constructs the frame.
     */
    StartFrame() {
        super("P2P Messenger");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel1 = new JPanel();
        panel1.setBorder(GUIUtils.getBorder("Connection"));
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        {
            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            ButtonGroup buttonGroup = new ButtonGroup();
            {
                JRadioButton button = new JRadioButton(new IsServerAction(true));
                buttonGroup.add(button);
                panel.add(button, GUIUtils.getConstraints(0, 0));

                button = new JRadioButton(new IsServerAction(false));
                buttonGroup.add(button);
                panel.add(button, GUIUtils.getConstraints(0, 1));

                button.setSelected(true);
            }
            panel1.add(panel);
        }
        {
            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            {
                JLabel label = new JLabel("Name");
                panel.add(label, GUIUtils.getConstraints(1, 0));

                nameField = new JTextField();
                panel.add(nameField, GUIUtils.getConstraints(1, 1, true));
            }
            {
                JLabel label = new JLabel("Host");
                panel.add(label, GUIUtils.getConstraints(2, 0));

                hostField = new JTextField();
                panel.add(hostField, GUIUtils.getConstraints(2, 1, true));
            }
            {
                JLabel label = new JLabel("Port");
                panel.add(label, GUIUtils.getConstraints(3, 0));

                portField = new JTextField();
                panel.add(portField, GUIUtils.getConstraints(3, 1, true));
            }
            {
                JButton button = new JButton(startAction);
                panel.add(button, GUIUtils.getConstraints(4, 0, 2));
            }
            panel1.add(panel);
        }
        panel1.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel1.getPreferredSize().height));
        panel1.setMinimumSize(panel1.getPreferredSize());
        add(panel1);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
        setMinimumSize(new Dimension(500, getPreferredSize().height));

        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }
}
