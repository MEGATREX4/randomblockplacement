package com.megatrex4.mixin.client;



import com.megatrex4.PlayerInventoryAccessor;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements PlayerInventoryAccessor {
    @Shadow
    public int selectedSlot;

    @Override
    public void setSelectedSlot(int slot) {
        this.selectedSlot = slot;
    }

    @Override
    public int getSelectedSlot() {
        return this.selectedSlot;
    }
}
