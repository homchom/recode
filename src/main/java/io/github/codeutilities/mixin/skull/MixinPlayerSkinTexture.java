package io.github.codeutilities.mixin.skull;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

@Mixin(PlayerSkinTexture.class)
public class MixinPlayerSkinTexture extends ResourceTexture {

    private final ExecutorService POOL = Executors.newCachedThreadPool();

    @Final
    @Shadow
    private static Logger LOGGER = LogManager.getLogger();

    @Final
    @Shadow
    private File cacheFile;

    @Final
    @Shadow
    private String url;

    @Final
    @Shadow
    private boolean convertLegacy;

    @Final
    @Shadow
    private Runnable loadedCallback;

    @Shadow
    private CompletableFuture<?> loader;

    @Shadow
    private boolean loaded;

    private MixinPlayerSkinTexture(Identifier location) {
        super(location);
    }

    @Inject(method = "load", at = @At("HEAD"), cancellable = true)
    public void load(ResourceManager manager, CallbackInfo ci) {
        ci.cancel();

        CompletableFuture.runAsync(() -> {
                if (!this.loaded) {
                    try {
                        super.load(manager);
                    } catch (IOException var3) {
                        LOGGER.warn("Failed to load texture: {}", this.location, var3);
                    }

                    this.loaded = true;
                }

            }, POOL);
        if (this.loader == null) {
            CompletableFuture.runAsync(() -> {
                NativeImage nativeImage2;
                if (this.cacheFile != null && this.cacheFile.isFile()) {
                    LOGGER.debug("Loading http texture from local cache ({})", this.cacheFile);
                    FileInputStream fileInputStream = null;
                    try {
                        fileInputStream = new FileInputStream(this.cacheFile);
                    } catch (FileNotFoundException exception) {
                        exception.printStackTrace();
                    }
                    nativeImage2 = this.loadTexture(fileInputStream);
                } else {
                    nativeImage2 = null;
                }

                if (nativeImage2 != null) {
                    this.onTextureLoaded(nativeImage2);
                } else {
                    this.loader = CompletableFuture.runAsync(() -> {
                        HttpURLConnection httpURLConnection = null;
                        LOGGER.debug("Downloading http texture from {} to {}", this.url, this.cacheFile);

                        try {
                            httpURLConnection = (HttpURLConnection) (new URL(this.url)).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                            httpURLConnection.setDoInput(true);
                            httpURLConnection.setDoOutput(false);
                            httpURLConnection.connect();
                            if (httpURLConnection.getResponseCode() / 100 == 2) {
                                InputStream inputStream2;
                                if (this.cacheFile != null) {
                                    FileUtils.copyInputStreamToFile(httpURLConnection.getInputStream(), this.cacheFile);
                                    inputStream2 = new FileInputStream(this.cacheFile);
                                } else {
                                    inputStream2 = httpURLConnection.getInputStream();
                                }

                                MinecraftClient.getInstance().execute(() -> {
                                    NativeImage nativeImage = this.loadTexture(inputStream2);
                                    if (nativeImage != null) {
                                        this.onTextureLoaded(nativeImage);
                                    }

                                });
                            }
                        } catch (Exception var6) {
                            LOGGER.error("Couldn't download http texture", var6);
                        } finally {
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }

                        }

                    }, POOL);
                }


            }, POOL);


        }


    }

    //DUMMY METHODS

    @Shadow
    private void onTextureLoaded(NativeImage image) {
    }

    @Shadow
    private NativeImage loadTexture(InputStream stream) {
        return null;
    }
}
