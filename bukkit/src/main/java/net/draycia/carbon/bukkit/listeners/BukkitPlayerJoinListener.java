package net.draycia.carbon.bukkit.listeners;

import com.google.inject.Inject;
import net.draycia.carbon.api.CarbonChat;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.draycia.carbon.api.users.ComponentPlayerResult;
import net.draycia.carbon.common.config.PrimaryConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitPlayerJoinListener implements Listener {

    private final CarbonChat carbonChat;
    private final PrimaryConfig primaryConfig;

    @Inject
    public BukkitPlayerJoinListener(
        final CarbonChat carbonChat,
        final PrimaryConfig primaryConfig
    ) {
        this.carbonChat = carbonChat;
        this.primaryConfig = primaryConfig;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(final PlayerJoinEvent event) {
        final ComponentPlayerResult result = this.carbonChat.server().player(event.getPlayer().getUniqueId()).join();

        if (result.player() == null) {
            return;
        }

        final CarbonPlayer player = result.player();

        // Don't show join messages when muted
        if (this.primaryConfig.hideMutedJoinLeaveQuit() && player.muted()) {
            event.joinMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        // Early exit in case "hide muted join / leave messages when muted" is disabled
        if (!this.primaryConfig.hideMutedJoinLeaveQuit()) {
            return;
        }

        final ComponentPlayerResult result = this.carbonChat.server().player(event.getPlayer().getUniqueId()).join();

        if (result.player() == null) {
            return;
        }

        final CarbonPlayer player = result.player();

        // Don't show quit messages when muted
        if (player.muted()) {
            event.quitMessage(null);
        }
    }

}