package io.github.codeutilities.events.interfaces;

import net.minecraft.util.ActionResult;

public interface CustomEvent {
//    Event<CustomEvent> EVENT = EventFactory.createArrayBacked(CustomEvent.class,
//            (listeners) -> (player, sheep) -> {
//                for (CustomEvent listener : listeners) {
//                    ActionResult result = listener.interact(player, sheep);
//
//                    if(result != ActionResult.PASS) {
//                        return result;
//                    }
//                }
//
//                return ActionResult.PASS;
//            });

    interface Interaction {
        ActionResult run(CustomEvent listener);
    }

    static ActionResult makeEvent(CustomEvent[] listeners, Interaction interaction) {
        for (CustomEvent listener : listeners) {
            ActionResult result = interaction.run(listener);

            if (result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    }
}
