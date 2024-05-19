package net.jukitsumc.jukmod.client.config.impl.category;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.jukitsumc.jukmod.client.config.ModConfig;
import net.jukitsumc.jukmod.client.config.impl.option.OptionImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Category {
    private final Map<String, OptionImpl<?, ?>> options = new HashMap<>();

    private final ModConfig modConfig;

    protected Category(ModConfig modConfig) {
        this.modConfig = modConfig;
    }

    public ModConfig getModConfig() {
        return this.modConfig;
    }

    public abstract String getId();

    public String getTranslationKey() {
        return ModConfig.TRANSLATION_KEY + "." + this.getId();
    }

    public Map<String, OptionImpl<?, ?>> getOptions() {
        return new HashMap<>(options);
    }

    public <T extends OptionImpl<?, ?>> T register(T option) {
        options.put(option.getId(), option);
        return option;
    }

    public void loadOptions(Map<String, Object> map) {
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                OptionImpl<?, ?> option = this.options.get(key);
                if (option != null) option.load(entry.getValue());
            }
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        for (OptionImpl<?, ?> option : this.getOptions().values()) {
            map.put(option.getId(), option.getInConfigFormat());
        }

        return map;
    }

    public List<AbstractConfigListEntry<?>> getConfigEntries() {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();
        for (OptionImpl<?, ?> option : this.getOptions().values()) {
            entries.add(option.getConfigEntry());
        }

        return entries;
    }
}
