package com.viyella.crimsonascension.item;

import com.viyella.crimsonascension.entity.BloodPuppetFactory;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public final class BloodSpawnEggItem extends Item {
    private final boolean bloodFiend;

    public BloodSpawnEggItem(boolean bloodFiend, Properties properties) {
        super(properties);
        this.bloodFiend = bloodFiend;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        BlockPos spawnPos = clickedState.getCollisionShape(level, clickedPos).isEmpty()
                ? clickedPos
                : clickedPos.relative(context.getClickedFace());
        Vec3 position = Vec3.atBottomCenterOf(spawnPos);

        boolean spawned = bloodFiend
                ? BloodPuppetFactory.spawnBoss(level, position) != null
                : BloodPuppetFactory.spawn(level, position);
        if (!spawned) {
            return InteractionResult.FAIL;
        }

        Player player = context.getPlayer();
        if (player != null) {
            context.getItemInHand().consume(1, player);
            player.awardStat(Stats.ITEM_USED.get(this));
        } else {
            context.getItemInHand().shrink(1);
        }
        level.gameEvent(player, GameEvent.ENTITY_PLACE, spawnPos);
        return InteractionResult.CONSUME;
    }
}
