package com.talhanation.recruits.util;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Simple interface for classes that can process recruitment interaction
 * logic for arbitrary {@link Mob} instances.
 */
public interface RecruitHandler {
    /**
     * Handle interaction with a mob that may be recruited.
     *
     * @param event the original interaction event
     * @param mob   the mob that was interacted with
     */
    void handle(PlayerInteractEvent event, Mob mob);
}
