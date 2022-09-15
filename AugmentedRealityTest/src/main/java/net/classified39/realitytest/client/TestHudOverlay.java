package net.classified39.realitytest.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.classified39.realitytest.RealityMod;
import net.classified39.realitytest.RealityModClient;
import net.classified39.realitytest.util.ARRenderer;
import net.classified39.realitytest.util.ARState;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Arrays;


public class TestHudOverlay implements HudRenderCallback {
    private ARRenderer arRenderer = new ARRenderer(MinecraftClient.getInstance().getTextureManager());
    private int overlayID = -1;
    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        if (overlayID >= 0) {
            renderAROverlay(matrixStack, overlayID);
        }
    }

    public void setOverlayID(int newID){
        overlayID = newID;
    }

    public int getOverlayID(){
        return overlayID;
    }

    public ARRenderer.ARTexture getOverlayTex(int id){
        if (arRenderer.getARTexture(id) == null){
            RealityMod.LOGGER.info("NULL TEXTURE");
            arRenderer = new ARRenderer(MinecraftClient.getInstance().getTextureManager());
        }
        return arRenderer.getARTexture(id);
    }

    public void renderAROverlay(MatrixStack matrixStack, int id){
        ARRenderer.ARTexture texture = getOverlayTex(id);
        if (texture != null) {
            //RealityMod.LOGGER.info("RENDER");
            ARState state = texture.getState();
            int x = state.screenWidth;
            int y = state.screenHeight;
            RenderSystem.setShaderTexture(0, texture.identifier);
            DrawableHelper.drawTexture(matrixStack, 0, 0, x, y, 0, 0, x, y, x, y);
            texture.setNeedsUpdate(false);
        }
    }

    public void updateAROverlay(int id,ARState state){
        ARRenderer.ARTexture texture = getOverlayTex(id);
        RealityMod.LOGGER.info("UPDATE");
        texture.setState(state);
    }
}
