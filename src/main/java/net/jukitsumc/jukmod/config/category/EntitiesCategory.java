package net.jukitsumc.jukmod.config.category;

import net.jukitsumc.jukmod.config.ModConfig;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.jukitsumc.jukmod.config.option.LongSliderOption;

public class EntitiesCategory extends Category {
    private final BooleanOption oldClientMovement;
    private final LongSliderOption entityLerpSteps;
    private final LongSliderOption entityUpdateInterval;
    private final BooleanOption lerpPlayerVelocity;
    private final BooleanOption remotePlayerPhysics;
    private final BooleanOption universalEntityUpdateInterval;

    public EntitiesCategory(ModConfig modConfig) {
        super(modConfig);
        oldClientMovement = this.register(new BooleanOption("oldClientMovement", this, true));
        entityLerpSteps = this.register(new LongSliderOption("entityLerpSteps", this, (long)3, 1, 6));
        entityUpdateInterval = this.register(new LongSliderOption("entityUpdateInterval", this, (long)1, 1, 6));
        lerpPlayerVelocity = this.register(new BooleanOption("lerpPlayerVelocity", this, false));
        remotePlayerPhysics = this.register(new BooleanOption("remotePlayerPhysics", this, false));
        universalEntityUpdateInterval = this.register(new BooleanOption("universalEntityUpdateInterval", this, false));
    }

    @Override
    public String getId() {
        return "entities";
    }

    public BooleanOption oldClientMovement() {
        return this.oldClientMovement;
    }

    public LongSliderOption entityLerpSteps() {
        return this.entityLerpSteps;
    }

    public LongSliderOption entityUpdateInterval() { return this.entityUpdateInterval; }

    public BooleanOption lerpPlayerVelocity() {
        return this.lerpPlayerVelocity;
    }

    public BooleanOption remotePlayerPhysics() {
        return this.remotePlayerPhysics;
    }

    public BooleanOption universalEntityUpdateInterval() {
        return this.universalEntityUpdateInterval;
    }

}

