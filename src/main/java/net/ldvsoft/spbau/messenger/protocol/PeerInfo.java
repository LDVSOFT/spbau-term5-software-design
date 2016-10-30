package net.ldvsoft.spbau.messenger.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ldvsoft on 26.10.16.
 */
public class PeerInfo {
    private String name;

    public String getName() {
        return name;
    }

    public PeerInfo(String name) {
        this.name = name;
    }

    void writeTo(DataOutputStream dos) throws IOException {
        dos.writeUTF(name);
    }

    static PeerInfo readFrom(DataInputStream dis) throws IOException {
        return new PeerInfo(dis.readUTF());
    }
}
