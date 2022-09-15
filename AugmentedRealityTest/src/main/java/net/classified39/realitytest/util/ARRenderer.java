package net.classified39.realitytest.util;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.classified39.realitytest.RealityMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class ARRenderer implements AutoCloseable {
    final TextureManager textureManager;
    private final Int2ObjectMap<ARRenderer.ARTexture> arTextures = new Int2ObjectOpenHashMap<ARTexture>();
    public ARRenderer(TextureManager textureManager) {
        this.textureManager = textureManager;
    }


    public void updateTexture(int id) {
        this.getARTexture(id).setNeedsUpdate(true);
    }

    @Environment(EnvType.CLIENT)
    public ARRenderer.ARTexture getARTexture(int id) {
        if (this.textureManager == null){
            return null;
        }
        return this.arTextures.compute(id, (id2, texture) -> {
            if (texture == null) {
                RealityMod.LOGGER.info("RETURNING NEW TEXTURE");
                return new ARTexture(id2, this.textureManager);
            }
            texture.updateTexture();
            return texture;
        });
    }

    public void clearStateTextures() {
        for (ARRenderer.ARTexture arTexture : this.arTextures.values()) {
            arTexture.close();
        }
        this.arTextures.clear();
    }

    @Override
    public void close() {
        this.clearStateTextures();
    }


    public class ARTexture implements AutoCloseable {

        private ARState state;
        private NativeImageBackedTexture texture;
        private final RenderLayer renderLayer;
        public boolean needsUpdate = true;
        public Identifier identifier;

        ARTexture(int id, TextureManager textureManager) {
            int x = 128;
            int y = 128;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getWindow() != null) {
                int width = client.getWindow().getScaledWidth();
                int height = client.getWindow().getScaledHeight();
                x = width;
                y = height;
            }

            this.state = new ARState(x,y);

            this.texture = new NativeImageBackedTexture(x, y, true);
            textureManager.registerDynamicTexture("ar/" + id, this.texture);
            this.identifier = new Identifier(String.format(Locale.ROOT, "dynamic/%s_%d", "ar/" + id, 1));
            this.renderLayer = RenderLayer.getText(this.identifier);
        }

        public void setState(ARState state) {
            boolean bl = this.state.colors != state.colors;
            this.state = state;
            this.needsUpdate = bl;
        }

        public ARState getState(){
            return state;
        }

        public void setNeedsUpdate(boolean b) {
            this.needsUpdate = b;
        }


        private void updateTexture() {
            for (int i = 0; i < this.state.screenWidth; ++i) {
                for (int j = 0; j < this.state.screenHeight; ++j) {
                    this.texture.getImage().setColor(i,j,this.state.colors[i][j]);
                }
            }
            this.texture.upload();
        }

        @Override
        public void close() {
            this.texture.close();
        }
    }
}
