package io.github.homchom.recode.mod.features;

import com.google.gson.*;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.file.ILoader;
import io.github.homchom.recode.sys.renderer.ToasterUtil;
import io.socket.client.*;
import javafx.scene.media.*;
import javafx.util.Duration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.sounds.SoundSource;

import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

public class AudioHandler implements ILoader {
    private static AudioHandler instance;
    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(1);

    private final Map<String, Set<MediaPlayer>> tracks = new HashMap<>();
    private boolean isActive = true;

    public AudioHandler() {
        instance = this;
    }

    @Override
    public void load() {
        try {
            String os = System.getProperty("os.name");
            if (!os.toLowerCase().contains("windows")) {
                ToasterUtil.sendToaster("Not Supported", "P:A is not supported on " + os + ".", SystemToast.SystemToastIds.NARRATOR_TOGGLE);
                return;
            }
            com.sun.javafx.application.PlatformImpl.startup(() -> {
                URI uri = URI.create(Config.getString("audioUrl"));
                String username = Minecraft.getInstance().getUser().getName();
                IO.Options options = IO.Options.builder()
                        .setQuery("username=" + username + "&source=" + ("fabric-" + Recode.MOD_ID + "-" + Recode.MOD_VERSION))
                        .build();

                Socket socket = IO.socket(uri, options);
                socket.on("message", args -> {
                    if (!isActive) return;
                    String jsonText = args[0].toString();
                    JsonObject json = JsonParser.parseString(jsonText).getAsJsonObject();
                    String action = json.get("action").getAsString();

                    if (action.equals("play")) {
                        try {
                            String plotId = json.get("plot").getAsString();
                            String track = json.get("track").getAsString();
                            String source = json.get("source").getAsString();
                            String title = json.get("title").getAsString();
                            boolean loop = json.get("loop").getAsBoolean();

                            Media mediaSource = new Media(source);
                            MediaPlayer player = new MediaPlayer(mediaSource);
                            if (loop) {
                                player.setOnEndOfMedia(() -> {
                                    player.seek(Duration.ZERO);
                                    player.play();
                                });
                            }
                            player.play();
                            Set<MediaPlayer> clips = tracks.getOrDefault(track, new HashSet<>());
                            clips.add(player);
                            tracks.put(track, clips);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (action.equals("stop")) {
                        String plotId = json.get("plot").getAsString();
                        String track = json.get("track").getAsString();
                        if (track.equals("all")) {
                            for (Map.Entry<String, Set<MediaPlayer>> trackList : tracks.entrySet()) {
                                for (MediaPlayer audio : trackList.getValue()) {
                                    audio.stop();
                                }
                            }
                        }
                        Set<MediaPlayer> clips = tracks.computeIfAbsent(track, k -> new HashSet<>());
                        for (MediaPlayer audio : clips) {
                            audio.stop();
                        }
                    }

                    Runnable handleVolume = () -> {
                        Minecraft mc = Recode.MC;

                        for (Map.Entry<String, Set<MediaPlayer>> trackList : tracks.entrySet()) {
                            for (MediaPlayer audio : trackList.getValue()) {
                                float volumeMaster = mc.options.getSoundSourceVolume(SoundSource.MASTER);
                                float volumeRecords = mc.options.getSoundSourceVolume(SoundSource.RECORDS);
                                float volume = volumeMaster * volumeRecords;
                                audio.setVolume(volume);
                            }
                        }
                    };
                    SERVICE.scheduleAtFixedRate(handleVolume, 0, 1, TimeUnit.SECONDS);
                });
                socket.connect();
            });
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void setActive(boolean isActive) {
        if (this.isActive != isActive) {
            this.isActive = isActive;
            if (!isActive) for (Map.Entry<String, Set<MediaPlayer>> trackList : tracks.entrySet()) {
                for (MediaPlayer audio : trackList.getValue()) {
                    audio.stop();
                }
            }
        }
    }

    public static AudioHandler getInstance() {
        return instance;
    }
}