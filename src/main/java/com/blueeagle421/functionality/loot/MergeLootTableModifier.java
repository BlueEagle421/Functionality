package com.blueeagle421.functionality.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class MergeLootTableModifier extends LootModifier {
    public static final Supplier<Codec<MergeLootTableModifier>> CODEC = Suppliers
            .memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst)
                    .and(ResourceLocation.CODEC.fieldOf("lootTable").forGetter(m -> m.lootTable))
                    .apply(inst, MergeLootTableModifier::new)));

    private final ResourceLocation lootTable;

    protected MergeLootTableModifier(net.minecraft.world.level.storage.loot.predicates.LootItemCondition[] conditions,
            ResourceLocation lootTable) {
        super(conditions);
        this.lootTable = lootTable;
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        LootPool pool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootTableReference.lootTableReference(this.lootTable))
                .name("functionality:merged_" + this.lootTable.getPath().replace('/', '_'))
                .build();

        pool.addRandomItems(generatedLoot::add, context);

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
