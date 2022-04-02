import fr.i360matt.sokeese.Client;
import fr.i360matt.sokeese.Server;

import java.io.IOException;
import java.util.concurrent.Future;

public class Test {

    public static void main(String[] args) throws IOException {

        Server server = new Server(25565);

        server.getEvents().onReceive((guestClient, o) -> {
            System.out.println("Received: " + o);
        });

        server.getEvents().onInputConnection((guestClient) -> {
            System.out.println("Client connected");

            return true;
        });

        server.getEvents().onDisconnection((guestClient) -> {
            System.out.println("Client disconnected");
        });


        Future<?> task = server.listen();
        System.out.println("Server #1 started");


        Server server2 = new Server(4000);
        server2.listen();
        System.out.println("Server #2 started");

        Client client = server2.connectTo("localhost", 25565, "test");
        server2.getOptions().setAutoFLushDelay(-1);



        client.sendObject("Hello");



    }

}
