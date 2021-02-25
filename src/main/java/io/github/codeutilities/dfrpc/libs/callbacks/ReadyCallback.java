package io.github.codeutilities.dfrpc.libs.callbacks;

import com.sun.jna.Callback;
import io.github.codeutilities.dfrpc.libs.DiscordUser;

/**
 * @author Nicolas "Vatuu" Adamoglou
 * @version 1.5.1
 * <p>
 * Interface to be implemented in classes that will be registered as "ReadyCallback" Event Handler.
 **/
public interface ReadyCallback extends Callback {

    /**
     * Method called when the connection to Discord has been established.
     *
     * @param user Object containing all required information about the user executing the app.
     * @see DiscordUser
     **/
    void apply(DiscordUser user);
}
