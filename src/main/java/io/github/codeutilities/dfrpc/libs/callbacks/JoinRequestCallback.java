package io.github.codeutilities.dfrpc.libs.callbacks;

import com.sun.jna.Callback;
import io.github.codeutilities.dfrpc.libs.DiscordUser;

/**
 * @author Nicolas "Vatuu" Adamoglou
 * @version 1.5.1
 * <p>
 * Interface to be implemented in classes that will be registered as "JoinRequestCallback" Event Handler.
 **/
public interface JoinRequestCallback extends Callback {

    /**
     * Method called when another player requests to join a game.
     *
     * @param request Object containing all required information about the user requesting to join.
     * @see DiscordUser
     */
    void apply(DiscordUser request);
}