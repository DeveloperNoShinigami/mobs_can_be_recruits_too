package com.talhanation.recruits.entities;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

/**
 * Delegate wrapper for companion-style behaviour on vanilla mobs. Stores
 * companion specific data in persistent NBT so they can participate in
 * recruit systems without extending {@link AbstractRecruitEntity}.
 */
public class CompanionMobRecruit extends MobRecruit implements ICompanion {

    private static final String KEY_OWNER_NAME = "OwnerName";
    private static final String KEY_AT_MISSION = "AtMission";

    public CompanionMobRecruit(Mob mob) {
        super(mob);
    }

    @Override
    public AbstractRecruitEntity get() {
        throw new UnsupportedOperationException("Vanilla mob has no recruit entity");
    }

    @Override
    public void openSpecialGUI(Player player) {
        // Vanilla mobs do not expose a companion GUI.
    }

    @Override
    public String getOwnerName() {
        return getString(KEY_OWNER_NAME);
    }

    @Override
    public void setOwnerName(String name) {
        setString(KEY_OWNER_NAME, name);
    }

    @Override
    public boolean isAtMission() {
        return getBoolean(KEY_AT_MISSION);
    }

    public void setAtMission(boolean atMission) {
        setBoolean(KEY_AT_MISSION, atMission);
    }
}

