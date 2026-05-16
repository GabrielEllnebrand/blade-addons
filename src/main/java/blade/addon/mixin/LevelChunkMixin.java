package blade.addon.mixin;

import blade.addon.utils.events.Events;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {

    @Inject(method = "setBlockEntity", at=@At("HEAD"))
    public void onBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
        Events.ON_BLOCK_ENTITY.invoke(blockEntityEvent -> blockEntityEvent.on(blockEntity));

    }

}
