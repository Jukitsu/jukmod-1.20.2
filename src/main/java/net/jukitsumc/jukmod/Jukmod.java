package net.jukitsumc.jukmod;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.impl.biome.modification.BiomeModificationImpl;
import net.jukitsumc.jukmod.entity.Human;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSources;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.FabricUtil;

public class Jukmod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("jukmod");
    public static final String MOD_ID = "jukmod";



	public static final EntityType<Human> HUMAN = Registry.register(BuiltInRegistries.ENTITY_TYPE,
			new ResourceLocation(Jukmod.MOD_ID, "human"),
			FabricEntityTypeBuilder.create(MobCategory.AMBIENT, Human::new).dimensions(EntityDimensions.fixed(0.6f, 1.8f)).build()
	);

	public static final Item HUMAN_SPAWN_EGG = new SpawnEggItem(HUMAN, 0xc4c4c4, 0xadadad, new FabricItemSettings());

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("1.8 Miss Penalty Removed ! :D");
		FabricDefaultAttributeRegistry.register(HUMAN, Human.createHumanAttributes());
		BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.BEACH, Biomes.FOREST, Biomes.DESERT, Biomes.SAVANNA, Biomes.WINDSWEPT_HILLS, Biomes.SNOWY_PLAINS), MobCategory.CREATURE, HUMAN, 24, 2, 12);
		SpawnPlacements.register(HUMAN, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Human::checkHumanSpawnRules);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("jukmod", "human_spawn_egg"), HUMAN_SPAWN_EGG);

	}
}