package io.github.codeutilities.mod.mixin.render;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class MChestBlock {

    @Inject(method = "onUse", at = @At("HEAD"))
    private void use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        BlockPos signloc = pos.add(-1,-1,0);
        BlockEntity be = world.getBlockEntity(signloc);
        if (be instanceof SignBlockEntity) {
            SignBlockEntity sign = (SignBlockEntity) be;
            CodeUtilities.signText = new String[]{
                sign.getTextOnRow(0).getString(),
                sign.getTextOnRow(1).getString(),
                sign.getTextOnRow(2).getString(),
                sign.getTextOnRow(3).getString()
            };
            CodeUtilities.EXECUTOR.submit(() -> {
                try {
                  Thread.sleep(1000);
                } catch (Exception ignored) {}
                if (!(CodeUtilities.MC.currentScreen instanceof GenericContainerScreen)) {
                    CodeUtilities.signText = new String[0];
                }
            });
        } else CodeUtilities.signText = new String[0];
    }

}
