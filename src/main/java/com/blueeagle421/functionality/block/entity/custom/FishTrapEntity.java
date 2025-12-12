package com.blueeagle421.functionality.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import com.blueeagle421.functionality.block.custom.FishTrapBlock;
import com.blueeagle421.functionality.block.entity.ModBlockEntities;

public class FishTrapEntity extends BlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);

    private int ticksInWater = 0;
    private int targetTicks = 0;
    private boolean triggered = false;

    private static final int MAX_UNITS_PER_TYPE = 2;
    private static final float LUCK_PER_UNIT = 0.5f;
    private static final int LUCK_SCAN_RADIUS = 5;
    private static final int WATER_DEPTH_MAX = 256;
    private static final int BASE_TICKS = 24000;

    public FishTrapEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public FishTrapEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FISH_TRAP.get(), pos, state);
    }

    public void drops() {
        if (this.level == null || this.level.isClientSide)
            return;
        for (ItemStack stack : items) {
            if (stack == null || stack.isEmpty())
                continue;
            Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(),
                    this.worldPosition.getZ(), stack);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FishTrapEntity be) {
        if (level.isClientSide)
            return;
        int stage = state.getValue(FishTrapBlock.STAGE);
        if (stage >= 1)
            return;
        if (be.targetTicks == 0) {
            RandomSource rand = level.getRandom();
            be.targetTicks = be.calculateInitialTargetTicks(level, pos, rand);
        }
        if (level.getFluidState(pos).getType() == Fluids.WATER) {
            be.ticksInWater++;
            if (be.ticksInWater >= be.targetTicks) {
                be.onFinish(level, pos, state);
            } else {
                if (be.ticksInWater % 1000 == 0)
                    be.setChanged();
            }
        }
    }

    private int calculateInitialTargetTicks(Level level, BlockPos pos, RandomSource rand) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        double speedFactor = 1.0 - 0.5 * (blockLight / 15.0);
        int base = (int) (BASE_TICKS * speedFactor);
        int jitter = rand.nextInt(1000) - 500;
        return Math.max(20, base + jitter);
    }

    private void onFinish(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide)
            return;

        RandomSource random = level.getRandom();

        int waterDepth = countWaterDepthAbove(level, pos, WATER_DEPTH_MAX);
        int maxRoll = determineMaxCatchAmount(waterDepth);
        int count = 1 + random.nextInt(maxRoll);
        float luck = computeLuck(level, pos);

        for (int i = 0; i < count; i++) {
            if (level instanceof ServerLevel serverLevel) {
                ItemStack fishingRod = new ItemStack(Items.FISHING_ROD);
                ItemStack loot = rollFishingLoot(serverLevel, Vec3.atCenterOf(pos), fishingRod, luck);
                addToInventory(loot);
            }
        }

        BlockState newState = state.setValue(FishTrapBlock.STAGE, 1);
        level.setBlock(pos, newState, 3);
        level.updateNeighborsAt(pos, newState.getBlock());
        level.updateNeighborsAt(pos.below(), newState.getBlock());

        triggered = true;
        setChanged();
    }

    public ItemStack rollFishingLoot(ServerLevel level, Vec3 origin, ItemStack tool, float luck) {
        if (level == null)
            return ItemStack.EMPTY;

        RandomSource rnd = level.getRandom();

        double treasureChance = 0.01 + (0.01 * luck);
        if (rnd.nextDouble() < treasureChance) {
            LootParams.Builder treasureBuilder = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, origin)
                    .withParameter(LootContextParams.TOOL, tool == null ? ItemStack.EMPTY : tool)
                    .withLuck(luck);
            LootParams treasureParams = treasureBuilder.create(LootContextParamSets.FISHING);
            LootTable treasureTable = level.getServer().getLootData().getLootTable(BuiltInLootTables.FISHING_TREASURE);
            List<ItemStack> tgen = treasureTable.getRandomItems(treasureParams);
            if (tgen != null && !tgen.isEmpty()) {
                return tgen.get(rnd.nextInt(tgen.size()));
            }
        }

        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, origin)
                .withParameter(LootContextParams.TOOL, tool == null ? ItemStack.EMPTY : tool)
                .withLuck(luck);

        LootParams lootParams = builder.create(LootContextParamSets.FISHING);
        LootTable lootTable = level.getServer().getLootData().getLootTable(BuiltInLootTables.FISHING);
        List<ItemStack> generated = lootTable.getRandomItems(lootParams);
        if (generated == null || generated.isEmpty())
            return ItemStack.EMPTY;
        return generated.get(rnd.nextInt(generated.size()));
    }

    private void addToInventory(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return;
        for (int i = 0; i < items.size(); i++) {
            ItemStack slot = items.get(i);
            if (!slot.isEmpty() && ItemStack.isSameItem(slot, stack) && slot.getCount() < slot.getMaxStackSize()) {
                int transferable = Math.min(stack.getCount(), slot.getMaxStackSize() - slot.getCount());
                slot.grow(transferable);
                stack.shrink(transferable);
                items.set(i, slot);
                if (stack.isEmpty()) {
                    setChanged();
                    return;
                }
            }
        }
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, stack.copy());
                setChanged();
                return;
            }
        }
    }

    private static int countWaterDepthAbove(Level level, BlockPos center, int maxDepth) {
        int depth = 0;
        for (int d = 1; d <= maxDepth; d++) {
            BlockPos p = center.above(d);
            if (level.getFluidState(p).getType() == Fluids.WATER)
                depth++;
            else
                break;
        }
        return depth;
    }

    private static int determineMaxCatchAmount(int waterDepth) {
        if (waterDepth <= 0)
            return 1;
        if (waterDepth < 3)
            return 2;
        return 3;
    }

    private static float computeLuck(Level level, BlockPos center) {
        int radius = LUCK_SCAN_RADIUS;
        int amethystCount = 0;
        int goldCount = 0;
        int headCount = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0)
                        continue;
                    BlockPos p = center.offset(dx, dy, dz);
                    BlockState bs = level.getBlockState(p);
                    if (bs.is(Blocks.AMETHYST_CLUSTER) || bs.is(Blocks.LARGE_AMETHYST_BUD)
                            || bs.is(Blocks.MEDIUM_AMETHYST_BUD) || bs.is(Blocks.SMALL_AMETHYST_BUD)
                            || bs.is(Blocks.AMETHYST_BLOCK)) {
                        if (amethystCount < MAX_UNITS_PER_TYPE)
                            amethystCount++;
                        continue;
                    }
                    if (bs.is(Blocks.GOLD_BLOCK)) {
                        if (goldCount < MAX_UNITS_PER_TYPE)
                            goldCount++;
                        continue;
                    }
                    if (bs.getBlock() instanceof SkullBlock || bs.is(Blocks.CREEPER_HEAD)
                            || bs.is(Blocks.SKELETON_SKULL) || bs.is(Blocks.WITHER_SKELETON_SKULL)
                            || bs.is(Blocks.ZOMBIE_HEAD) || bs.is(Blocks.PLAYER_HEAD)) {
                        if (headCount < MAX_UNITS_PER_TYPE)
                            headCount++;
                    }
                }
            }
        }
        int totalUnits = amethystCount + goldCount + headCount;
        return totalUnits * LUCK_PER_UNIT;
    }

    public void sendStatsToPlayer(Player player) {
        if (this.level == null || this.level.isClientSide || player == null)
            return;

        int waterDepth = countWaterDepthAbove(this.level, this.worldPosition, WATER_DEPTH_MAX);
        float luck = computeLuck(this.level, this.worldPosition);
        int maxCatchAmount = determineMaxCatchAmount(waterDepth);
        int blockLight = this.level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, this.worldPosition);
        double speedFactor = 1.0 - 0.5 * (blockLight / 15.0);
        double speedMultiplier = 1.0 / Math.max(0.0001, speedFactor);
        int speedPercent = (int) Math.round(speedMultiplier * 100.0);

        Component luckComp = Component.literal(String.format("%.2f", luck))
                .withStyle(net.minecraft.ChatFormatting.GOLD);
        Component maxComp = Component.literal(String.valueOf(maxCatchAmount))
                .withStyle(net.minecraft.ChatFormatting.AQUA);
        Component speedComp = Component.literal(speedPercent + "%").withStyle(net.minecraft.ChatFormatting.GREEN);

        Component message = Component.translatable(
                "message.functionality.fish_trap.stats",
                luckComp, maxComp, speedComp);

        player.sendSystemMessage(message);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items);
        this.ticksInWater = tag.getInt("TicksInWater");
        this.targetTicks = tag.getInt("TargetTicks");
        this.triggered = tag.getBoolean("Triggered");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("TicksInWater", ticksInWater);
        tag.putInt("TargetTicks", targetTicks);
        tag.putBoolean("Triggered", triggered);
    }
}
