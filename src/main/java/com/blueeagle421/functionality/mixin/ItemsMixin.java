package com.blueeagle421.functionality.mixin;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(net.minecraft.world.item.Items.class)
public class ItemsMixin {
    private static final int DURABILITY = 32;

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/RecordItem;<init>(ILnet/minecraft/sounds/SoundEvent;Lnet/minecraft/world/item/Item$Properties;I)V"), index = 2)
    private static Item.Properties addDurabilityToRecordItem(Item.Properties props) {
        return props.durability(DURABILITY);
    }
}
