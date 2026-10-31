package com.viyella.crimsonascension.item;

import java.util.List;

import com.viyella.crimsonascension.cultivation.CultivationRules;
import com.viyella.crimsonascension.cultivation.CultivationState;
import com.viyella.crimsonascension.cultivation.ModAttachments;
import com.viyella.crimsonascension.registry.ModDataComponents;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class BloodVialItem extends Item {
    public BloodVialItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        CultivationState state = ModAttachments.get(serverPlayer);
        int stored = getStoredBlood(stack);
        if (player.isShiftKeyDown()) {
            return returnBloodToPlayer(serverPlayer, stack, state, stored);
        }
        return sealBloodFromPlayer(serverPlayer, stack, state, stored);
    }

    private static InteractionResultHolder<ItemStack> sealBloodFromPlayer(
            ServerPlayer player,
            ItemStack stack,
            CultivationState state,
            int stored
    ) {
        int availableSpace = CultivationRules.BLOOD_VIAL_CAPACITY - stored;
        int transferred = Math.min(availableSpace, state.getBloodQi());
        if (transferred <= 0) {
            player.displayClientMessage(Component.translatable(
                    stored >= CultivationRules.BLOOD_VIAL_CAPACITY
                            ? "item.crimson_ascension.blood_vial.full"
                            : "item.crimson_ascension.blood_vial.no_blood_qi"
            ), true);
            return InteractionResultHolder.fail(stack);
        }

        state.addBloodQi(-transferred);
        setStoredBlood(stack, stored + transferred);
        ModAttachments.sync(player);
        player.displayClientMessage(Component.translatable(
                "item.crimson_ascension.blood_vial.sealed",
                transferred,
                getStoredBlood(stack),
                CultivationRules.BLOOD_VIAL_CAPACITY
        ), true);
        return InteractionResultHolder.success(stack);
    }

    private static InteractionResultHolder<ItemStack> returnBloodToPlayer(
            ServerPlayer player,
            ItemStack stack,
            CultivationState state,
            int stored
    ) {
        if (stored <= 0) {
            player.displayClientMessage(Component.translatable(
                    "item.crimson_ascension.blood_vial.empty"
            ), true);
            return InteractionResultHolder.fail(stack);
        }

        int availableSpace = state.getBloodQiCapacity() - state.getBloodQi();
        int restored = Math.min(availableSpace, stored);
        if (restored <= 0) {
            player.displayClientMessage(Component.translatable(
                    "item.crimson_ascension.blood_vial.blood_qi_full"
            ), true);
            return InteractionResultHolder.fail(stack);
        }

        state.addBloodQi(restored);
        setStoredBlood(stack, stored - restored);
        ModAttachments.sync(player);
        player.displayClientMessage(Component.translatable(
                "item.crimson_ascension.blood_vial.returned",
                restored,
                getStoredBlood(stack)
        ), true);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        tooltipComponents.add(Component.translatable(
                "item.crimson_ascension.blood_vial.stored",
                getStoredBlood(stack),
                CultivationRules.BLOOD_VIAL_CAPACITY
        ));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getStoredBlood(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getStoredBlood(stack) / CultivationRules.BLOOD_VIAL_CAPACITY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xA80F1D;
    }

    public static int getStoredBlood(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.STORED_BLOOD.get(), 0);
    }

    public static void setStoredBlood(ItemStack stack, int amount) {
        stack.set(
                ModDataComponents.STORED_BLOOD.get(),
                CultivationRules.clamp(amount, CultivationRules.BLOOD_VIAL_CAPACITY)
        );
    }
}
