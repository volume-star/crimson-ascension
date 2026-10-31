package com.viyella.crimsonascension.client;

public final class BloodMoonClientState {
    private static boolean active;

    private BloodMoonClientState() {
    }

    public static boolean isActive() {
        return active;
    }

    public static void setActive(boolean active) {
        BloodMoonClientState.active = active;
    }
}
