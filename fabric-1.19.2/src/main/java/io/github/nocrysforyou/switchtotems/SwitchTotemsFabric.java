package io.github.nocrysforyou.switchtotems;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

public class SwitchTotemsFabric implements ClientModInitializer {
    private static final KeyMapping TOGGLE_KEY = new KeyMapping("switchtotems.toggle", -1, "switchtotems.category");
    private static final KeyMapping CONFIG_KEY = new KeyMapping("switchtotems.config", -1, "switchtotems.category");

    @Override
    public void onInitializeClient() {
        SwitchTotems.loadConfig(FabricLoader.getInstance().getConfigDir());
        KeyBindingHelper.registerKeyBinding(TOGGLE_KEY);
        KeyBindingHelper.registerKeyBinding(CONFIG_KEY);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> SwitchTotems.reset());
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> SwitchTotems.reset());
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (TOGGLE_KEY.consumeClick()) {
                SwitchTotems.enabled = !SwitchTotems.enabled;
                SystemToast.addOrUpdate(client.getToasts(), SystemToast.SystemToastIds.NARRATOR_TOGGLE,
                        Component.literal("SwitchTotems"),
                        Component.translatable("switchtotems.toggle." + SwitchTotems.enabled)
                                .withStyle(SwitchTotems.enabled ? ChatFormatting.GREEN : ChatFormatting.RED));
            }
            if (CONFIG_KEY.consumeClick()) {
                client.setScreen(new SwitchTotemsScreen(null));
            }
        });
        SwitchTotems.LOG.info("It's not a hack®, you have to click™.");
    }
}
