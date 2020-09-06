package net.draycia.carbon.listeners;

import net.draycia.carbon.events.ChatFormatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;

public class IgnoredPlayerHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPapiPlaceholder(@NonNull ChatFormatEvent event) {
        if (event.getTarget() == null) {
            return;
        }

        if (event.getTarget().isIgnoringUser(event.getSender())) {
            event.setCancelled(true);
        }
    }

}
