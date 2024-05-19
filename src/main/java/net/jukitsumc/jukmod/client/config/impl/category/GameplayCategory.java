package net.jukitsumc.jukmod.client.config.impl.category;

import net.jukitsumc.jukmod.client.config.ModConfig;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.jukitsumc.jukmod.client.config.impl.option.BooleanOptionImpl;

public class GameplayCategory extends Category {
    private final BooleanOption missTime;
    private final BooleanOption dragonMovement;

    public GameplayCategory(ModConfig modConfig) {
        super(modConfig);
        missTime = this.register(new BooleanOptionImpl("missTime", this, false));
        dragonMovement = this.register(new BooleanOptionImpl("dragonMovement", this, true));
    }

    @Override
    public String getId() {
        return "gameplay";
    }

    public BooleanOption missTime() {
        return missTime;
    }

    public BooleanOption dragonMovement() { return dragonMovement; }


}
