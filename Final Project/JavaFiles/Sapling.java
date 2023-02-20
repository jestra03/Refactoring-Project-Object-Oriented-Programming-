import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Sapling extends Growable {
    private final double TREE_ANIMATION_MAX = 0.600;
    private final double TREE_ANIMATION_MIN = 0.050;
    private final double TREE_ACTION_MAX = 1.400;
    private final double TREE_ACTION_MIN = 1.000;
    private final int TREE_HEALTH_MAX = 3;
    private final int TREE_HEALTH_MIN = 1;
    public Sapling(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        super(id, position, images, actionPeriod, animationPeriod, health, healthLimit);
    }

    public void execute(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        super.addHealth(1);
        if (!this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Activity.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getHealth() <= 0) {
            Entity stump = world.createStump(WorldModel.STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(WorldModel.STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        } else if (this.getHealth() >= this.getHealthLimit()) {
            Entity tree = world.createTree(WorldModel.TREE_KEY + "_" + this.getId(), this.getPosition(), getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN), getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN), imageStore.getImageList(WorldModel.TREE_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            ((Actionable)tree).scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    // used only in Sapling
    public int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }

    // used only in Sapling
    public double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }
}
