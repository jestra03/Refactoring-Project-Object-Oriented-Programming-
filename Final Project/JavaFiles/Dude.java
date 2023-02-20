import processing.core.PImage;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class Dude extends Moveable implements Transformable{
    private int resourceLimit;
    private int damage = 1;
    public Dude(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int resourceLimit) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
    }

    public int getResourceLimit(){return this.resourceLimit;}
    public void setLumberJack() {
        this.setAnimationPeriod(.15);
        this.resourceLimit = 5;
        this.damage = 4;
    }
    public int getDamage() {
        return this.damage;
    }
}
