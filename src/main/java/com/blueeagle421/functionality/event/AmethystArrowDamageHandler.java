package com.blueeagle421.functionality.event;

import com.blueeagle421.functionality.FunctionalityMod;
import com.blueeagle421.functionality.config.FunctionalityConfig;
import com.blueeagle421.functionality.config.subcategories.items.AmethystArrow;
import com.blueeagle421.functionality.entity.custom.AmethystArrowEntity;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FunctionalityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AmethystArrowDamageHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {

        if (event.getEntity().level().isClientSide)
            return;

        if (event.getSource() == null)
            return;

        if (event.getSource().getDirectEntity() instanceof AmethystArrowEntity) {
            float original = event.getAmount();
            float modified = original * config().damageMultiplier.get().floatValue();

            event.setAmount(modified);
        }
    }

    private static AmethystArrow config() {
        return FunctionalityConfig.COMMON.items.amethystArrow;
    }
}