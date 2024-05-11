package net.jukitsumc.jukmod.config.category;

import net.jukitsumc.jukmod.config.ModConfig;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.jukitsumc.jukmod.config.option.LongSliderOption;

public class GameplayCategory extends Category {
    private final BooleanOption missTime;
    private final BooleanOption dragonMovement;
    private final LongSliderOption knockbackType;

    public GameplayCategory(ModConfig modConfig) {
        super(modConfig);
        missTime = this.register(new BooleanOption("missTime", this, false));
        dragonMovement = this.register(new BooleanOption("dragonMovement", this, true));
        knockbackType = this.register(new LongSliderOption("knockbackType", this, (long)0, -2, 0));
    }

    @Override
    public String getId() {
        return "gameplay";
    }

    public BooleanOption missTime() {
        return missTime;
    }

    public BooleanOption dragonMovement() { return dragonMovement; }

    public LongSliderOption knockbackType() { return knockbackType; }


}
