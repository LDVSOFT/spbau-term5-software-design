package net.ldvsoft.spbau.messenger.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Text message.
 * Consists of the message itself and sending time.
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

    P2PMessenger.TextMessage toProto() {
        return P2PMessenger.TextMessage.newBuilder()
                .setText(text)
                .setDate(date.getTime())
                .build();
    }

    static TextMessage fromProto(P2PMessenger.TextMessage proto) {
        return new TextMessage(proto.getText(), new Date(proto.getDate()));
    }
}
