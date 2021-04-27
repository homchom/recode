package io.github.codeutilities.audio;

import com.google.gson.stream.JsonReader;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.util.ToasterUtil;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import jdk.nashorn.internal.parser.JSONParser;
import jdk.nashorn.internal.runtime.JSONFunctions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.*;
import javax.sound.sampled.*;

public class AudioHandler {
    String currentPlotId = "";
    HashMap<String, HashSet<AudioClip>> tracks = new HashMap<>();
    public AudioHandler() throws MalformedURLException {
        try {
            com.sun.javafx.application.PlatformImpl.startup(() -> {
                URI uri = URI.create(ModConfig.getConfig().audioUrl);
                String username = MinecraftClient.getInstance().getSession().getUsername();
                IO.Options options = IO.Options.builder()
                        .setQuery("username="+username+"&source="+("forge-"+CodeUtilities.MOD_ID+"-"+CodeUtilities.MOD_VERSION))
                        .build();
                Socket socket = IO.socket(uri, options);
                socket.on("message", args -> {
                    JSONObject obj = new JSONObject(args[0].toString());
                    String action = (String) obj.get("action");
                    if (action.equals("play")) {
                        try {
                            String plotId = (String) obj.get("plot");
                            String track = (String) obj.get("track");
                            String source = (String) obj.get("source");
                            if (!currentPlotId.equals(plotId)) {
                                ToasterUtil.sendToaster("Now Playing", "Plot " + plotId, SystemToast.Type.NARRATOR_TOGGLE);
                                currentPlotId = plotId;
                            }
                            AudioClip player = new AudioClip(source);
                            player.play();
                            HashSet<AudioClip> clips = tracks.get(track);
                            if (clips == null) {
                                clips = new HashSet<>();
                            }
                            clips.add(player);
                            tracks.put(track, clips);
                        } catch (
                                Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ;
                    if (action.equals("stop")) {
                        String plotId = (String) obj.get("plot");
                        String track = (String) obj.get("track");
                        if (!currentPlotId.equals(plotId)) {
                            ToasterUtil.sendToaster("Now Playing", "Plot " + plotId, SystemToast.Type.NARRATOR_TOGGLE);
                            currentPlotId = plotId;
                        }
                        if (track.equals("all")) {
                            for (Map.Entry<String, HashSet<AudioClip>> trackList : tracks.entrySet()) {
                                for (AudioClip audio : trackList.getValue()) {
                                    audio.stop();
                                }
                            }
                        }
                        HashSet<AudioClip> clips = tracks.computeIfAbsent(track, k -> new HashSet<AudioClip>());
                        for (AudioClip audio : clips) {
                            audio.stop();
                        }
                    }
                });
                socket.connect();
            });
            } catch (Exception err) {
            err.printStackTrace();
        }
    }
}