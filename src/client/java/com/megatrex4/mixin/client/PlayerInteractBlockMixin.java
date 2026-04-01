package com.megatrex4.mixin.client;

import com.megatrex4.RandomBlockPlacementClient;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("all")
@Mixin(MultiPlayerGameMode.class)
public class PlayerInteractBlockMixin {
    @Inject(
            method = "useItemOn",
            at = @At("RETURN"),
            cancellable = false
    )
    private void onBlockPlaced(
            LocalPlayer player,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (cir.getReturnValue() instanceof InteractionResult.Success) {
            RandomBlockPlacementClient.getInstance().handleBlockPlacement(player);
        }
    }
}