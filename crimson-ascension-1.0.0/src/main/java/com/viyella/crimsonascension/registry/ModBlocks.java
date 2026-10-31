package com.viyella.crimsonascension.registry;

import com.viyella.crimsonascension.CrimsonAscension;
import com.viyella.crimsonascension.block.BloodCondensationAltarBlock;
import com.viyella.crimsonascension.block.BoneRuneFormationBlock;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks REGISTER =
            DeferredRegister.createBlocks(CrimsonAscension.MOD_ID);

    public static final DeferredBlock<BloodCondensationAltarBlock> BLOOD_CONDENSATION_ALTAR =
            REGISTER.register("blood_condensation_altar", () -> new BloodCondensationAltarBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.NETHER)
                            .sound(SoundType.STONE)
                            .strength(3.5F, 6.0F)
                            .requiresCorrectToolForDrops()
            ));

    public static final DeferredBlock<BoneRuneFormationBlock> BONE_RUNE_FORMATION =
            REGISTER.register("bone_rune_formation", () -> new BoneRuneFormationBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.SAND)
                            .sound(SoundType.BONE_BLOCK)
                            .strength(3.0F, 5.0F)
                            .requiresCorrectToolForDrops()
            ));

    private ModBlocks() {
    }
}
