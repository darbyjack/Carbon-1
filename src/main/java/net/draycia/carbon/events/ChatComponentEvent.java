package net.draycia.carbon.events;

import net.draycia.carbon.channels.ChatChannel;
import net.draycia.carbon.storage.ChatUser;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ChatComponentEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled = false;

    private @NonNull final ChatUser sender;
    private @Nullable final ChatUser target;
    private ChatChannel chatChannel;
    private TextComponent component;
    private final String originalMessage;

    public ChatComponentEvent(@NonNull ChatUser sender, @Nullable ChatUser target, ChatChannel chatChannel, TextComponent component, String originalMessage) {
        super(!Bukkit.isPrimaryThread());

        this.sender = sender;
        this.target = target;
        this.chatChannel = chatChannel;
        this.component = component;
        this.originalMessage = originalMessage;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    @NonNull
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static @NonNull HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @NonNull
    public ChatUser getSender() {
        return sender;
    }

    @Nullable
    public ChatUser getTarget() {
        return target;
    }

    public ChatChannel getChannel() {
        return chatChannel;
    }

    public void setChannel(ChatChannel chatChannel) {
        this.chatChannel = chatChannel;
    }

    public TextComponent getComponent() {
        return component;
    }

    public void setComponent(TextComponent component) {
        this.component = component;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

}
