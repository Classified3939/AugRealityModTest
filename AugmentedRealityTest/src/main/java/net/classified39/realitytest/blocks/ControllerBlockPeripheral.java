package net.classified39.realitytest.blocks;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.util.IDAssigner;
import net.classified39.realitytest.RealityMod;
import net.classified39.realitytest.RealityModClient;
import net.classified39.realitytest.client.TestHudOverlay;
import net.classified39.realitytest.util.ARAction;
import net.classified39.realitytest.util.ARState;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.squiddev.cobalt.*;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ControllerBlockPeripheral implements IPeripheral {
    private final ControllerBlockEntity controller_block_entity;
    private int ID = -1;
    private ARState bufferState = null;

    private final TestHudOverlay overlay = RealityModClient.getOverlay();

    private ARState getCurrentState(){
        return overlay.getOverlayTex(ID).getState();
    }

    private void checkIfBufferIsNull(){
        if (this.bufferState == null){
            RealityMod.LOGGER.info("NULL BUFFER");
            this.bufferState = new ARState(getCurrentState());
        }
    }

    public int getID(){
        return ID;
    }

    public void write(@Nonnull NbtCompound tag) {
        if (this.ID >= 0) {
            tag.putInt("ARControllerId", this.ID);
        }
        else{
            RealityMod.LOGGER.info("WRITING NEW ID");
            this.ID = IDAssigner.getNextId("ar_controller");
            tag.putInt("ARControllerId",this.ID);
            controller_block_entity.markDirty();
        }
    }

    public void read(@Nonnull NbtCompound tag) {
        RealityMod.LOGGER.info("READING NBT");
        this.ID = tag.contains("ARControllerId") ? tag.getInt("ARControllerId") : -1;
    }


    ControllerBlockPeripheral(ControllerBlockEntity controller_block_entity) {
        this.controller_block_entity = controller_block_entity;
    }

    @NotNull
    @Override
    public String getType() {
        return "ar_controller";
    }



    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return this == other || other instanceof ControllerBlockPeripheral controller && controller.controller_block_entity == controller_block_entity;
    }

    @LuaFunction(mainThread = true)
    public final void setPixel(int x, int y, int color){
        checkIfBufferIsNull();
        int[] args = new int[]{x,y,color};
        this.bufferState = new ARAction().renderAction(this.ID,this.bufferState, ARAction.RenderType.DRAWPIXEL,args);
    }

    @LuaFunction(mainThread = true)
    public final void horizontalLine(int startX, int endX, int y, int color) throws LuaError {
        if (startX == endX) {throw new LuaError("Line must be at least one pixel long!");}
        checkIfBufferIsNull();
        int minX = Integer.min(startX,endX);
        int maxX = Integer.max(startX,endX);
        int[] args = new int[] {minX,maxX,y, color};
        this.bufferState = new ARAction().renderAction(this.ID,this.bufferState,ARAction.RenderType.DRAW_H_LINE,args);
    }

    @LuaFunction(mainThread = true)
    public final void verticalLine(int startY, int endY, int x, int color) throws LuaError {
        if (startY == endY) {throw new LuaError("Line must be at least one pixel long!");}
        checkIfBufferIsNull();
        int minY = Integer.min(startY,endY);
        int maxY = Integer.max(startY,endY);
        int[] args = new int[] {minY,maxY,x, color};
        this.bufferState = new ARAction().renderAction(this.ID,this.bufferState,ARAction.RenderType.DRAW_V_LINE,args);
    }

    @LuaFunction(mainThread = true)
    public final void fillSquare(int startX, int startY, int width,  int height, int color) {
        int[] args = new int[] {startX, startY, width, height, color};
        checkIfBufferIsNull();
        this.bufferState = new ARAction().renderAction(this.ID,this.bufferState,ARAction.RenderType.FILLSQUARE,args);
    }

    @LuaFunction(mainThread = true)
    public final void clearScreen() {
        checkIfBufferIsNull();
        this.bufferState = new ARAction().renderAction(this.ID,this.bufferState,ARAction.RenderType.CLEAR,null);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult screenSize(){
        checkIfBufferIsNull();
        return MethodResult.of(this.bufferState.screenWidth,this.bufferState.screenHeight);
    }

    @LuaFunction(mainThread=true)
    public final int getColorFromRGBA(int red, int green, int blue, int alpha){
        return NativeImage.packColor(alpha,blue,green,red);
    }

    @LuaFunction(mainThread = true)
    public final int getColorFromHex(String hex){
        Color c = Color.decode(hex);
        return NativeImage.packColor(c.getAlpha(),c.getBlue(),c.getGreen(),c.getRed());
    }

    @LuaFunction(mainThread = true)
    public final void drawIntTable(int startX, int startY, LuaTable pixels) throws LuaError {
        checkIfBufferIsNull();
        LuaValue[] pixelsAsArray = getAllTableValues(pixels);
        if (pixelsAsArray == null) {
            throw new LuaError("Pixel table was empty!");
        }
        for (int i = 0; i < pixelsAsArray.length; i++){
            if (pixelsAsArray[i].isTable()){
                LuaValue[] pixelRowArray = getAllTableValues((LuaTable)pixelsAsArray[i]);
                if (pixelRowArray != null){
                    for (int j = 0; j < pixelRowArray.length; j++){
                        int[] args = new int[]{j+startX,i+startY,pixelRowArray[j].toInteger()};
                        this.bufferState = new ARAction().renderAction(this.ID,this.bufferState, ARAction.RenderType.DRAWPIXEL,args);
                    }
                }
            }
        }
    }


    @LuaFunction(mainThread = true)
    public final void updateHUD(){
        checkIfBufferIsNull();
        new ARAction().renderAction(this.ID,this.bufferState, ARAction.RenderType.UPDATEHUD, null);
        this.bufferState = getCurrentState();
    }

    @LuaFunction(mainThread = true)
    public final int getControllerID(){
        return this.ID;
    }

    private LuaValue[] getAllTableValues(LuaTable table) throws LuaError {
        LuaValue key = Constants.NIL;
        List<LuaValue> luaList = new ArrayList<LuaValue>();
        do {
            Varargs n = table.inext(key);
            key = n.arg(1);
            if (!key.isNil()) {
                luaList.add(n.arg(2));
            }
        }while (!key.isNil()) ;
        if (luaList.isEmpty()){return null;}
        else{return luaList.toArray(new LuaValue[0]);}
    }

    @LuaFunction(mainThread=true)
    public final int getColorFromName(String name){
        switch (name.toLowerCase()) {
            case "white" -> {
                return getColorFromHex("#F0F0F0");
            }
            case "orange" -> {
                return getColorFromHex("#F2B233");
            }
            case "magenta" -> {
                return getColorFromHex("#E57FD8");
            }
            case "lightblue" -> {
                return getColorFromHex("#99B2F2");
            }
            case "yellow" -> {
                return getColorFromHex("#DEDE6C");
            }
            case "lime" -> {
                return getColorFromHex("#7FCC19");
            }
            case "pink" -> {
                return getColorFromHex("#F2B2CC");
            }
            case "gray", "grey" -> {
                return getColorFromHex("#4C4C4C");
            }
            case "lightgray","lightgrey" -> {
                return getColorFromHex("#999999");
            }
            case "cyan" -> {
                return getColorFromHex("#4C99B2");
            }
            case "purple" -> {
                return getColorFromHex("#B266E5");
            }
            case "blue" -> {
                return getColorFromHex("#3366CC");
            }
            case "brown" -> {
                return getColorFromHex("#7F664C");
            }
            case "green" -> {
                return getColorFromHex("#57A64E");
            }
            case "red" -> {
                return getColorFromHex("#CC4C4C");
            }
            case "black" -> {
                return getColorFromHex("#111111");
            }
        }
        return 0;
    }

    @LuaFunction(mainThread=true)
    public final int getColorFromBlit(String ch){
        switch (ch.toLowerCase()) {
            case "0" -> {
                return getColorFromHex("#F0F0F0");
            }
            case "1" -> {
                return getColorFromHex("#F2B233");
            }
            case "2" -> {
                return getColorFromHex("#E57FD8");
            }
            case "3" -> {
                return getColorFromHex("#99B2F2");
            }
            case "4" -> {
                return getColorFromHex("#DEDE6C");
            }
            case "5" -> {
                return getColorFromHex("#7FCC19");
            }
            case "6" -> {
                return getColorFromHex("#F2B2CC");
            }
            case "7" -> {
                return getColorFromHex("#4C4C4C");
            }
            case "8" -> {
                return getColorFromHex("#999999");
            }
            case "9" -> {
                return getColorFromHex("#4C99B2");
            }
            case "a" -> {
                return getColorFromHex("#B266E5");
            }
            case "b" -> {
                return getColorFromHex("#3366CC");
            }
            case "c" -> {
                return getColorFromHex("#7F664C");
            }
            case "d" -> {
                return getColorFromHex("#57A64E");
            }
            case "e" -> {
                return getColorFromHex("#CC4C4C");
            }
            case "f" -> {
                return getColorFromHex("#111111");
            }
        }
        return 0;
    }
}
