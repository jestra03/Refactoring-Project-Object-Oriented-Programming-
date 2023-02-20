import processing.core.PImage;

import java.util.List;

public abstract class Executable extends Actionable {
    private final double actionPeriod;

    public Executable(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images, animationPeriod);
        this.actionPeriod = actionPeriod;
    }

    public abstract void execute(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    // Dude_Full, Dude_Not_Full, Fairy, Sapling, Tree implement same scheduleActions
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Activity.createActivityAction(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, Animation.createAnimationAction(this, 0), this.getAnimationPeriod());
    }
    public double getActionPeriod() {return this.actionPeriod;}
}
