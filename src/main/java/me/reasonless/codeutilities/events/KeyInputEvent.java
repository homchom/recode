package me.reasonless.codeutilities.events;

import me.reasonless.codeutilities.CodeUtilities;
import me.reasonless.codeutilities.CodeUtilities.PlayMode;
import me.reasonless.codeutilities.commands.util.AfkCommand;
import me.reasonless.codeutilities.objects.AfkMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class KeyInputEvent {

  public static List<FabricKeyBinding> kb = new ArrayList<>();
  public static List<Boolean> lastKb = new ArrayList<>();
  static MinecraftClient mc = MinecraftClient.getInstance();
  static int afktime = 0;

  public static void register() {
    KeyBindingRegistry.INSTANCE.addCategory("CodeUtilities");
    kb.add(FabricKeyBinding.Builder
        .create(new Identifier("play-shortcut"), Type.KEYSYM, -1, "CodeUtilities").build());

    kb.add(FabricKeyBinding.Builder
        .create(new Identifier("build-shortcut"), Type.KEYSYM, -1, "CodeUtilities").build());

    kb.add(FabricKeyBinding.Builder
        .create(new Identifier("dev-shortcut"), Type.KEYSYM, -1, "CodeUtilities").build());

    kb.add(FabricKeyBinding.Builder
        .create(new Identifier("switch-play-dev"), Type.KEYSYM, -1, "CodeUtilities").build());

    kb.add(FabricKeyBinding.Builder
        .create(new Identifier("chest-preview"), Type.KEYSYM, -1, "CodeUtilities").build());

    for (FabricKeyBinding bind : kb) {
      KeyBindingRegistry.INSTANCE.register(bind);
      lastKb.add(false);
    }

    ClientTickCallback.EVENT.register(e -> {

      //play
      if (kb.get(0).isPressed() != lastKb.get(0)) {
        if (kb.get(0).isPressed()) {
          assert mc.player != null;
          mc.player.sendChatMessage("/play");
        }
      }

      //build
      if (kb.get(1).isPressed() != lastKb.get(1)) {
        if (kb.get(1).isPressed()) {
          assert mc.player != null;
          mc.player.sendChatMessage("/build");
        }
      }

      //dev
      if (kb.get(2).isPressed() != lastKb.get(2)) {
        if (kb.get(2).isPressed()) {
          assert mc.player != null;
          mc.player.sendChatMessage("/dev");
        }
      }

      //switch play dev
      if (kb.get(3).isPressed() != lastKb.get(3)) {
        if (kb.get(3).isPressed()) {
          switch (CodeUtilities.playMode) {
            case DEV:
            case BUILD:
              assert mc.player != null;
              mc.player.sendChatMessage("/play");
              CodeUtilities.playMode = PlayMode.PLAY;
              break;
            case PLAY:
              assert mc.player != null;
              mc.player.sendChatMessage("/dev");
              CodeUtilities.playMode = PlayMode.DEV;
              break;
            default:
              CodeUtilities.errorMsg("§cYou need to be in /play, /dev or /build!");
              CodeUtilities.errorMsg("§cPlay <-> Dev   Build -> Play");
              break;
          }
        }
      }

      //Chest Preview
      if (kb.get(4).isPressed() != lastKb.get(4)) {
        if (kb.get(4).isPressed()) {
          CodeUtilities.errorMsg("§cWork in progress!");
          /*assert mc.crosshairTarget != null;
          if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = new BlockPos(mc.crosshairTarget.getPos());
            assert mc.world != null;
            if (mc.world.getBlockState(blockPos).getBlock().getName().asFormattedString()
                .equals("Chest")) {
              assert mc.player != null;
              if (mc.player.isCreative()) {
                Vec3d pos = mc.player.getPos();
                Direction side = Direction.UP;
                CodeUtilities.chestpreview = true;
                Objects.requireNonNull(mc.getNetworkHandler())
                    .sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(pos, side, blockPos, false)));
                new Thread(() -> {
                  for (int i = 0; i < 30; i++) {
                    try {
                      Thread.sleep(100);
                    } catch (InterruptedException ex) {
                      ex.printStackTrace();
                    }
                    if (!CodeUtilities.chestpreview) return;
                  }
                  CodeUtilities.print("§cChest Preview timed out!");
                  CodeUtilities.chestpreview = false;
                }).start();
              } else {
                CodeUtilities.print("§cYou need to be in creative!");
              }
            } else {
              CodeUtilities.print("§cYou need to look at a chest!");
            }
          } else {
            CodeUtilities.print("§cYou need to look at a block!");
          }*/
        }
      }

      //Disable afk
      if (CodeUtilities.afk) {
        for (KeyBinding bind : mc.options.keysAll) {
          if (bind.isPressed()) {
            CodeUtilities.afk = false;
            CodeUtilities.successMsg("Disabled afk mode.");
            if (AfkCommand.msgs.size() > 0) {
              CodeUtilities.infoMsgYellow("Logged messages:");
              for (AfkMessage msg : AfkCommand.msgs) {
                msg.print(mc);
              }
              CodeUtilities.infoMsgYellow("---------------");
              AfkCommand.msgs.clear();
            }
            return;
          }
        }
      }

      //AutoAfk
      if (!CodeUtilities.afk && CodeUtilities.p.getProperty("autoafk").equals("true")) {
        afktime++;
        if (afktime % 20 == 0) {
          for (KeyBinding bind : mc.options.keysAll) {
            if (bind.isPressed()) {
              afktime = 0;
              break;
            }
          }
          if (afktime > Integer.parseInt(CodeUtilities.p.getProperty("autoafktime")) * 20) {
            CodeUtilities.successMsg("Enabled afk mode.");
            CodeUtilities.afk = true;
          }
        }
      } else {
        afktime = 0;
      }

      lastKb.clear();
      for (FabricKeyBinding bind : kb) {
        lastKb.add(bind.isPressed());
      }
    });
  }
}
