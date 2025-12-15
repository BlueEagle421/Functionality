package com.blueeagle421.functionality.mixin.throwableDiscs;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.blueeagle421.functionality.config.subcategories.features.ThrowableDiscs;

@Mixin(net.minecraft.world.item.Items.class)
public class ItemsMixin {

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/RecordItem;<init>(ILnet/minecraft/sounds/SoundEvent;Lnet/minecraft/world/item/Item$Properties;I)V"), index = 2)
    private static Item.Properties addDurabilityToRecordItem(Item.Properties props) {
        return props.durability(ThrowableDiscs.DURABILITY);
    }
}
