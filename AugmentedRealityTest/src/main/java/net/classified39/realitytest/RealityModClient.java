package net.classified39.realitytest;

import net.classified39.realitytest.client.TestHudOverlay;
import net.classified39.realitytest.util.ARRenderer;
import net.classified39.realitytest.util.ARState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;

public class RealityModClient implements ClientModInitializer {

    private static final TestHudOverlay overlay = new TestHudOverlay();

    @Override
    public void onInitializeClient() {

        HudRenderCallback.EVENT.register(overlay);

    }

    public static TestHudOverlay getOverlay(){
        return overlay;
    }
}
