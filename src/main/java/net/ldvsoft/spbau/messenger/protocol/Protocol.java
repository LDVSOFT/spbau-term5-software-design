package net.ldvsoft.spbau.messenger.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Messenger protocol.
 * Handles all the IO tasks: read request, read it's body, send the answer.
 *
 * To write a message, one should call the write<...> method.
 * To read a message, one should first call readMessageType(),
 * and then call apropriate read<...> method to get the contents.
 */
public class Protocol implements AutoCloseable {
    /**
     * All message types are represented by MessageType enum.
     * Every message starts with it's type id, which are hold by MessageType values.
     */
    public enum MessageType {
        PEER_INFO(0),
        TEXT_MESSAGE(1),
        BYE(2);

        private final int id;

        MessageType(int id) {
            this.id = id;
        }

        int getId() {
            return id;
        }

        static MessageType getById(int id) {
            for (MessageType type: values()) {
                if (type.id == id) {
                    return type;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    private Connection connection;
    private DataInputStream dis;
    private DataOutputStream dos;
    private final Logger logger = LoggerFactory.getLogger(Protocol.class);

    public Protocol(Connection connection) throws IOException {
        this.connection = connection;
        dis = new DataInputStream(connection.getInputStream());
        dos = new DataOutputStream(connection.getOutputStream());
    }

    public MessageType readMessageType() throws IOException {
        int id = dis.readInt();
        logger.info("Received message type {}.", id);
        return MessageType.getById(id);
    }

    private void writeMessageType(MessageType messageType) throws IOException {
        int id = messageType.getId();
        logger.info("Writing message type {}.", id);
        dos.writeInt(id);
        dos.flush();
    }

    public PeerInfo readPeerInfo() throws IOException {
        logger.info("Reading peer info.");
        return PeerInfo.readFrom(dis);
    }

    public void writePeerInfo(PeerInfo peerInfo) throws IOException {
        logger.info("Writing peer info.");
        writeMessageType(MessageType.PEER_INFO);
        peerInfo.writeTo(dos);
        dos.flush();
    }

    public TextMessage readTextMessage() throws IOException {
        logger.info("Reading text message.");
        return TextMessage.readFrom(dis);
    }

    public void writeTextMessage(TextMessage textMessage) throws IOException {
        logger.info("Writing text message.");
        writeMessageType(MessageType.TEXT_MESSAGE);
        textMessage.writeTo(dos);
        dos.flush();
    }

    public void readBye() {
        logger.info("Reading bye.");
        /* bye message is empty and has no body */
    }

    public void writeBye() throws IOException {
        logger.info("Writing bye.");
        writeMessageType(MessageType.BYE);
        dos.flush();
    }

    public void close() throws IOException {
        logger.info("Closing connection.");
        connection.close();
    }
}
