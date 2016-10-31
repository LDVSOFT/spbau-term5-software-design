package net.ldvsoft.spbau.messenger;

import net.ldvsoft.spbau.messenger.protocol.Connection;
import net.ldvsoft.spbau.messenger.protocol.PeerInfo;
import net.ldvsoft.spbau.messenger.protocol.Protocol;
import net.ldvsoft.spbau.messenger.protocol.TextMessage;

import java.io.IOException;
import java.util.Date;

/**
 * Messenger main behaviour class.
 * After creating, starts waiting for incoming messages and reports them.
 * Gives methods for sending messages.
 */
public class Messenger {
    private static final String DEFAULT_NAME = "<unknown>";

    public interface Listener {
        void onMessage(TextMessage s);
        void onPeerInfo(PeerInfo s);
        void onBye();
        void onError(Exception e);
    }

    private Protocol protocol;
    private PeerInfo self;
    private PeerInfo peer = new PeerInfo(DEFAULT_NAME);
    private boolean isWorking = true;
    private Thread listenerThread;
    private Listener listener;

    public Messenger(String name, Connection connection, Listener listener) throws IOException {
        protocol = new Protocol(connection);
        setName(name);
        this.listener = listener;
        listenerThread = new Thread(this::listen);
        listenerThread.start();
    }

    public TextMessage sendMessage(String text) throws IOException {
        TextMessage textMessage = new TextMessage(text, new Date());
        protocol.writeTextMessage(textMessage);
        return textMessage;
    }

    public void setName(String name) throws IOException {
        self = new PeerInfo(name);
        protocol.writePeerInfo(self);
    }

    public void stop() throws IOException {
        if (isWorking) {
            isWorking = false;
            listenerThread.interrupt();
            protocol.writeBye();
            protocol.close();
        }
    }

    public PeerInfo getSelf() {
        return self;
    }

    public PeerInfo getPeer() {
        return peer;
    }

    private void listen() {
        try {
            while (isWorking) {
                Protocol.MessageType messageType = protocol.readMessageType();
                switch (messageType) {
                    case PEER_INFO:
                        PeerInfo info = protocol.readPeerInfo();
                        peer = info;
                        listener.onPeerInfo(info);
                        break;
                    case TEXT_MESSAGE:
                        TextMessage message = protocol.readTextMessage();
                        listener.onMessage(message);
                        break;
                    case BYE:
                        protocol.readBye();
                        isWorking = false;
                        listener.onBye();
                        protocol.close();
                        break;
                }
            }
        } catch (IOException e) {
            if (!isWorking || Thread.interrupted()) {
                return;
            }
            listener.onError(e);
        }
    }
}
