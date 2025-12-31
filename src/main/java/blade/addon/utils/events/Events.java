package blade.addon.utils.events;

import blade.addon.utils.events.interfaces.BlockEntityEvent;
import blade.addon.utils.events.interfaces.BlockInteractionEvent;
import blade.addon.utils.events.interfaces.CancelableMessageEvent;
import blade.addon.utils.events.interfaces.EntityEvent;
import blade.addon.utils.events.interfaces.GameMessageEvent;
import blade.addon.utils.events.interfaces.LeapEvent;
import blade.addon.utils.events.interfaces.LocationChangeEvent;
import blade.addon.utils.events.interfaces.ParticleEvent;
import blade.addon.utils.events.interfaces.PartyMessageEvent;
import blade.addon.utils.events.interfaces.PetEvent;
import blade.addon.utils.events.interfaces.PhaseEvent;
import blade.addon.utils.events.interfaces.PlaySoundEvent;
import blade.addon.utils.events.interfaces.PlayerListEvent;
import blade.addon.utils.events.interfaces.RunEndEvent;
import blade.addon.utils.events.interfaces.ScoreBoardEvent;
import blade.addon.utils.events.interfaces.SectionEvent;
import blade.addon.utils.events.interfaces.ServerTickEvent;
import blade.addon.utils.events.interfaces.SlotChangeEvent;
import blade.addon.utils.events.interfaces.TerminalEvent;

public class Events {
    public static final EventHandler<ServerTickEvent> ON_SERVER_TICK = new EventHandler<>();
    public static final EventHandler<SlotChangeEvent> ON_SLOT_CHANGE = new EventHandler<>();
    public static final EventHandler<LocationChangeEvent> ON_LOCATION_CHANGE = new EventHandler<>();
    public static final EventHandler<LeapEvent> ON_LEAP = new EventHandler<>();
    public static final EventHandler<RunEndEvent> ON_RUN_END = new EventHandler<>();
    public static final EventHandler<PlayerListEvent> ON_PLAYER_ENTRY = new EventHandler<>();
    public static final EventHandler<ScoreBoardEvent> ON_TEAM = new EventHandler<>();
    public static final EventHandler<PhaseEvent> ON_PHASE_CHANGE = new EventHandler<>();
    public static final EventHandler<ParticleEvent> ON_PARTICLE = new EventHandler<>();
    public static final EventHandler<PetEvent> ON_PET = new EventHandler<>();
    public static final EventHandler<PartyMessageEvent> ON_PARTY_MESSAGE = new EventHandler<>();

    public static final EventHandler<TerminalEvent> ON_TERMINAL = new EventHandler<>();
    public static final EventHandler<SectionEvent> ON_SECTION_CHANGE = new EventHandler<>();

    public static final EventHandler<EntityEvent> ON_ENTITY_TRACKED = new EventHandler<>();
    public static final EventHandler<EntityEvent> ON_ENTITY_SPAWNED = new EventHandler<>();

    public static final EventHandler<GameMessageEvent> ON_GAME_MESSAGE = new EventHandler<>();
    public static final CancelableEventHandler<CancelableMessageEvent> ON_CANCELABLE_GAME_MESSAGE = new CancelableEventHandler<>();
    public static final CancelableEventHandler<BlockInteractionEvent> ON_BLOCK_INTERACTION = new CancelableEventHandler<>();

    public static final CancelableEventHandler<PlaySoundEvent> ON_SOUND = new CancelableEventHandler<>();

    public static final EventHandler<BlockEntityEvent> ON_BLOCK_ENTITY = new EventHandler<>();


}
