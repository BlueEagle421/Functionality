package com.blueeagle421.functionality.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.RegistryObject;

public class GlowWallTorchBlock extends WallTorchBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected final RegistryObject<SimpleParticleType> pTypeRegistry;

    public GlowWallTorchBlock(Properties pProperties, RegistryObject<SimpleParticleType> pTypeRegistry) {
        super(pProperties, ParticleTypes.FLAME);

        this.pTypeRegistry = pTypeRegistry;

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BlockStateProperties.WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockstate = this.defaultBlockState();
        LevelReader levelreader = ctx.getLevel();
        BlockPos blockpos = ctx.getClickedPos();
        Direction[] adirection = ctx.getNearestLookingDirections();

        for (Direction direction : adirection) {
            if (direction.getAxis().isHorizontal()) {
                Direction opposite = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, opposite);
                if (blockstate.canSurvive(levelreader, blockpos)) {
                    FluidState fs = ctx.getLevel().getFluidState(blockpos);
                    return blockstate.setValue(BlockStateProperties.WATERLOGGED, fs.getType() == Fluids.WATER);
                }
            }
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(BlockStateProperties.WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
            if (world instanceof Level) {
                ((Level) world).setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true), 3);
            } else {
                world.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true), 3);
            }
            world.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
            return true;
        }
        return false;
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        return !state.getValue(BlockStateProperties.WATERLOGGED) && fluid == Fluids.WATER;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        Direction direction = pState.getValue(FACING);
        double d0 = (double) pPos.getX() + 0.5D;
        double d1 = (double) pPos.getY() + 0.7D;
        double d2 = (double) pPos.getZ() + 0.5D;
        Direction direction1 = direction.getOpposite();
        pLevel.addParticle(pTypeRegistry.get(), d0 + 0.27D * (double) direction1.getStepX(), d1 + 0.22D,
                d2 + 0.27D * (double) direction1.getStepZ(), 0.0D, 0.0D, 0.0D);
    }
}
