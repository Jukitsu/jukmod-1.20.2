package net.jukitsumc.jukmod.renderer;

import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.entity.Human;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;

public class HumanRenderer extends HumanoidMobRenderer<Human, HumanModel> {



    public HumanRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanModel(context.bakeLayer(HumanModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(Human entity) {
        return new ResourceLocation(Jukmod.MOD_ID, "human.png");
    }

}
