package com.blueeagle421.functionality.mixin;

import com.blueeagle421.functionality.entity.custom.ObsidianBoatEntity;
import com.blueeagle421.functionality.item.custom.InfernoGearItem;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    private static final List<Boat.Status> DAMAGE_STATUSES = Arrays.asList(
            Boat.Status.UNDER_WATER,
            Boat.Status.UNDER_FLOWING_WATER);

    @Inject(method = "lavaHurt()V", at = @At("HEAD"), cancellable = true)
    private void onLavaHurt(CallbackInfo ci) {

        if (isImmuneInBoat(this.getSelf()) || hasInfernoGear(this.getSelf()))
            ci.cancel();
    }

    @Inject(method = "isOnFire()Z", at = @At("HEAD"), cancellable = true)
    private void onIsOnFire(CallbackInfoReturnable<Boolean> cir) {

        if (isImmuneInBoat(this.getSelf()) || hasInfernoGear(this.getSelf()))
            cir.setReturnValue(false);
    }

    private Entity getSelf() {
        return (Entity) (Object) this;
    }

    private static boolean hasInfernoGear(Entity entity) {
        if (!(entity instanceof Player player))
            return false;

        var chestItem = player.getItemBySlot(EquipmentSlot.CHEST).getItem();

        return chestItem instanceof InfernoGearItem;
    }

    private static boolean isImmuneInBoat(Entity entity) {
        if (!entity.isPassenger())
            return false;

        Entity vehicle = entity.getVehicle();

        if (!(vehicle instanceof ObsidianBoatEntity boat))
            return false;

        try {
            java.lang.reflect.Method getStatus = ObfuscationReflectionHelper.findMethod(Boat.class, "m_38392_");
            getStatus.setAccessible(true);
            Boat.Status status = (Boat.Status) getStatus.invoke(boat);
            return !DAMAGE_STATUSES.contains(status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
