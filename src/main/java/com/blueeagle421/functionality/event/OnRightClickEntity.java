package com.blueeagle421.functionality.event;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.blueeagle421.functionality.FunctionalityMod;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID)
public class OnRightClickEntity {

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {

        if (event.getLevel().isClientSide())
            return;

        if (!(event.getTarget() instanceof GlowSquid glowSquid))
            return;

        Player player = event.getEntity();
        ItemStack hand = player.getItemInHand(event.getHand());

        if (hand.getItem() != Items.GLASS_BOTTLE)
            return;

        DamageType type = player.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .get(DamageTypes.MAGIC);

        DamageSource src = new DamageSource(Holder.direct(type), player, player);

        glowSquid.hurt(src, 4.0F);

        if (!player.getAbilities().instabuild)
            hand.shrink(1);

        ItemStack nv = new ItemStack(Items.POTION);

        nv.getOrCreateTag().putString("Potion", "minecraft:night_vision");

        if (!player.addItem(nv))
            player.level().addFreshEntity(new ItemEntity(player.level(),
                    player.getX(), player.getY(), player.getZ(), nv));

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
