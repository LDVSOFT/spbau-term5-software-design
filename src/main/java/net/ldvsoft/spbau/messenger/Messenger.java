package net.ldvsoft.spbau.messenger;

import net.ldvsoft.spbau.messenger.protocol.*;
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
        void onStartedTyping(StartedTyping s);
        void onBye();
        void onError(Throwable e);
    }

    private Protocol protocol;
    private Connection connection;
    private Listener listener;
    private PeerInfo self;
    private PeerInfo peer = new PeerInfo(DEFAULT_NAME);
    private boolean isWorking = true;
    private final Logger logger = LoggerFactory.getLogger(Messenger.class);

    public Messenger(String name, Connection connection, Listener listener) {
        self = new PeerInfo(name);
        this.connection = connection;
        this.listener = listener;
    }

    public void start() throws IOException {
        Protocol.ProtocolListener protocolListener = new Protocol.ProtocolListener() {
            @Override
            public void onTextMessage(TextMessage message) {
                logger.info("Peer sent text message \"{}\" at {}.", message.getText(), message.getDate());
                listener.onMessage(message);
            }

            @Override
            public void onPeerInfo(PeerInfo info) {
                logger.info("Peer changed his name to {} (was {}).", info.getName(), peer.getName());
                peer = info;
                listener.onPeerInfo(info);
            }

            @Override
            public void onStartedTyping(StartedTyping startedTyping) {
                logger.info("Peer started typing at {}.", startedTyping.getDate());
                listener.onStartedTyping(startedTyping);
            }

            @Override
            public void onError(Throwable throwable) {
                listener.onError(throwable);
            }

            @Override
            public void onClose() {
                logger.info("Peer closed chat.");
                isWorking = false;
                listener.onBye();
                protocol.close();
            }
        };
        protocol = new Protocol(connection, protocolListener);
        logger.info("Starting messenger.");
        setName(self.getName());
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

    public StartedTyping startedTyping() throws IOException {
        logger.info("Started typing.");
        StartedTyping startedTyping = new StartedTyping(new Date());
        protocol.writeStartedTyping(startedTyping);
        return startedTyping;
    }

    public void stop() throws IOException {
        logger.info("Stopping.");
        if (isWorking) {
            isWorking = false;
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
}
