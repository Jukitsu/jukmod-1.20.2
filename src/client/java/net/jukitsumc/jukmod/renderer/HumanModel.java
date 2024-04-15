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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;

public class HumanModel extends HumanoidModel<Human> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("jukmod", "humanmodel"), "main");


    public HumanModel(ModelPart modelPart) {
        super(modelPart, RenderType::entityTranslucent);
    }

    public static MeshDefinition createMesh() {
        MeshDefinition meshdefinition = createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partDefinition = meshdefinition.getRoot();
        partDefinition.addOrReplaceChild("left_arm",
                CubeListBuilder.create()
                        .texOffs(32, 48)
                        .addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation.NONE),
                PartPose.offset(5.0f, 2.0f, 0.0f));
        partDefinition.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(16, 48)
                        .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation.NONE),
                PartPose.offset(1.9f, 12.0f, 0.0f));
        return meshdefinition;
    }


    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(createMesh(), 64, 64);
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
        else if (itemStack.getItem() instanceof SwordItem && mob.isSwordBlocking()) {
            if (mob.getMainArm() == HumanoidArm.RIGHT) {
                this.rightArmPose = ArmPose.BLOCK;
            } else {
                this.leftArmPose = ArmPose.BLOCK;
            }
        }

        super.prepareMobModel(mob, f, g, h);
    }


}
