package net.ldvsoft.spbau.messenger.protocol;

import org.junit.Test;

import java.io.*;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Some protocol testing
 */
public final class ProtocolTest {
    private static class ScriptedConnection implements Connection {
        private ByteArrayInputStream inputStream;
        private ByteArrayOutputStream outputStream;
        private byte[] expectedOutput;

        private ScriptedConnection(byte[] input, byte[] output) {
            inputStream  = new ByteArrayInputStream(input);
            outputStream = new ByteArrayOutputStream();
            expectedOutput = output;
        }

        @Override
        public InputStream getInputStream() {
            return inputStream;
        }

        @Override
        public OutputStream getOutputStream() {
            return outputStream;
        }

        @Override
        public void close() {
            byte[] actualOutput = outputStream.toByteArray();
            assertArrayEquals("Expected different output!", expectedOutput, actualOutput);
            assertEquals("Not whole input was read!", 0, inputStream.available());
        }
    }

    @Test
    public void testPeerInfo() throws IOException {
        byte[] input  = {0, 0, 0, 0, 0, 3, 97, 98, 99};
        byte[] output = {0, 0, 0, 0, 0, 4, 100, 101, 102, 103};
        Connection connection = new ScriptedConnection(input, output);

        try (Protocol protocol = new Protocol(connection)) {
            Protocol.MessageType type = protocol.readMessageType();
            assertEquals(Protocol.MessageType.PEER_INFO, type);
            PeerInfo info = protocol.readPeerInfo();
            assertEquals("abc", info.getName());

            protocol.writePeerInfo(new PeerInfo("defg"));
        }
    }

    @Test
    public void testTextMessage() throws IOException {
        byte[] input  = {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 12, 0, 3, 97, 98, 99};
        byte[] output = {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 14, 0, 4, 100, 101, 102, 103};
        Connection connection = new ScriptedConnection(input, output);

        try (Protocol protocol = new Protocol(connection)) {
            Protocol.MessageType type = protocol.readMessageType();
            assertEquals(Protocol.MessageType.TEXT_MESSAGE, type);
            TextMessage message = protocol.readTextMessage();
            assertEquals("abc", message.getText());
            assertEquals(12, message.getDate().getTime());

            protocol.writeTextMessage(new TextMessage("defg", new Date(14)));
        }
    }

    @Test
    public void testBye() throws IOException {
        byte[] input  = {0, 0, 0, 2};
        byte[] output = {0, 0, 0, 2};
        Connection connection = new ScriptedConnection(input, output);

        try (Protocol protocol = new Protocol(connection)) {
            Protocol.MessageType type = protocol.readMessageType();
            assertEquals(Protocol.MessageType.BYE, type);
            protocol.readBye();

            protocol.writeBye();
        }
    }
}