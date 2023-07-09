package io.github.nocrysforyou.switchtotems;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Shared SwitchTotems class.
 *
 * @author NoCrysForYou
 */
public class SwitchTotems {
    public static final Logger LOG = LoggerFactory.getLogger("SwitchTotems");
    public static final Gson GSON = new Gson();
    public static boolean swapping;
    public static boolean ensureYourFreedomIsRespectedByFilthyServerAntiCheats;
    public static boolean ensureYourFreedomIsSecuredFromNastyServerAdmins;
    public static boolean enabled = true;
    public static int scrollStartDelay = 0;
    public static int swapDelay = 0;
    public static int scrollEndDelay = 0;
    public static int closeDelay = 0;

    public static void reset() {
        swapping = false;
        ensureYourFreedomIsRespectedByFilthyServerAntiCheats = false;
        ensureYourFreedomIsSecuredFromNastyServerAdmins = false;
    }

    // stolen from HCsCR 🐗

    public static void loadConfig(Path path) {
        try {
            Path file = path.resolve("switchtotems.json");
            if (!Files.isRegularFile(file)) return;
            JsonObject json = GSON.fromJson(Files.readString(file), JsonObject.class);
            enabled = json.has("enabled") ? json.get("enabled").getAsBoolean() : enabled;
            scrollStartDelay = json.has("scrollStartDelay") ? json.get("scrollStartDelay").getAsInt() : scrollStartDelay;
            swapDelay = json.has("swapDelay") ? json.get("swapDelay").getAsInt() : swapDelay;
            scrollEndDelay = json.has("scrollEndDelay") ? json.get("scrollEndDelay").getAsInt() : scrollEndDelay;
            closeDelay = json.has("closeDelay") ? json.get("closeDelay").getAsInt() : closeDelay;
        } catch (Exception e) {
            LOG.warn("Unable to load SwitchTotems config.", e);
        }
    }

    public static void saveConfig(Path path) {
        try {
            Path file = path.resolve("switchtotems.json");
            Files.createDirectories(file.getParent());
            JsonObject json = new JsonObject();
            json.addProperty("enabled", enabled);
            json.addProperty("scrollStartDelay", scrollStartDelay);
            json.addProperty("swapDelay", swapDelay);
            json.addProperty("scrollEndDelay", scrollEndDelay);
            json.addProperty("closeDelay", closeDelay);
            Files.writeString(file, GSON.toJson(json));
        } catch (Exception e) {
            LOG.warn("Unable to save SwitchTotems config.", e);
        }
    }
}
