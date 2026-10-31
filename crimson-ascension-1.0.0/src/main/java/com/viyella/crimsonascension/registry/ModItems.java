package com.viyella.crimsonascension.registry;

import com.viyella.crimsonascension.CrimsonAscension;
import com.viyella.crimsonascension.item.BloodSpawnEggItem;
import com.viyella.crimsonascension.item.BloodVialItem;
import com.viyella.crimsonascension.item.BloodlettingDaggerItem;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items REGISTER =
            DeferredRegister.createItems(CrimsonAscension.MOD_ID);

    public static final DeferredItem<SwordItem> BLOODLETTING_DAGGER = REGISTER.register(
            "bloodletting_dagger",
            () -> new BloodlettingDaggerItem(new Item.Properties()
                    .durability(260)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 4.0F, -2.2F)))
    );

    public static final DeferredItem<BloodVialItem> BLOOD_VIAL = REGISTER.register(
            "blood_vial",
            () -> new BloodVialItem(new Item.Properties().stacksTo(1))
    );

    public static final DeferredItem<BloodSpawnEggItem> BLOOD_PUPPET_SPAWN_EGG = REGISTER.register(
            "blood_puppet_spawn_egg",
            () -> new BloodSpawnEggItem(false, new Item.Properties().stacksTo(64))
    );

    public static final DeferredItem<BloodSpawnEggItem> BLOOD_FIEND_SPAWN_EGG = REGISTER.register(
            "blood_fiend_spawn_egg",
            () -> new BloodSpawnEggItem(true, new Item.Properties().stacksTo(64))
    );

    public static final DeferredItem<BlockItem> BLOOD_CONDENSATION_ALTAR = REGISTER.registerSimpleBlockItem(
            "blood_condensation_altar",
            ModBlocks.BLOOD_CONDENSATION_ALTAR
    );

    public static final DeferredItem<BlockItem> BONE_RUNE_FORMATION = REGISTER.registerSimpleBlockItem(
            "bone_rune_formation",
            ModBlocks.BONE_RUNE_FORMATION
    );

    private ModItems() {
    }
}
