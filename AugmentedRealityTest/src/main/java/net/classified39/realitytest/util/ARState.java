package net.classified39.realitytest.util;

import net.classified39.realitytest.RealityMod;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class ARState extends PersistentState {

    public int screenWidth;
    public int screenHeight;
    public int[][] colors;

    public ARState(int width, int height){
        this.screenWidth = width;
        this.screenHeight = height;
        this.colors = new int[width][height];
        resetColors();
    }

    public ARState(ARState oldState){
        this.screenHeight = oldState.screenHeight;
        this.screenWidth = oldState.screenWidth;
        this.colors = new int[oldState.screenWidth][oldState.screenHeight];
        for (int i = 0; i < this.screenWidth; i++){
            System.arraycopy(oldState.colors[i], 0, colors[i], 0, this.screenHeight);
        }
    }

    public void resetColors(){
        int clearPixel = NativeImage.packColor(0,0,0,0);
        for (int i = 0; i < this.screenWidth; i++){
            for (int j = 0; j < this.screenHeight; j++){
                colors[i][j] = clearPixel;
            }
        }
    }

    public boolean putColor(int x, int y, int newColor) {
        int color = this.colors[x][y];
        if (color != newColor) {
            this.setColor(x, y, newColor);
            return true;
        }
        return false;
    }

    public void setColor(int x, int y, int color) {
        this.colors[x][y] = color;
    }


    private int[] convertColorsTo1D(int[][] input){
        List<Integer> tempList = new ArrayList<>();
        for (int[] ints : input) {
            for (int aInt : ints) {
                tempList.add(aInt);
            }
        }
        Integer[] tempArray = tempList.toArray(new Integer[0]);
        //int[] is not int[], so toPrimitive is NEEDED
        return ArrayUtils.toPrimitive(tempArray);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("width",this.screenWidth);
        nbt.putInt("height",this.screenHeight);
        nbt.putDouble("totalPixels",this.colors[0].length * this.colors[1].length);
        nbt.putIntArray("colors",convertColorsTo1D(this.colors));
        return nbt;
    }
}
