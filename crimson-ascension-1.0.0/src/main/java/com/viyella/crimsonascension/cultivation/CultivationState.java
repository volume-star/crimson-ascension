package com.viyella.crimsonascension.cultivation;

import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class CultivationState {
    public static final Codec<CultivationState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("blood_qi", 0).forGetter(CultivationState::getBloodQi),
            Codec.INT.optionalFieldOf("refined_blood", 0).forGetter(CultivationState::getRefinedBlood),
            Codec.INT.optionalFieldOf("realm", 0).forGetter(CultivationState::getRealm),
            Codec.INT.optionalFieldOf("corruption", 0).forGetter(CultivationState::getCorruption),
            Codec.STRING.optionalFieldOf("path", CultivationPath.NONE.id()).forGetter(state -> state.getPath().id())
    ).apply(instance, CultivationState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CultivationState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            CultivationState::getBloodQi,
            ByteBufCodecs.VAR_INT,
            CultivationState::getRefinedBlood,
            ByteBufCodecs.VAR_INT,
            CultivationState::getRealm,
            ByteBufCodecs.VAR_INT,
            CultivationState::getCorruption,
            ByteBufCodecs.STRING_UTF8,
            state -> state.getPath().id(),
            CultivationState::new
    );

    private int bloodQi;
    private int refinedBlood;
    private int realm;
    private int corruption;
    private String path;

    public CultivationState() {
        this(0, 0, 0, 0, CultivationPath.NONE.id());
    }

    public CultivationState(int bloodQi, int refinedBlood, int realm, int corruption) {
        this(bloodQi, refinedBlood, realm, corruption, CultivationPath.NONE.id());
    }

    public CultivationState(int bloodQi, int refinedBlood, int realm, int corruption, String path) {
        this.realm = CultivationRules.clamp(realm, CultivationRules.MAX_REALM);
        this.bloodQi = CultivationRules.clamp(bloodQi, CultivationRules.maxBloodQiForRealm(this.realm));
        this.refinedBlood = CultivationRules.clamp(refinedBlood, CultivationRules.MAX_REFINED_BLOOD);
        this.corruption = CultivationRules.clamp(corruption, CultivationRules.MAX_CORRUPTION);
        this.path = CultivationPath.fromId(path).id();
    }

    public int getBloodQi() {
        return bloodQi;
    }

    public int getBloodQiCapacity() {
        return CultivationRules.maxBloodQiForRealm(realm);
    }

    public int getRefinedBlood() {
        return refinedBlood;
    }

    public int getRealm() {
        return realm;
    }

    public int getCorruption() {
        return corruption;
    }

    public CultivationPath getPath() {
        return CultivationPath.fromId(path);
    }

    public void setBloodQi(int bloodQi) {
        this.bloodQi = CultivationRules.clamp(bloodQi, getBloodQiCapacity());
    }

    public void setRefinedBlood(int refinedBlood) {
        this.refinedBlood = CultivationRules.clamp(refinedBlood, CultivationRules.MAX_REFINED_BLOOD);
    }

    public void setRealm(int realm) {
        this.realm = CultivationRules.clamp(realm, CultivationRules.MAX_REALM);
        bloodQi = Math.min(bloodQi, getBloodQiCapacity());
    }

    public void setCorruption(int corruption) {
        this.corruption = CultivationRules.clamp(corruption, CultivationRules.MAX_CORRUPTION);
    }

    public void setPath(CultivationPath path) {
        this.path = (path == null ? CultivationPath.NONE : path).id();
    }

    public void addBloodQi(int amount) {
        bloodQi = CultivationRules.addClamped(bloodQi, amount, getBloodQiCapacity());
    }

    public void addRefinedBlood(int amount) {
        refinedBlood = CultivationRules.addClamped(refinedBlood, amount, CultivationRules.MAX_REFINED_BLOOD);
    }

    public void addRealm(int amount) {
        realm = CultivationRules.addClamped(realm, amount, CultivationRules.MAX_REALM);
        bloodQi = Math.min(bloodQi, getBloodQiCapacity());
    }

    public void addCorruption(int amount) {
        corruption = CultivationRules.addClamped(corruption, amount, CultivationRules.MAX_CORRUPTION);
    }

    public boolean tryBreakthrough() {
        if (realm >= CultivationRules.MAX_REALM
                || refinedBlood < CultivationRules.breakthroughRefinedBloodCost(realm)) {
            return false;
        }
        int previousRealm = realm;
        addRefinedBlood(-CultivationRules.breakthroughRefinedBloodCost(realm));
        addRealm(1);
        addCorruption(25 * (previousRealm + 1));
        return true;
    }

    public void reset() {
        bloodQi = 0;
        refinedBlood = 0;
        realm = 0;
        corruption = 0;
        path = CultivationPath.NONE.id();
    }

    public CultivationState copy() {
        return new CultivationState(bloodQi, refinedBlood, realm, corruption, path);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CultivationState state)) {
            return false;
        }
        return bloodQi == state.bloodQi
                && refinedBlood == state.refinedBlood
                && realm == state.realm
                && corruption == state.corruption
                && path.equals(state.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bloodQi, refinedBlood, realm, corruption, path);
    }

    @Override
    public String toString() {
        return "CultivationState[" +
                "bloodQi=" + bloodQi +
                ", refinedBlood=" + refinedBlood +
                ", realm=" + realm +
                ", corruption=" + corruption +
                ", path=" + path +
                ']';
    }
}
