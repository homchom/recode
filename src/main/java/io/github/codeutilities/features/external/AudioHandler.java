package io.github.codeutilities.features.external;

import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.Config;
import io.github.codeutilities.util.file.ILoader;
import io.github.codeutilities.util.render.ToasterUtil;
import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.sound.SoundCategory;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AudioHandler implements ILoader {
    private static AudioHandler instance;
    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(1);

    private String currentPlotId = "";
    private final Map<String, Set<MediaPlayer>> tracks = new HashMap<>();

    public AudioHandler() {
        instance = this;
    }

    @Override
    public void load() {
        try {
            String os = System.getProperty("os.name");
            if(!os.toLowerCase().contains("windows")) {
                ToasterUtil.sendToaster("Not Supported","P:A is not supported on "+os+".",SystemToast.Type.NARRATOR_TOGGLE);
                return;
            }
            com.sun.javafx.application.PlatformImpl.startup(() -> {
                URI uri = URI.create(Config.getString("audioUrl"));
                String username = MinecraftClient.getInstance().getSession().getUsername();
                IO.Options options = IO.Options.builder()
                        .setQuery("username=" + username + "&source=" + ("fabric-" + CodeUtilities.MOD_ID + "-" + CodeUtilities.MOD_VERSION))
                        .build();

                Socket socket = IO.socket(uri, options);
                socket.on("message", args -> {
                    String jsonText = args[0].toString();
                    JsonObject json = CodeUtilities.JSON_PARSER.parse(jsonText).getAsJsonObject();
                    String action = json.get("action").getAsString();

                    if (action.equals("play")) {
                        try {
                            String plotId = json.get("plot").getAsString();
                            String track = json.get("track").getAsString();
                            String source = json.get("source").getAsString();
                            String title = json.get("title").getAsString();
                            boolean loop = json.get("loop").getAsBoolean();

                            if (!currentPlotId.equals(plotId)) {
                                // enable to show when plot takes control of session
                                if (Config.getBoolean("audioAlerts")) {
                                    ToasterUtil.sendToaster("Now Playing", "Plot " + plotId, SystemToast.Type.NARRATOR_TOGGLE);
                                }
                                currentPlotId = plotId;
                            }
                            if (CodeUtilities.BETA) {
                                ToasterUtil.sendToaster("Debug Playback - " + plotId, title, SystemToast.Type.NARRATOR_TOGGLE);
                            }

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
                        if (!currentPlotId.equals(plotId)) {
                            ToasterUtil.sendToaster("Now Playing", "Plot " + plotId, SystemToast.Type.NARRATOR_TOGGLE);
                            currentPlotId = plotId;
                        }
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
                        MinecraftClient mc = CodeUtilities.MC;

                        for (Map.Entry<String, Set<MediaPlayer>> trackList : tracks.entrySet()) {
                            for (MediaPlayer audio : trackList.getValue()) {
                                float volumeMaster = mc.options.getSoundVolume(SoundCategory.MASTER);
                                float volumeRecords = mc.options.getSoundVolume(SoundCategory.RECORDS);
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

    public static AudioHandler getInstance() {
        return instance;
    }
}