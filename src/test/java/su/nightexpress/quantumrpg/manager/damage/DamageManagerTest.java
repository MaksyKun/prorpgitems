package su.nightexpress.quantumrpg.manager.damage;

import be.seeseemelk.mockbukkit.entity.FishHookMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.ZombieMock;
import org.bukkit.entity.FishHook;
import org.bukkit.event.player.PlayerFishEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import su.nightexpress.quantumrpg.config.EngineCfg;
import su.nightexpress.quantumrpg.testutil.MockedTest;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Disabled
class DamageManagerTest extends MockedTest {

    private PlayerMock player;

    @BeforeEach
    public void setup() {
        player = genPlayer("Travja");
    }

    @Test
    public void onDamageFishHook_enabledDoesDamage() {
        EngineCfg.COMBAT_FISHING_HOOK_DO_DAMAGE = true;
        ZombieMock entity = new ZombieMock(server, UUID.randomUUID());
        entity.setHealth(20);
        FishHook fishHook = new FishHookMock(server, UUID.randomUUID());
        new PlayerFishEvent(
                player,
                entity,
                fishHook,
                PlayerFishEvent.State.CAUGHT_ENTITY
        ).callEvent();

        assertEquals(19, entity.getHealth(), 0.001);
    }

    @Test
    public void onDamageFishHook_disabledDoesNoDamage() {
        EngineCfg.COMBAT_FISHING_HOOK_DO_DAMAGE = false;
        ZombieMock entity = new ZombieMock(server, UUID.randomUUID());
        entity.setHealth(20);
        FishHook fishHook = new FishHookMock(server, UUID.randomUUID());
        new PlayerFishEvent(
                player,
                entity,
                fishHook,
                PlayerFishEvent.State.CAUGHT_ENTITY
        ).callEvent();

        assertEquals(20, entity.getHealth(), 0.001);
    }

    @Test
    public void onDamageRPGStart() {
        assertTrue(true);
    }
}