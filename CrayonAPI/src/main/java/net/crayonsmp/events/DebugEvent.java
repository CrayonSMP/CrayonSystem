package net.crayonsmp.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DebugEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public DebugEvent() {
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
