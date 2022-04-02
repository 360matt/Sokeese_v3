package fr.i360matt.sokeese;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ServerEvent {

    private Function<GuestClient, Boolean> onInputConnect = (x) -> true;
    private Function<GuestClient, Boolean> onOutputConnect = (x) -> true;
    private Consumer<GuestClient> onDisconnection = (x) -> {};
    private BiConsumer<GuestClient, Object> onReceive = (x, y) -> {};
    private BiFunction<GuestClient, Object, Boolean> onSend = (x, y) -> true;
    private BiConsumer<GuestClient, Exception> onError = (x, e) -> {};

    public void onInputConnection (@NotNull Function<GuestClient, Boolean> function) {
        this.onInputConnect = function;
    }

    public void onOutputConnection (@NotNull Function<GuestClient, Boolean> function) {
        this.onOutputConnect = function;
    }

    public void onDisconnection (@NotNull Consumer<GuestClient> function) {
        this.onDisconnection = function;
    }

    public void onReceive (@NotNull BiConsumer<GuestClient, Object> function) {
        this.onReceive = function;
    }

    public void onSend (@NotNull BiFunction<GuestClient, Object, Boolean> function) {
        this.onSend = function;
    }

    public void onError (@NotNull BiConsumer<GuestClient, Exception> function) {
        this.onError = function;
    }

    protected boolean callInputConnect (GuestClient client) {
        return this.onInputConnect.apply(client);
    }

    protected boolean callOutputConnect (GuestClient client) {
        return this.onOutputConnect.apply(client);
    }

    protected void callDisconnect (GuestClient client) {
        this.onDisconnection.accept(client);
    }

    protected void callReceive (GuestClient client, Object object) {
        this.onReceive.accept(client, object);
    }

    protected void callSend (GuestClient client, Object object) {
        this.onSend.apply(client, object);
    }

    protected void callError (GuestClient client, Exception e) {
        this.onError.accept(client, e);
    }

}
