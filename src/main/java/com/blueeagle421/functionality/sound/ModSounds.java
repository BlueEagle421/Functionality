package com.blueeagle421.functionality.sound;

import com.blueeagle421.functionality.FunctionalityMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("removal")
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister
            .create(ForgeRegistries.SOUND_EVENTS, FunctionalityMod.MOD_ID);

    public static final RegistryObject<SoundEvent> DISC_THROW = registerSoundEvents("disc_throw");
    public static final RegistryObject<SoundEvent> DISC_HIT = registerSoundEvents("disc_hit");
    public static final RegistryObject<SoundEvent> REPAIR_ALTAR_USE = registerSoundEvents("repair_altar_use");

    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name,
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FunctionalityMod.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}