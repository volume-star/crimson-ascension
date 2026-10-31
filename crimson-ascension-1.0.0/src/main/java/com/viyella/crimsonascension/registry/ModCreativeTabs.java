package com.viyella.crimsonascension.registry;

import com.viyella.crimsonascension.CrimsonAscension;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CrimsonAscension.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CRIMSON_ASCENSION =
            REGISTER.register("crimson_ascension", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.crimson_ascension"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> new ItemStack(ModItems.BLOODLETTING_DAGGER.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.BLOODLETTING_DAGGER.get());
                        output.accept(ModItems.BLOOD_VIAL.get());
                        output.accept(ModItems.BLOOD_PUPPET_SPAWN_EGG.get());
                        output.accept(ModItems.BLOOD_FIEND_SPAWN_EGG.get());
                        output.accept(ModItems.BLOOD_CONDENSATION_ALTAR.get());
                        output.accept(ModItems.BONE_RUNE_FORMATION.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }
}
