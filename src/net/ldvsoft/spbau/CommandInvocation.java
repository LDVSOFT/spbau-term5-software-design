package net.ldvsoft.spbau;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by ldvsoft on 10.09.16.
 */
/*package*/ class CommandInvocation implements PipeElement {
    private String command;
    private List<String> args;
    private Future<Boolean> result;

    /*package*/ CommandInvocation(String command, List<String> args) {
        this.command = command;
        this.args = args;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getArgs() {
        return args;
    }

    @Override
    public void start(InputStream in, OutputStream out) {
        result = null;
    }

    @Override
    public boolean join() {
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }
}
