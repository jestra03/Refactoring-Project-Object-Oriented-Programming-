import processing.core.PImage;

import java.util.List;

public abstract class Plant extends Growable implements Transformable{
    public Plant(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        super(id, position, images, actionPeriod, animationPeriod, health, healthLimit);
    }
}
