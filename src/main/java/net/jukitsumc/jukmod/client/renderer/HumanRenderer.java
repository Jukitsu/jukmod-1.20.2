package net.jukitsumc.jukmod.client.renderer;

import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.entity.Human;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class HumanRenderer extends HumanoidMobRenderer<Human, HumanModel> {

    public HumanRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanModel(context.bakeLayer(HumanModel.LAYER_LOCATION)), 0.5f);
        this.addLayer(new HumanoidArmorLayer(this,
                new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager())
        );
    }

    @Override
    public ResourceLocation getTextureLocation(Human entity) {
        return new ResourceLocation(Jukmod.MOD_ID, "human.png");
    }

}
