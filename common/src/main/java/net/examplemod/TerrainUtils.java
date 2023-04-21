package net.examplemod;

import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class TerrainUtils {
    public static void placeOrReplace(Level world, BlockPos pos, @Nullable BlockState paintedState, Player player) {
        BlockState toReplace = world.getBlockState(pos);
        if (!player.isCreative()) {
            if (0 == BlockHelper.findAndRemoveInInventory(paintedState, player, 1)) {
                return;
            }
            if (!toReplace.isAir()) {
                BlockEntity blockEntity = toReplace.hasBlockEntity() ? world.getBlockEntity(pos) : null;
                Block.dropResources(toReplace, world, pos, blockEntity);
            }
        }
        world.setBlockAndUpdate(pos, paintedState);
    }
}