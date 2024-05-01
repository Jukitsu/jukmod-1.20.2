package net.jukitsumc.jukmod.config.category;

import net.jukitsumc.jukmod.config.ModConfig;
import net.jukitsumc.jukmod.config.option.BooleanOption;

public class AnimationsCategory extends Category {
    private final BooleanOption oldZombieArm;
    private final BooleanOption worldWar2;
    private final BooleanOption oldSwing;
    private final BooleanOption deathWalk;
    private final BooleanOption oldPlayerBackwards;
    private final BooleanOption oldBackwards;

    public AnimationsCategory(ModConfig modConfig) {
        super(modConfig);
        oldZombieArm = this.register(new BooleanOption("oldZombieArm", this, true));
        worldWar2 = this.register(new BooleanOption("worldWar2", this, true));
        oldSwing = this.register(new BooleanOption("oldSwing", this, true));
        deathWalk = this.register(new BooleanOption("deathWalk", this, true));
        oldPlayerBackwards = this.register(new BooleanOption("oldPlayerBackwards", this, true));
        oldBackwards = this.register(new BooleanOption("oldBackwards", this, true));
    }

    @Override
    public String getId() {
        return "animations";
    }

    public BooleanOption oldZombieArm() {
        return oldZombieArm;
    }

    public BooleanOption worldWar2() {
        return worldWar2;
    }

    public BooleanOption oldSwing() {
        return oldSwing;
    }

    public BooleanOption deathWalk() {
        return deathWalk;
    }

    public BooleanOption oldPlayerBackwards() {
        return oldPlayerBackwards;
    }

    public BooleanOption oldBackwards() {
        return oldBackwards;
    }
}
