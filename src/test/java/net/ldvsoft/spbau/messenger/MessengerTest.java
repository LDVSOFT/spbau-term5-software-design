package net.ldvsoft.spbau.messenger;

import net.ldvsoft.spbau.messenger.protocol.Connection;
import net.ldvsoft.spbau.messenger.protocol.PeerInfo;
import net.ldvsoft.spbau.messenger.protocol.TextMessage;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Some full messaging testing
 */
public class MessengerTest {
    private static final int PORT = 9987;

    private static class Echo implements Messenger.Listener {
        private Messenger messenger;

        @Override
        public void onMessage(TextMessage s) {
            try {
                messenger.sendMessage(s.getText());
            } catch (IOException e) {
                fail();
            }
        }

        @Override
        public void onPeerInfo(PeerInfo s) {
            try {
                messenger.setName(s.getName());
            } catch (IOException e) {
                fail();
            }
        }

        @Override
        public void onBye() {

        }

        @Override
        public void onError(Exception e) {
            fail("Exception: " + e.getMessage());
        }

        public void setMessenger(Messenger messenger) {
            this.messenger = messenger;
        }
    }

    private static class Buffer implements Messenger.Listener {
        private Queue<Object> queue = new ArrayDeque<>();

        private Object poll() {
            return queue.poll();
        }

        @Override
        public void onMessage(TextMessage s) {
            queue.add(s);
        }

        @Override
        public void onPeerInfo(PeerInfo s) {
            queue.add(s);
        }

        @Override
        public void onBye() {
        }

        @Override
        public void onError(Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    /**
     * Server is echoing everything, client just sends several messages
     * @throws Exception in case something fails
     */
    @Test(timeout = 10000)
    public void testSimpleMessaging() throws Exception {
        // Here, we have to use a little hack
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<Connection> fServer = executor.submit(() -> Starter.startServer(PORT));
        Future<Connection> fClient = executor.submit(() -> {
            Thread.sleep(1000);
            return Starter.startClient("localhost", PORT);
        });
        Connection serverConnection = fServer.get();
        Connection clientConnection = fClient.get();

        Buffer buffer = new Buffer();
        Messenger client = new Messenger("client", clientConnection, buffer);
        assertEquals("client", client.getSelf().getName());

        Echo echo = new Echo();
        Messenger server = new Messenger("server", serverConnection, echo);
        echo.setMessenger(server);

        Thread.sleep(1000);
        assertEquals("client", server.getSelf().getName());
        assertEquals("client", server.getPeer().getName());
        PeerInfo peerInfo = (PeerInfo) buffer.poll();
        assertEquals("server", peerInfo.getName()); // original name

        peerInfo = (PeerInfo) buffer.poll();
        assertEquals("client", peerInfo.getName()); // replied with our name

        client.sendMessage("Hey, you!");
        Thread.sleep(1000);
        TextMessage textMessage = (TextMessage) buffer.poll();
        assertEquals("Hey, you!", textMessage.getText());

        client.setName("real client");
        Thread.sleep(1000);
        peerInfo = (PeerInfo) buffer.poll();
        assertEquals("real client", peerInfo.getName());

        assertEquals("real client", server.getSelf().getName());
        assertEquals("real client", server.getPeer().getName());
        assertEquals("real client", client.getSelf().getName());
        assertEquals("real client", client.getPeer().getName());

        client.sendMessage("Go troll someone else!");
        Thread.sleep(1000);
        textMessage = (TextMessage) buffer.poll();
        assertEquals("Go troll someone else!", textMessage.getText());

        client.stop();
        Thread.sleep(1000);
        assertEquals(null, buffer.poll());
        server.stop();
    }
}