package net.ldvsoft.spbau.messenger.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Peer info structure.
 */
public class PeerInfo {
    private String name;

    public String getName() {
        return name;
    }

    public PeerInfo(String name) {
        this.name = name;
    }

    P2PMessenger.PeerInfo toProto() {
        return P2PMessenger.PeerInfo.newBuilder()
                .setName(name)
                .build();
    }

    static PeerInfo fromProto(P2PMessenger.PeerInfo proto) {
        return new PeerInfo(proto.getName());
    }
}
