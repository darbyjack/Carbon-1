package net.draycia.carbon.sponge.users;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.draycia.carbon.api.util.InventorySlot;
import net.draycia.carbon.api.util.InventorySlots;
import net.draycia.carbon.common.users.CarbonPlayerCommon;
import net.draycia.carbon.common.users.WrappedCarbonPlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.util.locale.LocaleSource;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

@DefaultQualifier(NonNull.class)
public final class CarbonPlayerSponge extends WrappedCarbonPlayer implements ForwardingAudience.Single {

    private final CarbonPlayerCommon carbonPlayerCommon;

    public CarbonPlayerSponge(final CarbonPlayerCommon carbonPlayerCommon) {
        this.carbonPlayerCommon = carbonPlayerCommon;
    }

    @Override
    public @NotNull Audience audience() {
        return this.player()
            .map(player -> (Audience) player)
            .orElseGet(Audience::empty);
    }

    @Override
    public CarbonPlayerCommon carbonPlayerCommon() {
        return this.carbonPlayerCommon;
    }

    private Optional<ServerPlayer> player() {
        return Sponge.server().player(this.carbonPlayerCommon.uuid());
    }

    //    @Override
    //    public void displayName(final @Nullable Component displayName) {
    //        this.carbonPlayer.displayName(displayName);
    //
    //        this.player().ifPresent(player -> {
    //            if (displayName != null) {
    //                player.offer(Keys.CUSTOM_NAME, displayName);
    //            } else {
    //                player.remove(Keys.CUSTOM_NAME);
    //            }
    //        });
    //    }

    @Override
    public boolean hasPermission(final String permission) {
        final var player = this.player();

        // Ignore inspection. Don't make code harder to read, IntelliJ.
        return player.map(serverPlayer -> serverPlayer.hasPermission(permission))
            .orElse(false);
    }

    @Override
    public void sendMessageAsPlayer(final String message) {
        this.player().ifPresent(player -> player.simulateChat(Component.text(message), Cause.builder().build()));
    }

    @Override
    public boolean online() {
        final var player = this.player();
        return player.isPresent() && player.get().isOnline();
    }

    @Override
    public @Nullable Locale locale() {
        return this.player().map(LocaleSource::locale).orElse(null);
    }

    @Override
    public void displayName(final @Nullable Component displayName) {
        this.carbonPlayerCommon.displayName(displayName);
    }

    @Override
    public void temporaryDisplayName(final @Nullable Component displayName, final long expirationEpoch) {
        this.carbonPlayerCommon.temporaryDisplayName(displayName, expirationEpoch);
    }

    @Override
    public @Nullable Component createItemHoverComponent(final InventorySlot slot) {
        final Optional<ServerPlayer> optionalPlayer = this.player(); // This is temporary (it's not)

        if (optionalPlayer.isEmpty()) {
            return null;
        }

        final ServerPlayer player = optionalPlayer.get();
        final EquipmentType equipmentSlot;

        if (slot.equals(InventorySlots.MAIN_HAND)) {
            equipmentSlot = EquipmentTypes.MAIN_HAND.get();
        } else if (slot.equals(InventorySlots.OFF_HAND)) {
            equipmentSlot = EquipmentTypes.OFF_HAND.get();
        } else if (slot.equals(InventorySlots.HELMET)) {
            equipmentSlot = EquipmentTypes.HEAD.get();
        } else if (slot.equals(InventorySlots.CHEST)) {
            equipmentSlot = EquipmentTypes.CHEST.get();
        } else if (slot.equals(InventorySlots.LEGS)) {
            equipmentSlot = EquipmentTypes.LEGS.get();
        } else if (slot.equals(InventorySlots.BOOTS)) {
            equipmentSlot = EquipmentTypes.FEET.get();
        } else {
            return null;
        }

        final Optional<ItemStack> equipment = player.equipment().peek(equipmentSlot);

        if (equipment.isEmpty()) {
            return null;
        }

        final @Nullable ItemStack itemStack = equipment.get();

        return this.fromStack(itemStack);
    }

    private Component fromStack(final ItemStack stack) {
        return stack.get(Keys.DISPLAY_NAME)

            // This is here as a fallback, but really, every ItemStack should
            // have a DISPLAY_NAME which is already formatted properly for us by the game.
            .orElseGet(() -> translatable()
                .key("chat.square_brackets")
                .args(stack.get(Keys.CUSTOM_NAME)
                    .map(name -> name.decorate(ITALIC))
                    .orElseGet(() -> stack.type().asComponent()))
                .hoverEvent(stack.createSnapshot())
                .apply(builder -> stack.get(Keys.ITEM_RARITY).ifPresent(rarity -> builder.color(rarity.color())))
                .build());
    }

    @Override
    public String primaryGroup() {
        return "default";
    }

    @Override
    public List<String> groups() {
        return List.of("default"); // TODO: implement
    }

    @Override
    public boolean vanished() {
        return false;
    }

    @Override
    public boolean awareOf(final CarbonPlayer other) {
        if (other.vanished()) {
            return this.hasPermission("carbon.seevanished");
        }

        return true;
    }

}
