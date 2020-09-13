package io.github.codeutilities.template;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;

import java.io.*;
import java.net.Socket;

public abstract class MinecraftCommunicator {

    public static Socket socket = null;
    public static boolean status;

    public static void initalize() {
        new Thread(() -> {

            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    if (MinecraftCommunicator.status) continue;
                    MinecraftCommunicator.socket = new Socket("127.0.0.1", 31372);
                    MinecraftCommunicator.status = true;
                    new SocketHandler(MinecraftCommunicator.socket).start();
                    MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, new LiteralText("Connected to DFVisual"), null));
                } catch (Exception e) {
                    status = false;
                }
            }
        }).start();

    }
}

class SocketHandler extends Thread {

    Socket connectedsocket = null;
    InputStream input = null;
    OutputStream output = null;

    public SocketHandler(Socket socket) {
        this.connectedsocket = socket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                MinecraftClient mc = MinecraftClient.getInstance();
                input = connectedsocket.getInputStream();
                output = connectedsocket.getOutputStream();
                // Wait for a line to be sent to DFVisual

                // reset code
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectedsocket.getInputStream()));
                String line = reader.readLine();
                ItemStack item = new ItemStack(Items.ENDER_CHEST);

                CompoundTag nbt = new CompoundTag();
                nbt.putInt("version", 1);
                nbt.putString("author", mc.player.getName().getString());
                nbt.putString("name", "Imported from DFVisual....");
                nbt.putString("code", line);

                CompoundTag publicBukkitNbt = new CompoundTag();
                publicBukkitNbt.putString("hypercube:codetemplatedata", nbt.toString());

                item.putSubTag("PublicBukkitValues", publicBukkitNbt);

                item.setCustomName(new LiteralText("Imported Code Template"));
                item.setCount(1);

                mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
                mc.player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 200, 1);
            } catch (Exception e) {
                System.out.println("Socket ran into a problem! Shutting down");
                MinecraftCommunicator.status = false;
                break;
            }
        }
    }
}
