package net.ldvsoft.spbau.messenger.protocol;

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

    public Protocol(Connection connection) throws IOException {
        this.connection = connection;
        dis = new DataInputStream(connection.getInputStream());
        dos = new DataOutputStream(connection.getOutputStream());
    }

    public MessageType readMessageType() throws IOException {
        return MessageType.getById(dis.readInt());
    }

    private void writeMessageType(MessageType messageType) throws IOException {
        dos.writeInt(messageType.getId());
        dos.flush();
    }

    public PeerInfo readPeerInfo() throws IOException {
        return PeerInfo.readFrom(dis);
    }

    public void writePeerInfo(PeerInfo peerInfo) throws IOException {
        writeMessageType(MessageType.PEER_INFO);
        peerInfo.writeTo(dos);
        dos.flush();
    }

    public TextMessage readTextMessage() throws IOException {
        return TextMessage.readFrom(dis);
    }

    public void writeTextMessage(TextMessage textMessage) throws IOException {
        writeMessageType(MessageType.TEXT_MESSAGE);
        textMessage.writeTo(dos);
        dos.flush();
    }

    public void readBye() {
        /* bye message is empty and has no body */
    }

    public void writeBye() throws IOException {
        writeMessageType(MessageType.BYE);
        dos.flush();
    }

    public void close() throws IOException {
        connection.close();
    }
}
