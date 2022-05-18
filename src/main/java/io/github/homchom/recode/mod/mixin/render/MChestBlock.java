package io.github.homchom.recode.mod.mixin.render;

import io.github.homchom.recode.Recode;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ALL")
@Mixin(ChestBlock.class)
public class MChestBlock {
    @Inject(method = "use", at = @At("HEAD"))
    private void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        BlockPos signloc = pos.offset(-1,-1,0);
        BlockEntity be = world.getBlockEntity(signloc);
        if (be instanceof SignBlockEntity) {
            SignBlockEntity sign = (SignBlockEntity) be;
            Recode.signText = new String[]{
                sign.getMessage(0, false).getString(),
                sign.getMessage(1, false).getString(),
                sign.getMessage(2, false).getString(),
                sign.getMessage(3, false).getString()
            };
            Recode.EXECUTOR.submit(() -> {
                try {
                  Thread.sleep(1000);
                } catch (Exception ignored) {}
                if (!(Recode.MC.screen instanceof ContainerScreen)) {
                    Recode.signText = new String[0];
                }
            });
        } else Recode.signText = new String[0];
    }
}
