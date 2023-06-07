package dev.venom.check.impl.player.inventory;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Inventory (B)", category = Category.PLAYER)
public final class InventoryB extends Check {

    private boolean attacking, swinging;

    public InventoryB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isClientCommand()) {
            if(!data.getActionProcessor().isInventory()) return;

            if (attacking || swinging) {
                if (buffer++ > 4) {
                    fail("a: " + attacking + ", s: " + swinging);
                }
            } else {
                buffer = 0;
            }

        } else if (packet.isFlying()) {
            attacking = false;
            swinging = false;
        } else if (packet.isArmAnimation()) {
            swinging = true;
        } else if (packet.isUseEntity()) {
            attacking = true;
        }
    }
}