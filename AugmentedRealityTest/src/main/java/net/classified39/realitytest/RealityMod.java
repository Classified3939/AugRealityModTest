package net.classified39.realitytest;

import net.classified39.realitytest.blocks.ControllerBlock;
import net.classified39.realitytest.blocks.ControllerBlockEntity;
import net.classified39.realitytest.items.ArGogglesItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RealityMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "realitytest";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ControllerBlock CONTROLLER_BLOCK = new ControllerBlock();
	public static final BlockEntityType<ControllerBlockEntity> CONTROLLER_BLOCK_ENTITY =
					Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "controller_block_entity"),
					FabricBlockEntityTypeBuilder.create(ControllerBlockEntity::new, CONTROLLER_BLOCK).build(null));
	public static final ArGogglesItem AR_GOGGLES_ITEM = new ArGogglesItem();

	@Override
	public void onInitialize() {
		LOGGER.info("Augmented Reality Online...");
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "controller_block"), CONTROLLER_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID,"controller_block"),
				new BlockItem(CONTROLLER_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "ar_goggles_item"),AR_GOGGLES_ITEM);
	}
}
