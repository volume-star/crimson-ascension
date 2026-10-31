package com.viyella.crimsonascension.block;

import com.viyella.crimsonascension.cultivation.CultivationRules;
import com.viyella.crimsonascension.cultivation.CultivationState;
import com.viyella.crimsonascension.cultivation.ModAttachments;
import com.viyella.crimsonascension.menu.BloodCondensationAltarMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class BloodCondensationAltarBlock extends Block {
    public BloodCondensationAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (player instanceof ServerPlayer serverPlayer) {
            BloodCondensationAltarMenu.openFor(serverPlayer, pos);
        }
        return ItemInteractionResult.SUCCESS;
    }

    public static void exchange(ServerPlayer player, int refinedAmount, BlockPos pos) {
        CultivationState cultivation = ModAttachments.get(player);
        if (refinedAmount <= 0
                || refinedAmount > cultivation.getBloodQiCapacity() / CultivationRules.BLOOD_PER_REFINED) {
            return;
        }
        int bloodCost = refinedAmount * CultivationRules.BLOOD_PER_REFINED;
        if (cultivation.getBloodQi() < bloodCost) {
            player.displayClientMessage(Component.translatable(
                    "block.crimson_ascension.blood_condensation_altar.not_enough_blood",
                    bloodCost,
                    cultivation.getBloodQi()
            ), true);
            return;
        }

        cultivation.addBloodQi(-bloodCost);
        cultivation.addRefinedBlood(refinedAmount);
        cultivation.addCorruption(refinedAmount * CultivationRules.CORRUPTION_PER_REFINED);
        ModAttachments.sync(player);

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.CRIMSON_SPORE,
                    pos.getX() + 0.5,
                    pos.getY() + 1.05,
                    pos.getZ() + 0.5,
                    18,
                    0.35,
                    0.25,
                    0.35,
                    0.01
            );
        }
        player.level().playSound(
                null,
                pos,
                SoundEvents.RESPAWN_ANCHOR_CHARGE,
                SoundSource.BLOCKS,
                0.85F,
                0.75F
        );
        player.displayClientMessage(Component.translatable(
                "block.crimson_ascension.blood_condensation_altar.refined",
                bloodCost,
                refinedAmount,
                cultivation.getRefinedBlood(),
                cultivation.getCorruption()
        ), true);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }
        if (!player.isShiftKeyDown()) {
            BloodCondensationAltarMenu.openFor(serverPlayer, pos);
            return InteractionResult.SUCCESS;
        }

        tryBreakthrough(serverPlayer, level, pos);
        return InteractionResult.SUCCESS;
    }

    public static void tryBreakthrough(ServerPlayer player, Level level, BlockPos pos) {
        CultivationState cultivation = ModAttachments.get(player);
        int realm = cultivation.getRealm();
        if (realm >= CultivationRules.MAX_REALM) {
            player.displayClientMessage(Component.translatable(
                    "block.crimson_ascension.blood_condensation_altar.max_realm"
            ), true);
            return;
        }

        int requiredRefinedBlood = CultivationRules.breakthroughRefinedBloodCost(realm);
        if (!cultivation.tryBreakthrough()) {
            player.displayClientMessage(Component.translatable(
                    "block.crimson_ascension.blood_condensation_altar.not_ready",
                    cultivation.getRefinedBlood(),
                    requiredRefinedBlood
            ), true);
            return;
        }

        ModAttachments.sync(player);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SOUL,
                    pos.getX() + 0.5,
                    pos.getY() + 1.1,
                    pos.getZ() + 0.5,
                    28,
                    0.5,
                    0.7,
                    0.5,
                    0.04
            );
        }
        level.playSound(
                null,
                pos,
                SoundEvents.EVOKER_CAST_SPELL,
                SoundSource.BLOCKS,
                1.0F,
                0.65F
        );
        player.displayClientMessage(Component.translatable(
                "block.crimson_ascension.blood_condensation_altar.breakthrough",
                Component.translatable("crimson_ascension.realm." + cultivation.getRealm()),
                cultivation.getCorruption()
        ), false);
    }
}
