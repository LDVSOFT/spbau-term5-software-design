package net.ldvsoft.spbau.messenger.gui;

import net.ldvsoft.spbau.messenger.Messenger;
import net.ldvsoft.spbau.messenger.protocol.Connection;
import net.ldvsoft.spbau.messenger.protocol.PeerInfo;
import net.ldvsoft.spbau.messenger.protocol.TextMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * Chat dialog.
 * Just close to leave.
 */
class ChatDialog extends JDialog {
    private static final Format FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Messenger messenger;
    private JTextField nameField;
    private JTextArea messagesArea;
    private JTextField messageField;
    private Messenger.Listener listener = new Messenger.Listener() {
        @Override
        public void onMessage(TextMessage s) {
            messagesArea.append(String.format(
                    "<<< (%s at %s) %s\n",
                    messenger.getPeer().getName(),
                    FORMATTER.format(s.getDate()),
                    s.getText()
            ));
        }

        @Override
        public void onPeerInfo(PeerInfo s) {
            messagesArea.append(String.format(
                    "<<< (changed name to %s)\n",
                    s.getName()
            ));
        }

        @Override
        public void onBye() {
            messagesArea.append(String.format(
                    "<<< (%s left)\n",
                    messenger.getPeer().getName()
            ));
            setNameAction.setEnabled(false);
            sendMessageAction.setEnabled(false);
        }

        @Override
        public void onError(Exception e) {

        }
    };

    private Action setNameAction = new AbstractAction() {
        {
            putValue(NAME, "Set name");
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            setEnabled(false);
            try {
                messenger.setName(nameField.getText());
            } catch (IOException e) {
                GUIUtils.showErrorDialog(ChatDialog.this, e.getMessage());
            }
            setEnabled(true);
        }
    };

    private Action sendMessageAction = new AbstractAction() {
        {
            putValue(NAME, "Send");
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            setEnabled(false);
            try {
                TextMessage textMessage = messenger.sendMessage(messageField.getText());
                messageField.setText("");
                messagesArea.append(String.format(
                        ">>> (%s at %s) %s\n",
                        messenger.getPeer().getName(),
                        FORMATTER.format(textMessage.getDate()),
                        textMessage.getText()
                ));
            } catch (IOException e) {
                GUIUtils.showErrorDialog(ChatDialog.this, e.getMessage());
            }
            setEnabled(true);
        }
    };

    ChatDialog(JFrame owner) {
        super(owner, "Chat", ModalityType.DOCUMENT_MODAL);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(5, 5));
        {
            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            {
                nameField = new JTextField();
                panel.add(nameField, GUIUtils.getConstraints(0, 0, true));
            }
            {
                JButton button = new JButton(setNameAction);
                button.setMaximumSize(button.getPreferredSize());
                panel.add(button, GUIUtils.getConstraints(0, 1));
            }
            add(panel, BorderLayout.NORTH);
        }
        {
            messagesArea = new JTextArea();
            messagesArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(messagesArea);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            add(scrollPane, BorderLayout.CENTER);
        }
        {
            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            {
                messageField = new JTextField();
                panel.add(messageField, GUIUtils.getConstraints(0, 0, true));
            }
            {
                JButton button = new JButton(sendMessageAction);
                button.setMaximumSize(button.getPreferredSize());
                panel.add(button, GUIUtils.getConstraints(0, 1));
            }
            add(panel, BorderLayout.SOUTH);
        }
        setMinimumSize(new Dimension(600, 600));
        pack();
    }

    void chat(String name, Connection connection) {
        try {
            messenger = new Messenger(name, connection, listener);
            nameField.setText(name);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        messenger.stop();
                    } catch (IOException e1) {
                        GUIUtils.showErrorDialog(ChatDialog.this, e1.getMessage());
                    }
                }
            });
            setVisible(true);
        } catch (IOException e) {
            GUIUtils.showErrorDialog(this, e.getMessage());
        }
    }
}
