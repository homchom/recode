package io.github.homchom.recode.mod.events.interfaces;

import net.minecraft.world.InteractionResult;

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
        InteractionResult run(CustomEvent listener);
    }

    static InteractionResult makeEvent(CustomEvent[] listeners, Interaction interaction) {
        for (CustomEvent listener : listeners) {
            InteractionResult result = interaction.run(listener);

            if (result != InteractionResult.PASS) {
                return result;
            }
        }

        return InteractionResult.PASS;
    }
}
