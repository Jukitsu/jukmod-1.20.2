package net.jukitsumc.jukmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.jukitsumc.jukmod.entity.Human;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.jukitsumc.jukmod.client.renderer.HumanRenderer;
import net.jukitsumc.jukmod.client.renderer.HumanModel;
import net.jukitsumc.jukmod.config.ModConfig;

import java.nio.file.Path;

public class Jukmod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("jukmod");
    public static final String MOD_ID = "jukmod";
    private static Jukmod instance;

    public ModConfig config;
    private ModContainer modContainer;
    private ModMetadata modMetadata;

    public ModConfig getConfig() { return config; }

    public static final EntityType<Human> HUMAN = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(Jukmod.MOD_ID, "human"),
            FabricEntityTypeBuilder.create(MobCategory.AMBIENT, Human::new).dimensions(EntityDimensions.fixed(0.6f, 1.8f)).build()
    );

    public static Jukmod getInstance() { return instance; }
    public Logger getLogger() {
        return LOGGER;
    }

    public static final Item HUMAN_SPAWN_EGG = new SpawnEggItem(HUMAN, 0xc4c4c4, 0xadadad, new FabricItemSettings());

    @Override
    public void onInitialize() {
        instance = this;

        LOGGER.info("0.24 (\"1.8\") Miss Penalty Removed ! :D");
        LOGGER.info("Old hit color initialized, old corpse animation loading...");
        LOGGER.info("Fixed MC-69655, MC-120955 and MC-147694");

        FabricLoader loader = FabricLoader.getInstance();

        modContainer = loader.getModContainer(MOD_ID).orElseThrow(() ->
                new IllegalStateException("Could not find own mod container!"));
        modMetadata = modContainer.getMetadata();

        Path configDir = loader.getConfigDir().resolve(MOD_ID);
        config = new ModConfig(this, configDir.resolve("config.toml"));


        EntityRendererRegistry.register(Jukmod.HUMAN, HumanRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(HumanModel.LAYER_LOCATION, HumanModel::createBodyLayer);

        FabricDefaultAttributeRegistry.register(HUMAN, Human.createHumanAttributes());
        BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld(),
                MobCategory.CREATURE, HUMAN, 2, 1, 6);
        SpawnPlacements.register(HUMAN, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Human::checkHumanSpawnRules);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("jukmod", "human_spawn_egg"), HUMAN_SPAWN_EGG);

    }
}