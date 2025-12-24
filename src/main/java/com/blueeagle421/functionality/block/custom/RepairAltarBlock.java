package com.blueeagle421.functionality.block.custom;

import com.blueeagle421.functionality.block.entity.ModBlockEntities;
import com.blueeagle421.functionality.block.entity.custom.RepairAltarEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

public class RepairAltarBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape BASE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
    private static final VoxelShape NARROW = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 5.0D, 12.0D);
    private static final VoxelShape WIDE_X = Block.box(6.0D, 5.0D, 5.0D, 10.0D, 10.0D, 11.0D);
    private static final VoxelShape WIDE_Z = Block.box(5.0D, 5.0D, 6.0D, 11.0D, 10.0D, 10.0D);
    private static final VoxelShape TOP = Block.box(3.0D, 8.0D, 3.0D, 13.0D, 14.0D, 13.0D);

    private static final VoxelShape X_AXIS_SHAPE = Shapes.or(BASE, NARROW, WIDE_X, TOP);
    private static final VoxelShape Z_AXIS_SHAPE = Shapes.or(BASE, NARROW, WIDE_Z, TOP);

    public RepairAltarBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RepairAltarEntity(ModBlockEntities.REPAIR_ALTAR.get(), pPos, pState);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction dir = pState.getValue(FACING);
        return dir.getAxis() == Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }
}
