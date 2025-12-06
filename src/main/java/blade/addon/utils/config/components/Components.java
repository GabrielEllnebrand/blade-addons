package blade.addon.utils.config.components;

import blade.addon.features.dungeon.ChestCounter;
import blade.addon.features.dungeon.DeathTickTimer;
import blade.addon.features.dungeon.DupeClassChecker;
import blade.addon.features.dungeon.KeyNotifier;
import blade.addon.features.dungeon.SecretSpawnTimer;
import blade.addon.features.dungeon.WarpCooldown;
import blade.addon.features.dungeon.f7.CrystalSpawn;
import blade.addon.features.dungeon.f7.DistanceToLedge;
import blade.addon.features.dungeon.f7.GoldorTickTimer;
import blade.addon.features.dungeon.f7.InvincibilityTimer;
import blade.addon.features.dungeon.f7.RelicTimer;
import blade.addon.features.dungeon.f7.StormTickTimer;
import blade.addon.features.dungeon.f7.TermStartTimer;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.Floor7;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;

public class Components {

    public static void init() {
    }

    @ConfigValue
    public static HUDComponent invincibilityTimer = new HUDComponent(0, 0, 110, 28, 1, "Invincibility timer", InvincibilityTimer::display, InvincibilityTimer::render, () -> Dungeons.displayInvincibilityTimer);

    @ConfigValue
    public static HUDComponent chestCounter = new HUDComponent(0, 0, 110, 10, 1, "Chest count", ChestCounter::display, ChestCounter::render, () -> Dungeons.displayChestCount);

    @ConfigValue
    public static HUDComponent deathTickTimer = new HUDComponent(0, 0, 30, 10, 1, "Death Tick Timer", DeathTickTimer::display, DeathTickTimer::render, () -> Dungeons.enableDeathTickTimer);

    @ConfigValue
    public static HUDComponent keyNotifierDisplay = new HUDComponent(0, 0, 120, 10, 1, "", KeyNotifier::display, KeyNotifier::render, () -> Dungeons.enableKeyNotifier);

    @ConfigValue
    public static HUDComponent duplicateClassDisplay = new HUDComponent(0, 0, 130, 10, 1, "", DupeClassChecker::display, DupeClassChecker::render, () -> Dungeons.detectDuplicateClass);

    @ConfigValue
    public static HUDComponent secretSpawnTimer = new HUDComponent(0, 0, 20, 10, 1, "Secret spawn timer", SecretSpawnTimer::display, SecretSpawnTimer::render, () -> Dungeons.enableSecretSpawnTimer);

    @ConfigValue
    public static HUDComponent warpCoolDown = new HUDComponent(0, 0, 110, 10, 0.75f, "Warp cooldown", WarpCooldown::display, WarpCooldown::render, () ->Dungeons.enableWarpCooldown);

    @ConfigValue
    public static HUDComponent crystalSpawnTime = new HUDComponent(0, 0, 30, 10, 1, "Crystal Spawn Time", CrystalSpawn::display, CrystalSpawn::render, () -> Floor7.enableCrystalSpawnTime);

    @ConfigValue
    public static HUDComponent stormTickTimer = new HUDComponent(0, 0, 30, 10, 1, "Storm Tick Timer", StormTickTimer::display, StormTickTimer::render, () -> Floor7.enableStormTickTimer);

    @ConfigValue
    public static HUDComponent stormDeathTime = new HUDComponent(0, 0, 30, 10, 1, "Storm Death Time", StormTickTimer::displayDeathTime, StormTickTimer::renderDeathTime, () -> Floor7.enableStormDeathTime);

    @ConfigValue
    public static HUDComponent distanceToLedgeComponent = new HUDComponent(0, 0, 30, 10, 1, "Distance to ledge", DistanceToLedge::display, DistanceToLedge::render, () -> Floor7.displayDistanceToLedge);

    @ConfigValue
    public static HUDComponent goldorTickTimer = new HUDComponent(0, 0, 30, 10, 1, "Goldor Tick Timer", GoldorTickTimer::display, GoldorTickTimer::render, () -> Floor7.enableGoldorTickTimer);

    @ConfigValue
    public static HUDComponent termStartTimer = new HUDComponent(0, 0, 30, 10, 1, "Term Start Timer", TermStartTimer::display, TermStartTimer::render, () -> Floor7.enableTermStartTimer);

    @ConfigValue
    public static HUDComponent relicSpawnTimer = new HUDComponent(0, 0, 30, 10, 1, "Relic Spawn Timer", RelicTimer::display, RelicTimer::render, () -> Floor7.enableRelicStartTimer);
}
