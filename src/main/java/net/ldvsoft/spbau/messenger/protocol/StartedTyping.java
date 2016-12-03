package net.ldvsoft.spbau.messenger.protocol;

import java.util.Date;

/**
 * Created by ldvsoft on 02.12.16.
 */
public class StartedTyping {
    private Date date;

    public Date getDate() {
        return date;
    }

    public StartedTyping(Date date) {
        this.date = date;
    }

    P2PMessenger.StartedTyping toProto() {
        return P2PMessenger.StartedTyping.newBuilder()
                .setDate(date.getTime())
                .build();
    }

    static StartedTyping fromProto(P2PMessenger.StartedTyping proto) {
        return new StartedTyping(new Date(proto.getDate()));
    }
}
