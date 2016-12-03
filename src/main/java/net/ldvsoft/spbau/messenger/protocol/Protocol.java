package net.ldvsoft.spbau.messenger.protocol;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Messenger protocol.
 * Handles all the IO tasks: read request, read it's body, send the answer.
 *
 * To write a message, one should call the write<...> method.
 * To read a message, one should pass a Listener object.
 */
public class Protocol implements AutoCloseable {
    public interface ProtocolListener {
        void onTextMessage(TextMessage textMessage);
        void onPeerInfo(PeerInfo peerInfo);
        void onStartedTyping(StartedTyping startedTyping);
        void onError(Throwable throwable);
        void onClose();
    }

    private final Logger logger = LoggerFactory.getLogger(Protocol.class);
    private Connection connection;
    private StreamObserver<P2PMessenger.Message> writer;

    public Protocol(Connection connection, ProtocolListener protocolListener) throws IOException {
        this.connection = connection;
        StreamObserver<P2PMessenger.Message> reader = new StreamObserver<P2PMessenger.Message>() {
            @Override
            public void onNext(P2PMessenger.Message message) {
                try {
                    switch (message.getBodyCase()) {
                        case PEERINFO:
                            logger.info("gRPC peer info received");
                            protocolListener.onPeerInfo(PeerInfo.fromProto(message.getPeerInfo()));
                            break;
                        case TEXTMESSAGE:
                            logger.info("gRPC text message received");
                            P2PMessenger.TextMessage textMessage = message.getTextMessage();
                            protocolListener.onTextMessage(TextMessage.fromProto(textMessage));
                            break;
                        case STARTEDTYPING:
                            logger.info("gRPC started typing received");
                            protocolListener.onStartedTyping(StartedTyping.fromProto(message.getStartedTyping()));
                            break;
                        case BODY_NOT_SET:
                            logger.warn("gRPC message received with no body :/");
                            break;
                        default:
                            logger.error("gRPC gave unknown message!");
                    }
                } catch (Throwable e) {
                    logger.error("WTF", e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("gRPC error", throwable);
                protocolListener.onError(throwable);
            }

            @Override
            public void onCompleted() {
                logger.info("gRPC closed");
                protocolListener.onClose();
            }
        };
        writer = connection.startChat(reader);
    }

    public void writeTextMessage(TextMessage textMessage) {
        P2PMessenger.Message message = P2PMessenger.Message.newBuilder().setTextMessage(textMessage.toProto()).build();
        writer.onNext(message);
    }

    public void writePeerInfo(PeerInfo peerInfo) {
        P2PMessenger.Message message = P2PMessenger.Message.newBuilder().setPeerInfo(peerInfo.toProto()).build();
        writer.onNext(message);
    }

    public void writeStartedTyping(StartedTyping startedTyping) {
        P2PMessenger.Message message = P2PMessenger.Message.newBuilder().setStartedTyping(startedTyping.toProto()).build();
        writer.onNext(message);
    }

    @Override
    public void close() {
        writer.onCompleted();
        try {
            connection.close();
        } catch (IOException e) {
            logger.warn("Exception closing connection", e);
        }
    }
}
