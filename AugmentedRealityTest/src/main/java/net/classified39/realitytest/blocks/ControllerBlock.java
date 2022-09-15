package net.classified39.realitytest.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ControllerBlock extends Block implements BlockEntityProvider {
    public ControllerBlock() {
        super(FabricBlockSettings.of(Material.METAL).strength(2.0f).sounds(BlockSoundGroup.METAL));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ControllerBlockEntity(pos,state);
    }
}
