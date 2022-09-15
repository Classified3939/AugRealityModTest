package net.classified39.realitytest.items;

import net.classified39.realitytest.RealityMod;
import net.classified39.realitytest.RealityModClient;
import net.classified39.realitytest.blocks.ControllerBlockEntity;
import net.classified39.realitytest.blocks.ControllerBlockPeripheral;
import net.classified39.realitytest.client.TestHudOverlay;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArGogglesItem extends ArmorItem {

    private static final String CONTROLLER_POS = "controller_pos";
    private static final String CONTROLLER_WORLD = "controller_world";

    private static final String CONTROLLER_ID = "controller_id";

    public ArGogglesItem() {
        super(ArmorMaterials.LEATHER, EquipmentSlot.HEAD, new FabricItemSettings().group(ItemGroup.REDSTONE).maxCount(1));
    }

    public static void clientTick(PlayerEntity player, ItemStack stack, int slot){
        if (stack.hasNbt() && stack.getNbt().contains(CONTROLLER_POS) && stack.getNbt().contains(CONTROLLER_WORLD) &&
                stack.getNbt().contains(CONTROLLER_ID)){
            int[] posArr = stack.getNbt().getIntArray(CONTROLLER_POS);
            if (posArr.length < 3) return;
            BlockPos pos = new BlockPos(posArr[0],posArr[1],posArr[2]);
            String dimension = stack.getNbt().getString(CONTROLLER_WORLD);
            World world = player.world;
            if (!dimension.equals(world.getDimension().toString())){
                return;
            }
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof ControllerBlockEntity )){
                stack.getNbt().remove(CONTROLLER_ID);
                stack.getNbt().remove(CONTROLLER_POS);
                stack.getNbt().remove(CONTROLLER_WORLD);
                RealityModClient.getOverlay().setOverlayID(-1);
                return;
            }
            int ID = stack.getNbt().getInt(CONTROLLER_ID);
            ControllerBlockEntity controller = (ControllerBlockEntity) blockEntity;
            ControllerBlockPeripheral peripheral = controller.getSpecificPeripheral();
            if (peripheral.getID() != ID){
                stack.getNbt().putInt(CONTROLLER_ID, peripheral.getID());
                RealityMod.LOGGER.info(String.valueOf(stack.getNbt().getInt(CONTROLLER_ID)));
            }
            if (slot == EquipmentSlot.HEAD.getEntitySlotId() && player.world.isClient()) {
                if (RealityModClient.getOverlay().getOverlayID() != ID){
                    RealityModClient.getOverlay().setOverlayID(ID);
                }
            }
            else{
                RealityModClient.getOverlay().setOverlayID(-1);
            }

        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.isClient()) {
            clientTick((PlayerEntity) entity, stack, slot);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.of("Provides a programmable HUD overlay. Right click an AR Controller Block to link."));
        if (stack.hasNbt() && stack.getNbt().contains(CONTROLLER_POS,NbtCompound.INT_ARRAY_TYPE) &&
                stack.getNbt().contains(CONTROLLER_ID,NbtCompound.INT_TYPE)){
            int[] pos = stack.getNbt().getIntArray(CONTROLLER_POS);
            tooltip.add(Text.of(String.format("Linked to controller at %d %d %d with an ID of %d.",
                    pos[0],pos[1],pos[2],stack.getNbt().getInt(CONTROLLER_ID))));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        if (!world.getBlockState(blockPos).isOf(RealityMod.CONTROLLER_BLOCK)) {
            return super.useOnBlock(context);
        }
        else{
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (!(blockEntity instanceof ControllerBlockEntity)) return super.useOnBlock(context);
            ControllerBlockEntity controller = (ControllerBlockEntity) blockEntity;
            if (!context.getWorld().isClient()) {
                ItemStack item = context.getStack();
                if (!item.hasNbt()) item.setNbt(new NbtCompound());
                NbtCompound nbt = item.getNbt();
                BlockPos pos = controller.getPos();
                nbt.putIntArray(CONTROLLER_POS, new int[]{pos.getX(), pos.getY(), pos.getZ()});
                nbt.putString(CONTROLLER_WORLD, controller.getWorld().getDimension().toString());
                nbt.putInt(CONTROLLER_ID, controller.getSpecificPeripheral().getID());
                item.setNbt(nbt);
            }
            context.getPlayer().sendMessage(Text.of("Goggles Linked!"),true);
            return ActionResult.SUCCESS;
        }
    }
}
