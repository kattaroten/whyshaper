package net.examplemod.mixin;
import com.simibubi.create.content.curiosities.zapper.ZapperItem;
import com.simibubi.create.content.curiosities.zapper.terrainzapper.FlattenTool;
import com.simibubi.create.content.curiosities.zapper.terrainzapper.WorldshaperItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import com.simibubi.create.content.curiosities.zapper.terrainzapper.TerrainTools;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

import static com.simibubi.create.content.curiosities.zapper.terrainzapper.TerrainTools.isReplaceable;
import static net.examplemod.TerrainUtils.placeOrReplace;

@Mixin(WorldshaperItem.class)
public abstract class MixinWorldShaperItem {

    @Redirect(method = "activate",
            at = @At(value = "INVOKE",
                    target = "Lcom/simibubi/create/content/curiosities/zapper/terrainzapper/TerrainTools;run(Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/world/entity/player/Player;)V"))
    private void injectedRun(@NotNull TerrainTools instance, Level world, List<BlockPos> targetPositions, Direction facing, BlockState paintedState, CompoundTag data, Player player) {
        switch (instance) {
            case Clear:
                targetPositions.forEach(p -> world.destroyBlock(p, true));
                break;
            case Fill:
                targetPositions.forEach(p -> {
                    BlockState toReplace = world.getBlockState(p);
                    if (!isReplaceable(toReplace))
                        return;
                    placeOrReplace(world, p, paintedState, player);
                    ZapperItem.setTileData(world, p, paintedState, data, player);
                });
                break;
            case Flatten:
                FlattenTool.apply(world, targetPositions, facing);
                break;
            case Overlay:
                targetPositions.forEach(p -> {
                    BlockState toOverlay = world.getBlockState(p);
                    if (isReplaceable(toOverlay))
                        return;
                    if (toOverlay == paintedState)
                        return;
                    p = p.above();
                    BlockState toReplace = world.getBlockState(p);
                    if (!isReplaceable(toReplace))
                        return;
                    placeOrReplace(world, p, paintedState, player);
                    ZapperItem.setTileData(world, p, paintedState, data, player);
                });
                break;
            case Place:
                targetPositions.forEach(p -> {
                    placeOrReplace(world, p, paintedState, player);
                    ZapperItem.setTileData(world, p, paintedState, data, player);
                });
                break;
            case Replace:
                targetPositions.forEach(p -> {
                    BlockState toReplace = world.getBlockState(p);
                    if (isReplaceable(toReplace))
                        return;
                    placeOrReplace(world, p, paintedState, player);
                    ZapperItem.setTileData(world, p, paintedState, data, player);
                });
                break;
        }
    }
}
