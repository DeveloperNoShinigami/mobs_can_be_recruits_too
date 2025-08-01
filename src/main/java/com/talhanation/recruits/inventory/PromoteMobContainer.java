package com.talhanation.recruits.inventory;

import com.talhanation.recruits.Main;
import com.talhanation.recruits.init.ModScreens;
import de.maxhenkel.corelib.inventory.ContainerBase;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class PromoteMobContainer extends ContainerBase {
    private final Player player;
    private final Mob mob;

    public PromoteMobContainer(int id, Player player, Mob mob) {
        super(ModScreens.PROMOTE.get(), id, player.getInventory(), new SimpleContainer(0));
        this.player = player;
        this.mob = mob;
    }

    public Player getPlayerEntity() {
        return player;
    }

    public Mob getMob() {
        return mob;
    }
}
