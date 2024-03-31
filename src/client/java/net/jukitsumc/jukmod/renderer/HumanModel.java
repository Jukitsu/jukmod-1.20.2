package net.jukitsumc.jukmod.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.jukitsumc.jukmod.entity.Human;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class HumanModel extends HumanoidModel<Human> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("jukmod", "humanmodel"), "main");


    public HumanModel(ModelPart modelPart) {
        super(modelPart);
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = createMesh(new CubeDeformation(0.0F), 0.0F);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }


}
