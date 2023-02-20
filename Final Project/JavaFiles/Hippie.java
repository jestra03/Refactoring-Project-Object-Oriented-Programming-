import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Hippie extends Moveable {
    private int waiting;
    private String type;
    private int backgroundLimit = 3;
    public Hippie(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int waiting, String type) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.waiting = waiting;
        this.type = type;
    }

    public void execute(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.waiting += 1;
        Optional<Entity> hippieTarget = world.findNearest(this.getPosition(), "House");
        if (this.type.equals("treehugger")) {
            hippieTarget = world.findNearest(this.getPosition(), "Tree");
        }
        if (this.moveTo(world, hippieTarget.get(), scheduler)) {
            if (this.type.equals("treehugger")) {
                ((Growable) (world.getNearest(this.getPosition(), "Tree"))).setHealth(8);
                (world.getNearest(this.getPosition(), "Tree")).setImages(imageStore.getImageList("gtree"));
            }
        } else {
            if (world.getBackgroundCell(this.getPosition()).getId().equals("grass") && this.backgroundLimit >= 0) {
                Random rand = new Random();
                int randInt = rand.nextInt(0, 5);
                String pgrass = "pgrass" + Integer.toString(randInt);
                Background background = new Background(pgrass, imageStore.getImageList(pgrass));
                world.setBackgroundCell(this.getPosition(), background);
                this.backgroundLimit -= 1;
            }
        }
        if (this.type.equals("treehugger") && this.waiting > 20) {
            this.type = "houseprotestor";
            this.waiting = 0;
        } else if ((this.type.equals("houseprotestor") && this.waiting > 26)) {
            this.type = "treehugger";
            this.waiting = 0;
        }
        scheduler.scheduleEvent(this, Activity.createActivityAction(this, world, imageStore), this.getActionPeriod());
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, nextPos, this);
            }
            return false;
        }
    }

    public int getWaiting(){
        return this.waiting;
    }
    public String getType(){
        return this.type;
    }
}
