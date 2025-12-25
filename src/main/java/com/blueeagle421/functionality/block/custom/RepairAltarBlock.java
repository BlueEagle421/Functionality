package com.blueeagle421.functionality.block.custom;

import javax.annotation.Nullable;

import com.blueeagle421.functionality.block.entity.ModBlockEntities;
import com.blueeagle421.functionality.block.entity.custom.RepairAltarEntity;
import com.blueeagle421.functionality.menu.RepairAltarMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {

        if (!level.isClientSide) {
            NetworkHooks.openScreen(
                    (ServerPlayer) player,
                    state.getMenuProvider(level, pos),
                    pos);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return new SimpleMenuProvider((containerId, access, player) -> {
            return new RepairAltarMenu(containerId, access, ContainerLevelAccess.create(pLevel, pPos));
        }, this.getName());
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

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        double d0 = (double) pPos.getX() + 0.4D + (double) pRandom.nextFloat() * 0.2D;
        double d1 = (double) pPos.getY() + 0.7D + (double) pRandom.nextFloat() * 0.3D;
        double d2 = (double) pPos.getZ() + 0.4D + (double) pRandom.nextFloat() * 0.2D;
        pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }
}
