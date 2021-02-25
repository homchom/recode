package io.github.codeutilities.dfrpc.libs.callbacks;

import com.sun.jna.Callback;

/**
 * @author Nicolas "Vatuu" Adamoglou
 * @version 1.5.1
 * <p>
 * Interface to be implemented in classes that will be registered as "SpectateGameCallback" Event Handler.
 **/
public interface SpectateGameCallback extends Callback {

    /**
     * Method called when joining a game.
     *
     * @param spectateSecret Unique String containing information needed to let the player spectate.
     */
    void apply(String spectateSecret);
}