package net.classified39.realitytest.blocks;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralTile;
import net.classified39.realitytest.RealityMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ControllerBlockEntity extends BlockEntity implements IPeripheralTile {
    private ControllerBlockPeripheral peripheral;

    public ControllerBlockEntity(BlockPos pos, BlockState state) {
        super(RealityMod.CONTROLLER_BLOCK_ENTITY, pos, state);
    }

    public ControllerBlockPeripheral getSpecificPeripheral(){
        if (peripheral == null){
            peripheral = new ControllerBlockPeripheral(this);
        }
        return this.peripheral;
    }

    @Nullable
    @Override
    public IPeripheral getPeripheral(@NotNull Direction side) {
        if (peripheral == null){
            peripheral = new ControllerBlockPeripheral(this);
        }
        return peripheral;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {

        this.getSpecificPeripheral().write(nbt);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.getSpecificPeripheral().read(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
