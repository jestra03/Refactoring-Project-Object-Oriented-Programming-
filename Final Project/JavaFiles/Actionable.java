import processing.core.PImage;

import java.util.List;

public abstract class Actionable extends Entity {
    private double animationPeriod;

    public Actionable(String id, Point position, List<PImage> images, double animationPeriod) {
        super(id, position, images);
        this.animationPeriod = animationPeriod;
    }

    public double getAnimationPeriod() {return this.animationPeriod;}
    public abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
    public void setAnimationPeriod(double v) {
        this.animationPeriod = v;
    }
}
