package net.jukitsumc.jukmod.config.category;

import net.jukitsumc.jukmod.config.ModConfig;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.jukitsumc.jukmod.config.option.LongSliderOption;

public class EntitiesCategory extends Category {
    private final BooleanOption oldClientMovement;
    private final LongSliderOption entityLerpSteps;
    private final BooleanOption lerpPlayerVelocity;

    public EntitiesCategory(ModConfig modConfig) {
        super(modConfig);
        oldClientMovement = this.register(new BooleanOption("oldClientMovement", this, true));
        entityLerpSteps = this.register(new LongSliderOption("entityLerpSteps", this, (long)1, 1, 6));
        lerpPlayerVelocity = this.register(new BooleanOption("lerpPlayerVelocity", this, false));
    }

    @Override
    public String getId() {
        return "entities";
    }

    public BooleanOption oldClientMovement() {
        return oldClientMovement;
    }

    public LongSliderOption entityLerpSteps() {
        return entityLerpSteps;
    }

    public BooleanOption lerpPlayerVelocity() {
        return lerpPlayerVelocity;
    }

}

