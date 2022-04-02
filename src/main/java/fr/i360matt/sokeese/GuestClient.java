package fr.i360matt.sokeese;

import java.io.*;
import java.net.Socket;

public class GuestClient {

    protected Server server;
    protected Socket socket;
    protected boolean isOpen;
    protected String currentName;

    protected final BufferedOutputStream outBuffer;
    protected final BufferedInputStream inBuffer;

    protected GuestClient (final Server server, final Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.isOpen = true;

        this.outBuffer = new BufferedOutputStream(this.socket.getOutputStream());
        this.inBuffer = new BufferedInputStream(this.socket.getInputStream());
    }

    protected GuestClient (final GuestClient guestClient) {
        this.server = guestClient.server;
        this.socket = guestClient.socket;
        this.isOpen = true;
        this.currentName = guestClient.currentName;

        this.outBuffer = guestClient.outBuffer;
        this.inBuffer = guestClient.inBuffer;
    }

    protected Client createClient () throws IOException {
        Client client = new Client(this);
        GuestClient previous = this.server.getContacts().set(this.currentName, client);
        if (previous != null)
            previous.close();
        return client;
    }

    public boolean isOpen () {
        return this.isOpen;
    }

    public void setCurrentName (String currentName) {
        this.currentName = currentName;
    }
    public String getCurrentName () {
        return currentName;
    }

    public void close () {
        try {
            this.socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.isOpen = false;
    }

}