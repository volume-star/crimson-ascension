package com.viyella.crimsonascension.block;

import com.viyella.crimsonascension.CrimsonConfig;
import com.viyella.crimsonascension.cultivation.CultivationRules;
import com.viyella.crimsonascension.item.BloodVialItem;
import com.viyella.crimsonascension.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class BoneRuneFormationBlock extends Block {
    public BoneRuneFormationBlock(Properties properties) {
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
        if (!stack.is(ModItems.BLOOD_VIAL.get())) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.displayClientMessage(Component.translatable(
                        "block.crimson_ascension.bone_rune_formation.hint"
                ), true);
            }
            return ItemInteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            activate(serverPlayer, stack, level, pos);
        }
        return ItemInteractionResult.SUCCESS;
    }

    private static void activate(ServerPlayer player, ItemStack vial, Level level, BlockPos pos) {
        int stored = BloodVialItem.getStoredBlood(vial);
        int bloodCost = CrimsonConfig.BONE_RUNE_BLOOD_COST.get();
        if (stored < bloodCost) {
            player.displayClientMessage(Component.translatable(
                    "block.crimson_ascension.bone_rune_formation.not_enough",
                    bloodCost,
                    stored
            ), true);
            return;
        }
        BloodVialItem.setStoredBlood(vial, stored - bloodCost);
        if (level instanceof ServerLevel serverLevel) {
            for (ServerPlayer nearby : serverLevel.getPlayers(target -> target.distanceToSqr(player) <= 64.0)) {
                nearby.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0));
                nearby.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 1));
            }
            serverLevel.sendParticles(
                    ParticleTypes.SOUL,
                    pos.getX() + 0.5,
                    pos.getY() + 0.8,
                    pos.getZ() + 0.5,
                    32,
                    3.0,
                    0.8,
                    3.0,
                    0.02
            );
        }
        level.playSound(null, pos, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.BLOCKS, 0.8F, 0.7F);
        player.displayClientMessage(Component.translatable(
                "block.crimson_ascension.bone_rune_formation.activated",
                bloodCost
        ), true);
    }
}
