package me.reasonless.codeutilities.events;

import me.reasonless.codeutilities.CodeUtilities;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ContainerOpenEvent {

  public static void onOpen(List<ItemStack> content, CallbackInfo ci) {

	  if (CodeUtilities.hasblazing) return;
	  
    boolean cancel = false;

    //Chest Preview
    if (CodeUtilities.chestpreview) {
      CodeUtilities.chestpreview = false;
      cancel = true;
      for (ItemStack item : content) {
        if (!item.isEmpty()) {
          if (item.getOrCreateTag().getInt("CustomModelData") == 500) {
            try {
              CompoundTag nbt = item.getOrCreateTag();
              String snbt = nbt.getCompound("PublicBukkitValues").get("hypercube:varitem")
                  .toString();
              nbt = StringNbtReader.parse(snbt.substring(1, snbt.length() - 1));
              CompoundTag data = nbt.getCompound("data");

              switch (nbt.getString("id")) {
                case "txt": //Text
                  CodeUtilities.infoMsgBlue("§6[Txt] " + data.getString("name"));
                  break;
                case "num": //Number
                  CodeUtilities.infoMsgBlue("§a[Num] " + data.getString("name"));
                  break;
                case "loc": //Location
                  CompoundTag loc = data.getCompound("loc");
                  if (loc.getDouble("pitch") != 0 || loc.getDouble("yaw") != 0) {
                    CodeUtilities.infoMsgBlue(
                        "§r[Loc] " + loc.getDouble("x") + " " + loc.getDouble("y") + " " + loc
                            .getDouble("z") + " " + loc.getDouble("pitch") + " " + loc
                            .getDouble("yaw"));
                  } else {
                    CodeUtilities.infoMsgBlue(
                        "§r[Loc] " + loc.getDouble("x") + " " + loc.getDouble("y") + " " + loc
                            .getDouble("z"));
                  }
                  break;
                case "snd": //Sound
                  if (data.getDouble("vol") != 2 || data.getDouble("pitch") != 1) {
                    CodeUtilities.infoMsgBlue(
                        "§6[Snd] " + data.getString("sound") + " " + data.getDouble("pitch") + " "
                            + data.getDouble("vol"));
                  } else {
                    CodeUtilities.infoMsgBlue("§6[Snd] " + data.getString("sound"));
                  }
                  break;
                case "part": //Particle
                  CodeUtilities.infoMsgBlue("§5[Par] " + data.getString("particle"));
                  break;
                case "pot": //Potion
                  int duration = data.getInt("dur");
                  duration /= 20;
                  int min = duration / 60;
                  int sec = duration % 60;
                  CodeUtilities.infoMsgBlue(
                      "§5[Pot] " + data.getString("pot") + " " + min + ":" + sec + " " + (
                          data.getInt("amp") + 1));
                  break;
                case "var": //Variable
                  switch (data.getString("scope")) {
                    case "unsaved":
                      CodeUtilities.infoMsgBlue("§6[Var] §7[G] §e" + data.getString("name"));
                      break;
                    case "saved":
                      CodeUtilities.infoMsgBlue("§6[Var] §e[S] §e" + data.getString("name"));
                      break;
                    case "local":
                      CodeUtilities.infoMsgBlue("§6[Var] §a[L] §e" + data.getString("name"));
                      break;
                  }
                  break;
                case "g_val": //Game Value
                  CodeUtilities.infoMsgBlue(
                      "§c[GVal] " + data.getString("type") + ": " + data.getString("target"));
                  break;
                case "bl_tag": //Tag
                  CodeUtilities
                      .infoMsgBlue("§b[Tag] " + data.getString("tag") + ": " + data.getString("option"));
                  break;
                default:
                  CodeUtilities.infoMsgBlue("§cUnknow id: " + nbt.getString("id"));
                  break;
              }
            } catch (Exception e) {
              e.printStackTrace();
              CodeUtilities.errorMsg(
                  "§cFailed to parse item, if this keeps happening please send me (BlazeMCworld) the log.");
            }
          } else { //Item
            CodeUtilities.infoMsgBlue("[Item] " + item.getItem().toString());
          }
        }
      }
    }

    if (cancel) {
      ci.cancel();
      assert MinecraftClient.getInstance().player != null;
      MinecraftClient.getInstance().player.closeContainer();
    }
  }

}
