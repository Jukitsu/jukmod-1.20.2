package net.jukitsumc.jukmod.config.category;

import net.jukitsumc.jukmod.config.ModConfig;
import net.jukitsumc.jukmod.config.option.BooleanOption;

public class GameplayCategory extends Category {
    private final BooleanOption missTime;

    public GameplayCategory(ModConfig modConfig) {
        super(modConfig);
        missTime = this.register(new BooleanOption("missTime", this, false));
    }

    @Override
    public String getId() {
        return "gameplay";
    }

    public BooleanOption missTime() {
        return missTime;
    }


}
