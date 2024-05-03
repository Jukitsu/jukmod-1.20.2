package net.jukitsumc.jukmod.mixin.client;

import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IllagerModel.class)
public class IllagerModelMixin<T extends AbstractIllager> {
    @Unique
    private BooleanOption worldWar2 = Jukmod.getInstance().getConfig().animations().worldWar2();

    @Redirect(method = "setupAnim", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    public boolean bringBackOldIllagers(ItemStack stack) {
        return worldWar2.get();
    }
}
