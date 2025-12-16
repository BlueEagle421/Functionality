package com.blueeagle421.functionality.block.custom;

import com.blueeagle421.functionality.block.entity.ModBlockEntities;
import com.blueeagle421.functionality.block.entity.custom.ChunkLoaderEntity;
import com.blueeagle421.functionality.network.ChunkHighlightClient;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChunkLoaderBlock extends BaseEntityBlock {

    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 12, 16);

    public ChunkLoaderBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ChunkLoaderEntity(ModBlockEntities.CHUNK_LOADER.get(), pPos, pState);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {

        if (player.isShiftKeyDown()) {
            if (level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof ChunkLoaderEntity loader) {
                    ChunkHighlightClient.toggle(pos.immutable(), loader.radius());
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ChunkLoaderEntity loader) {
                loader.toggleMode(player, pos);
            }
        }

        return InteractionResult.SUCCESS;
    }

}
