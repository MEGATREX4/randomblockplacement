package com.megatrex4.mixin.client;

import com.megatrex4.RandomBlockPlacementClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class PlayerInteractBlockMixin {

    @Inject(method = "interactBlock", at = @At("RETURN"))
    private void onBlockPlaceSuccess(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        RandomBlockPlacementClient clientInstance = RandomBlockPlacementClient.getInstance();
        if (clientInstance != null) {
            clientInstance.handleBlockPlacement(MinecraftClient.getInstance().player);
        }
    }
}
