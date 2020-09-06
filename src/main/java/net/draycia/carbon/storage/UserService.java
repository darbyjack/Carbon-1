package net.draycia.carbon.storage;

import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public interface UserService {

    @Nullable ChatUser wrap(String name);
    @Nullable ChatUser wrap(OfflinePlayer player);
    @Nullable ChatUser wrap(UUID uuid);

    @Nullable
    default ChatUser wrapIfLoaded(@NonNull OfflinePlayer player) {
        return wrapIfLoaded(player.getUniqueId());
    }

    @Nullable @Nullable
    ChatUser wrapIfLoaded(UUID uuid);

    @Nullable ChatUser refreshUser(UUID uuid);

    void onDisable();

    void invalidate(ChatUser user);
    void validate(ChatUser user);

}
