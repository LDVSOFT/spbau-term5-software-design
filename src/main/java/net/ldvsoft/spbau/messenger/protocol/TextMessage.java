package net.ldvsoft.spbau.messenger.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by ldvsoft on 26.10.16.
 */
public class TextMessage {
    private String text;
    private Date date;

    public String getText() {
        return text;
    }

    public Date getDate() {
        return date;
    }

    public TextMessage(String text, Date date) {
        this.text = text;
        this.date = date;
    }

    void writeTo(DataOutputStream dos) throws IOException {
        dos.writeLong(date.getTime());
        dos.writeUTF(text);
    }

    static TextMessage readFrom(DataInputStream dis) throws IOException {
        Date date = new Date(dis.readLong());
        String text = dis.readUTF();
        return new TextMessage(text, date);
    }
}
