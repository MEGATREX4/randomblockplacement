package com.megatrex4;

import com.megatrex4.config.GradientType;
import com.megatrex4.config.GradientAxis;
import com.megatrex4.config.RandomBlockPlacementConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class GradientPlacement {
    private static BlockPos anchor;
    private static Vec3 linearDirection;
    private static UUID anchorPlayer;
    private static ResourceKey<?> anchorDimension;

    private GradientPlacement() {
    }

    public static PlacementAttempt prepare(
            LocalPlayer player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        var config = RandomBlockPlacementConfig.CLIENT;
        if (!RandomBlockPlacementClient.isGradientPlacementMode()
                || hand != InteractionHand.MAIN_HAND) {
            return null;
        }

        if (anchor != null && (!player.getUUID().equals(anchorPlayer)
                || !player.level().dimension().equals(anchorDimension))) {
            reset();
        }

        List<Integer> paletteSlots = getPaletteSlots(player);
        if (paletteSlots.isEmpty()) {
            return null;
        }

        BlockPos target = new BlockPlaceContext(
                player,
                hand,
                player.getInventory().getItem(paletteSlots.getFirst()),
                hitResult
        ).getClickedPos();

        boolean isAnchor = anchor == null;
        double progress = isAnchor ? 0.0 : getProgress(
                target,
                config.gradientType.get(),
                config.gradientAxis.get(),
                config.gradientLength.get()
        );
        int paletteIndex = getPaletteIndex(
                progress,
                paletteSlots.size(),
                config.gradientDithering.get(),
                isAnchor ? target : target.subtract(anchor)
        );
        int selectedSlot = paletteSlots.get(paletteIndex);
        int originalSlot = player.getInventory().getSelectedSlot();
        Block selectedBlock = ((BlockItem) player.getInventory().getItem(selectedSlot).getItem()).getBlock();
        player.getInventory().setSelectedSlot(selectedSlot);

        return new PlacementAttempt(
                originalSlot,
                target,
                isAnchor,
                player.getLookAngle(),
                selectedBlock
        );
    }

    public static void complete(LocalPlayer player, PlacementAttempt attempt, boolean interactionSucceeded) {
        boolean placed = interactionSucceeded
                && player.level().getBlockState(attempt.target()).is(attempt.block());
        if (placed) {
            if (attempt.isAnchor()) {
                anchor = attempt.target();
                linearDirection = attempt.lookDirection();
                anchorPlayer = player.getUUID();
                anchorDimension = player.level().dimension();
            }
        } else {
            player.getInventory().setSelectedSlot(attempt.originalSlot());
        }
    }

    public static void reset() {
        anchor = null;
        linearDirection = null;
        anchorPlayer = null;
        anchorDimension = null;
    }

    private static List<Integer> getPaletteSlots(LocalPlayer player) {
        List<Integer> slots = new ArrayList<>();
        for (int slot = 0; slot < 9; slot++) {
            var stack = player.getInventory().getItem(slot);
            if (stack.getItem() instanceof BlockItem && stack.getCount() > 0) {
                slots.add(slot);
            }
        }
        return slots;
    }

    private static double getProgress(
            BlockPos target,
            GradientType type,
            GradientAxis axis,
            int length
    ) {
        double dx = target.getX() - anchor.getX();
        double dy = target.getY() - anchor.getY();
        double dz = target.getZ() - anchor.getZ();
        double distance;
        if (axis == GradientAxis.VERTICAL) {
            distance = Math.abs(dy);
        } else if (type == GradientType.LINEAR) {
            double directionX = linearDirection.x;
            double directionZ = linearDirection.z;
            double directionLength = Math.sqrt(
                    directionX * directionX + directionZ * directionZ
            );
            if (directionLength < 1.0e-6) {
                directionX = 0.0;
                directionZ = 1.0;
                directionLength = 1.0;
            }
            distance = Math.max(0.0,
                    (dx * directionX + dz * directionZ) / directionLength);
        } else {
            distance = type == GradientType.RADIAL
                    ? Math.sqrt(dx * dx + dz * dz)
                    : Math.max(Math.abs(dx), Math.abs(dz));
        }
        return Math.min(1.0, distance / (length - 1.0));
    }

    private static int getPaletteIndex(
            double progress,
            int paletteSize,
            boolean dithering,
            BlockPos relativePosition
    ) {
        if (paletteSize <= 1) {
            return 0;
        }

        double palettePosition = progress * (paletteSize - 1);
        int lower = (int) Math.floor(palettePosition);
        int upper = Math.min(lower + 1, paletteSize - 1);
        if (upper == lower) {
            return lower;
        }

        double fraction = palettePosition - lower;
        if (dithering) {
            return orderedDitherThreshold(relativePosition) < fraction ? upper : lower;
        }
        return fraction < 0.5 ? lower : upper;
    }

    private static double orderedDitherThreshold(BlockPos position) {
        int rank = 0;
        for (int bit = 1; bit >= 0; bit--) {
            rank = (rank << 3)
                    | (((position.getX() >>> bit) & 1) << 2)
                    | (((position.getY() >>> bit) & 1) << 1)
                    | ((position.getZ() >>> bit) & 1);
        }
        return (rank + 0.5) / 64.0;
    }

    public record PlacementAttempt(
            int originalSlot,
            BlockPos target,
            boolean isAnchor,
            Vec3 lookDirection,
            Block block
    ) {
    }
}
