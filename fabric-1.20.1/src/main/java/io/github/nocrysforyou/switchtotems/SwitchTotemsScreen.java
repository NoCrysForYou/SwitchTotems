package io.github.nocrysforyou.switchtotems;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

// stolen from HCsCR 🐗
// hcscr authors ez
public class SwitchTotemsScreen extends Screen {
    private final Screen parent;
    private Checkbox enabled;

    public SwitchTotemsScreen(Screen parent) {
        super(Component.translatable("switchtotems.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addRenderableWidget(enabled = new Checkbox(
                width / 2 - 12 - font.width(Component.translatable("switchtotems.enabled")) / 2, 40,
                24 + font.width(Component.translatable("switchtotems.enabled")), 20,
                Component.translatable("switchtotems.enabled"), SwitchTotems.enabled));
        addRenderableWidget(new Slider(width / 2 - 100, 64, 200, 20,
                SwitchTotems.scrollStartDelay, 0, 200, value -> SwitchTotems.scrollStartDelay = value,
                value -> CommonComponents.optionNameValue(Component.translatable("switchtotems.scrollStartDelay"),
                        Component.literal(Integer.toString(value)))));
        addRenderableWidget(new Slider(width / 2 - 100, 88, 200, 20,
                SwitchTotems.swapDelay, 0, 200, value -> SwitchTotems.swapDelay = value,
                value -> CommonComponents.optionNameValue(Component.translatable("switchtotems.swapDelay"),
                        Component.literal(Integer.toString(value)))));
        addRenderableWidget(new Slider(width / 2 - 100, 112, 200, 20,
                SwitchTotems.scrollEndDelay, 0, 200, value -> SwitchTotems.scrollEndDelay = value,
                value -> CommonComponents.optionNameValue(Component.translatable("switchtotems.scrollEndDelay"),
                        Component.literal(Integer.toString(value)))));
        addRenderableWidget(new Slider(width / 2 - 100, 136, 200, 20,
                SwitchTotems.closeDelay, 0, 200, value -> SwitchTotems.closeDelay = value,
                value -> CommonComponents.optionNameValue(Component.translatable("switchtotems.closeDelay"),
                        Component.literal(Integer.toString(value)))));
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> minecraft.setScreen(parent)).bounds(width / 2 - 75, height - 24, 150, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(font, title, width / 2, 10, -1);
        graphics.drawCenteredString(font, Component.translatable("switchtotems.slogan"), width / 2, 20, 0xFFFF0000);
    }

    @Override
    public void removed() {
        super.removed();
        SwitchTotems.reset();
        SwitchTotems.enabled = enabled.selected();
        SwitchTotems.saveConfig(FabricLoader.getInstance().getConfigDir());
    }


    private static final class Slider extends AbstractSliderButton {
        private final int min;
        private final int max;
        private final IntConsumer handler;
        private final IntFunction<Component> messageProvider;

        public Slider(int x, int y, int width, int height, int value, int min, int max,
                      IntConsumer handler, IntFunction<Component> messageProvider) {
            super(x, y, width, height, messageProvider.apply(value), (value - min) / (double) (max - min));
            this.min = min;
            this.max = max;
            this.handler = handler;
            this.messageProvider = messageProvider;
        }

        @Override
        protected void updateMessage() {
            int clamped = (int) (min + value * (max - min));
            if (clamped < min) clamped = min;
            if (clamped > max) clamped = max;
            setMessage(messageProvider.apply(clamped));
        }

        @Override
        protected void applyValue() {
            int clamped = (int) (min + value * (max - min));
            if (clamped < min) clamped = min;
            if (clamped > max) clamped = max;
            handler.accept(clamped);
        }
    }
}
