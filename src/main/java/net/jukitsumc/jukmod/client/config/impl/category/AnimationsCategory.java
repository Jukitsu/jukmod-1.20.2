package net.jukitsumc.jukmod.client.config.impl.category;

import net.jukitsumc.jukmod.client.config.ModConfig;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.jukitsumc.jukmod.client.config.impl.option.BooleanOptionImpl;

public class AnimationsCategory extends Category {
    private final BooleanOption oldZombieArm;
    private final BooleanOption worldWar2;
    private final BooleanOption oldSwing;
    private final BooleanOption deathWalk;
    private final BooleanOption oldPlayerBackwards;
    private final BooleanOption oldBackwards;
    private final BooleanOption fixLeftHand;

    public AnimationsCategory(ModConfig modConfig) {
        super(modConfig);
        oldZombieArm = this.register(new BooleanOptionImpl("oldZombieArm", this, true));
        worldWar2 = this.register(new BooleanOptionImpl("worldWar2", this, true));
        oldSwing = this.register(new BooleanOptionImpl("oldSwing", this, true));
        deathWalk = this.register(new BooleanOptionImpl("deathWalk", this, true));
        oldPlayerBackwards = this.register(new BooleanOptionImpl("oldPlayerBackwards", this, true));
        oldBackwards = this.register(new BooleanOptionImpl("oldBackwards", this, true));
        fixLeftHand = this.register(new BooleanOptionImpl("fixLeftHand", this, true));
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

    public BooleanOption fixLeftHand() { return fixLeftHand; }
}
