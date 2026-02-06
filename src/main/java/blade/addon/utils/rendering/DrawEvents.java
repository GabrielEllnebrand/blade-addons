package blade.addon.utils.rendering;

import blade.addon.utils.rendering.drawevents.SlotEvent;

public class DrawEvents {
    public static DrawHandler<SlotEvent> INVENTORY_SLOT_AFTER = new DrawHandler<>();
    public static DrawHandler<SlotEvent> INVENTORY_SLOT_BEFORE = new DrawHandler<>();
    public static DrawHandler<SlotEvent> HUD_SLOT_AFTER = new DrawHandler<>();
    public static DrawHandler<SlotEvent> HUD_SLOT_BEFORE = new DrawHandler<>();
}
