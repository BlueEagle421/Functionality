package com.blueeagle421.functionality.mixin.throwableDiscs;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.blueeagle421.functionality.config.subcategories.features.ThrowableDiscs;

@Mixin(RecordItem.class)
public abstract class ItemsMixin {

    // Constructor 1
    @ModifyArg(method = "<init>(ILjava/util/function/Supplier;Lnet/minecraft/world/item/Item$Properties;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;<init>(Lnet/minecraft/world/item/Item$Properties;)V"), index = 0)
    private static Item.Properties modifyPropsSupplier(Item.Properties props) {
        return props.durability(ThrowableDiscs.DURABILITY);
    }

    // Constructor 2
    @ModifyArg(method = "<init>(ILnet/minecraft/sounds/SoundEvent;Lnet/minecraft/world/item/Item$Properties;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;<init>(Lnet/minecraft/world/item/Item$Properties;)V"), index = 0)
    private static Item.Properties modifyPropsSound(Item.Properties props) {
        return props.durability(ThrowableDiscs.DURABILITY);
    }
}