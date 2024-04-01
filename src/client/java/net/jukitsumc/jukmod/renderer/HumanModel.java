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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HumanModel extends HumanoidModel<Human> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("jukmod", "humanmodel"), "main");


    public HumanModel(ModelPart modelPart) {
        super(modelPart);
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = createMesh(new CubeDeformation(0.0F), 0.0F);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void prepareMobModel(Human mob, float f, float g, float h) {
        this.rightArmPose = ArmPose.EMPTY;
        this.leftArmPose = ArmPose.EMPTY;
        ItemStack itemStack = mob.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemStack.is(Items.BOW) && mob.isAggressive()) {
            if (mob.getMainArm() == HumanoidArm.RIGHT) {
                this.rightArmPose = ArmPose.BOW_AND_ARROW;
            } else {
                this.leftArmPose = ArmPose.BOW_AND_ARROW;
            }
        }

        super.prepareMobModel(mob, f, g, h);
    }


}
