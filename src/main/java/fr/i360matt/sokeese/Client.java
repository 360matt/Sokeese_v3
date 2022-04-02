package fr.i360matt.sokeese;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Client extends GuestClient implements Closeable {

    protected ObjectOutputStream out;
    protected ObjectInputStream in;

    private Future<?> autoFlushTask;
    private Future<?> readTask;

    protected Client (GuestClient client) throws IOException {
        super(client);
        this.out = new ObjectOutputStream((socket.getOutputStream()));
        if (this.socket.getInputStream().available() == 0) {
            this.close();
            throw new RuntimeException("Login failed");
        }
        this.in = new ObjectInputStream((socket.getInputStream()));
    }

    public void startTasks () {
        if (server.getOptions().autoFLushDelay > 0) {
            this.autoFlushTask = server.getExecutor().schedule(() -> {
                this.flush();
            }, server.getOptions().autoFLushDelay, TimeUnit.MICROSECONDS);
        }

        this.readTask = server.getExecutor().submit(() -> {
            try {
                Object object;
                while ((object = in.readUnshared()) != null) {
                    this.server.getEvents().callReceive(this, object);
                }
            } catch (Exception e) {
                if (!(e instanceof SocketException)) {
                    this.server.getEvents().callError(this, e);
                }
            }
            this.close();
        });

    }

    public void sendObject (Object object) {
        try {
            this.out.writeUnshared(object);
            if (server.getOptions().autoFLushDelay == 0) {
                flush();
            }
            this.server.getEvents().callSend(this, object);
        } catch (Exception e) {
            if (!(e instanceof SocketException)) {
                this.server.getEvents().callError(this, e);
            }
        }
    }

    public void flush () {
        try {
            this.out.flush();
            this.out.reset();
        } catch (Exception e) {
            if (!(e instanceof SocketException)) {
                this.server.getEvents().callError(this, e);
            }
        }
    }

    // public abstract void receiveObject (Object object);

    @Override
    public void close () {
        super.close();
        if (autoFlushTask != null)
            autoFlushTask.cancel(true);
        if (readTask != null)
            readTask.cancel(true);
        this.server.getEvents().callDisconnect(this);
    }

}
