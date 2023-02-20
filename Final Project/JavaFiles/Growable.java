import processing.core.PImage;

import java.util.List;

public abstract class Growable extends Executable implements Transformable {
    private int health;
    private final int healthLimit;

    public Growable(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.health = health;
        this.healthLimit = healthLimit;
    }

    public int getHealth() {return this.health;}
    public void addHealth(int value) {this.health += value;}
    public int getHealthLimit(){ return this.healthLimit;}
    public void setHealth(int health) {this.health = health;}
}
