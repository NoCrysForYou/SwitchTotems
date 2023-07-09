package io.github.nocrysforyou.switchtotems;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class SwitchTotemsMM implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SwitchTotemsScreen::new;
    }
}
