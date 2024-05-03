package net.jukitsumc.jukmod.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {

    @Unique
    private BooleanOption deathWalk = Jukmod.getInstance().getConfig().animations().deathWalk();

    protected M model;

    public LivingEntityRendererMixin(EntityRendererProvider.Context context, M entityModel, float f) {
        super(context);
        this.model = entityModel;
        this.shadowRadius = f;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isAlive()Z"))
    private boolean deathWalkAnimation(boolean original) {
        return deathWalk.get() || original;
    }


}
