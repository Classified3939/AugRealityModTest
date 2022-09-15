package net.classified39.realitytest.util;

import net.classified39.realitytest.RealityMod;
import net.classified39.realitytest.RealityModClient;
import net.classified39.realitytest.client.TestHudOverlay;
import net.minecraft.client.texture.NativeImage;

import javax.annotation.Nullable;
import java.util.Arrays;

public class ARAction {
    TestHudOverlay overlay = RealityModClient.getOverlay();

    public ARState renderAction(int id, ARState inputState, RenderType action, @Nullable int[] args){
        if (args == null && (action != RenderType.CLEAR && action != RenderType.UPDATEHUD)){
            return inputState;
        }
        switch (action) {
            case DRAWPIXEL -> {
                inputState.setColor(args[0],args[1],args[2]);
            }
            case DRAW_H_LINE -> {
                int width = args[1] - args[0];
                for (int i = 0; i < width; i++) {
                    inputState.setColor(i, args[2], args[3]);
                }
            }
            case DRAW_V_LINE -> {
                int height = args[1] - args[0];
                for (int i = 0; i < height; i++) {
                    inputState.setColor(args[2], i, args[3]);
                }
            }
            case CLEAR -> {
                inputState.resetColors();
            }
            case FILLSQUARE -> {
                for (int i = 0; i < args[2]; i++){
                    for (int j = 0; j < args[3]; j++){
                        inputState.setColor(i+args[0],j+args[1],args[4]);
                    }
                }
            }
            case UPDATEHUD -> {
                overlay.updateAROverlay(id,inputState);
            }
        }
        return inputState;
    }

    public enum RenderType{
        DRAW_H_LINE,
        DRAW_V_LINE,
        CLEAR,
        FILLSQUARE,
        UPDATEHUD,
        DRAWPIXEL
    }
}
