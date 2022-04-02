package fr.i360matt.sokeese;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class Server implements Closeable {

    protected final ServerSocket server;
    protected final Contacts contacts;
    protected final ServerOptions options;
    protected final ServerEvent events;
    protected final ScheduledExecutorService executor;

    protected Future<?> listeningTask;

    public Server (final int port) throws IOException {
        this.server = new ServerSocket(port);
        this.contacts = new Contacts();
        this.options = new ServerOptions();
        this.events = new ServerEvent();
        this.executor = Executors.newScheduledThreadPool(this.options.getCorePoolSize());
    }

    public Future<?> listen () {
        if (this.executor.isShutdown())
            return null;

        if (this.listeningTask != null) {
            if (this.listeningTask.isCancelled())
                return this.listeningTask;
            if (this.listeningTask.isDone())
                return this.listeningTask;
        }

        return this.listeningTask = this.executor.submit(() -> {
            while (true) {
                Socket socket = server.accept();
                GuestClient guestClient = new GuestClient(this, socket);
                if (this.receiveConnection(guestClient)) {
                    Client client = guestClient.createClient();
                    client.startTasks();
                } else {
                    guestClient.close();
                }
            }
        });
    }

    public void unlisten () {
        if (this.listeningTask != null) {
            this.listeningTask.cancel(true);
        }
    }

    public Client connectTo (final String host, final int port, final String name) throws IOException {
        Socket socket = new Socket(host, port);
        GuestClient guestClient = new GuestClient(this, socket);
        guestClient.setCurrentName(name);
        if (this.sendConnection(guestClient)) {
            try {
                Client client = guestClient.createClient();
                client.startTasks();
                return client;
            } catch (RuntimeException e) {
                guestClient.close();
                throw e;
            }
        }
        return null;
    }


    public boolean receiveConnection (final GuestClient guestClient) throws IOException {
        while (guestClient.inBuffer.available() == 0) {}

        byte[] nameBytes = new byte[guestClient.inBuffer.available()];
        guestClient.inBuffer.read(nameBytes);
        guestClient.setCurrentName(new String(nameBytes));

        return this.getEvents().callInputConnect(guestClient);
    }

    public boolean sendConnection (final GuestClient guestClient) throws IOException {
        guestClient.outBuffer.write(guestClient.getCurrentName().getBytes());
        guestClient.outBuffer.flush();

        return this.getEvents().callOutputConnect(guestClient);
    }



    protected ScheduledExecutorService getExecutor() {
        return executor;
    }
    public ServerOptions getOptions() {
        return options;
    }
    public Contacts getContacts() {
        return contacts;
    }
    public ServerEvent getEvents() {
        return events;
    }

    @Override
    public void close() throws IOException {
        this.server.close();
        this.executor.shutdownNow();
    }
}
