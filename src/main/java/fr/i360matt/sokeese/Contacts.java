package fr.i360matt.sokeese;

import java.util.HashMap;
import java.util.Map;

public class Contacts {

    protected final Map<String, Client> contacts;

    public Contacts () {
        this.contacts = new java.util.HashMap<>();
    }

    public Client set (String name, Client instance) {
        return this.contacts.put(name, instance);
    }

    public Client get (String name) {
        return this.contacts.get(name);
    }

    public void remove (String name) {
        this.contacts.remove(name);
    }

    public HashMap<String, Client> getAll () {
        return (HashMap<String, Client>) this.contacts;
    }

    public void disconnectAll () {
        for (final Client client : this.contacts.values()) {
            client.close();
        }
    }

}
