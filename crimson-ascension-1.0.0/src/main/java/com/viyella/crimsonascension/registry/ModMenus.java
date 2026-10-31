package com.viyella.crimsonascension.registry;

import com.viyella.crimsonascension.CrimsonAscension;
import com.viyella.crimsonascension.menu.BloodVaultMenu;
import com.viyella.crimsonascension.menu.BloodCondensationAltarMenu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER =
            DeferredRegister.create(Registries.MENU, CrimsonAscension.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<BloodVaultMenu>> BLOOD_VAULT =
            REGISTER.register("blood_vault", () -> IMenuTypeExtension.create(BloodVaultMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<BloodCondensationAltarMenu>> BLOOD_ALTAR =
            REGISTER.register("blood_altar", () -> IMenuTypeExtension.create(BloodCondensationAltarMenu::new));

    private ModMenus() {
    }
}
