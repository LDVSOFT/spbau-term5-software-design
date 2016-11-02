package net.ldvsoft.spbau.messenger;

import net.ldvsoft.spbau.messenger.protocol.Connection;
import net.ldvsoft.spbau.messenger.protocol.PeerInfo;
import net.ldvsoft.spbau.messenger.protocol.Protocol;
import net.ldvsoft.spbau.messenger.protocol.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Logger logger = LoggerFactory.getLogger(Messenger.class);

    public Messenger(String name, Connection connection, Listener listener) throws IOException {
        protocol = new Protocol(connection);
        logger.info("Starting messenger.");
        setName(name);
        this.listener = listener;
        listenerThread = new Thread(this::listen);
        listenerThread.start();
    }

    public TextMessage sendMessage(String text) throws IOException {
        logger.info("Sending message \"{}\".", text);
        TextMessage textMessage = new TextMessage(text, new Date());
        protocol.writeTextMessage(textMessage);
        return textMessage;
    }

    public void setName(String name) throws IOException {
        logger.info("Changing name to \"{}\".", name);
        self = new PeerInfo(name);
        protocol.writePeerInfo(self);
    }

    public void stop() throws IOException {
        logger.info("Stopping.");
        if (isWorking) {
            isWorking = false;
            listenerThread.interrupt();
            protocol.writeBye();
            protocol.close();
        } else {
            logger.debug("Already stopped.");
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
                logger.debug("Waiting for a new message...");
                Protocol.MessageType messageType = protocol.readMessageType();
                logger.debug("Received message of type {}.", messageType.toString());
                switch (messageType) {
                    case PEER_INFO:
                        PeerInfo info = protocol.readPeerInfo();
                        logger.info("Peer changed his name to {} (was {}).", info.getName(), peer.getName());
                        peer = info;
                        listener.onPeerInfo(info);
                        break;
                    case TEXT_MESSAGE:
                        TextMessage message = protocol.readTextMessage();
                        logger.info("Peer sent text message \"{}\" at {}.", message.getText(), message.getDate());
                        listener.onMessage(message);
                        break;
                    case BYE:
                        protocol.readBye();
                        logger.info("Peer closed chat.");
                        isWorking = false;
                        listener.onBye();
                        protocol.close();
                        break;
                }
            }
            logger.info("Listener stopped gracefully.");
        } catch (IOException e) {
            if (!isWorking || Thread.interrupted()) {
                logger.info("Listener interrupted, stopping.");
                return;
            }
            logger.debug("Listener caught exception!", e);
            listener.onError(e);
        }
    }
}
