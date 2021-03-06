package fr.doctaenkoda.cmi.pl3xmapcmi.utils.configuration;

import fr.doctaenkoda.cmi.pl3xmapcmi.Pl3xmapCmi;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * This code was create by BillyGalbreath
 * https://github.com/pl3xgaming/Pl3xMap-Essentials/blob/master/src/main/java/net/pl3x/map/essentials/configuration/Config.java
 */

@SuppressWarnings("unused")
public class Config {
    private static File CONFIG_FILE;
    static YamlConfiguration CONFIG;
    static int VERSION;

    public static void reload(Plugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder(), "config.yml");
        CONFIG = new YamlConfiguration();
        try {
            CONFIG.load(CONFIG_FILE);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            Pl3xmapCmi.getLog().severe("Could not load config.yml, please correct your syntax errors");
            throw new RuntimeException(ex);
        }
        CONFIG.options().copyDefaults(true);

        VERSION = getInt("config-version", 1);
        set("config-version", 1);

        readConfig(Config.class, null);

        WorldConfig.reload();
    }

    static void readConfig(Class<?> clazz, Object instance) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(instance);
                    } catch (InvocationTargetException ex) {
                        throw new RuntimeException(ex.getCause());
                    } catch (Exception ex) {
                        Pl3xmapCmi.getLog().severe("Error invoking " + method);
                        ex.printStackTrace();
                    }
                }
            }
        }

        try {
            CONFIG.save(CONFIG_FILE);
        } catch (IOException ex) {
            Pl3xmapCmi.getLog().severe("Could not save " + CONFIG_FILE);
            ex.printStackTrace();
        }
    }

    private static void set(String path, Object val) {
        CONFIG.addDefault(path, val);
        CONFIG.set(path, val);
    }

    private static String getString(String path, String def) {
        CONFIG.addDefault(path, def);
        return CONFIG.getString(path, CONFIG.getString(path));
    }

    private static boolean getBoolean(String path, boolean def) {
        CONFIG.addDefault(path, def);
        return CONFIG.getBoolean(path, CONFIG.getBoolean(path));
    }

    private static int getInt(String path, int def) {
        CONFIG.addDefault(path, def);
        return CONFIG.getInt(path, CONFIG.getInt(path));
    }

    public static boolean DEBUG_MODE = false;
    public static int UPDATE_INTERVAL = 5;
    public static boolean HIDE_VANISHED = true;

    private static void baseSettings() {
        DEBUG_MODE = getBoolean("settings.debug-mode", DEBUG_MODE);
        UPDATE_INTERVAL = getInt("settings.update-interval", UPDATE_INTERVAL);
        HIDE_VANISHED = getBoolean("settings.hide-vanished", HIDE_VANISHED);
    }
}