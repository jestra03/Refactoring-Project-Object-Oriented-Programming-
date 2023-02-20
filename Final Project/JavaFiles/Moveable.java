import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class Moveable extends Executable{
    public Moveable(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    // moveTo is implemented differently for Dude_Full, Dude_NotFull, Fairy
    public abstract boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);
    // nextPosition is shared
    public Point nextPosition(WorldModel world, Point destPos){
        Predicate<Point> canPassThroughDude = point -> world.withinBounds(point) && (!world.isOccupied(point) || world.getOccupancyCell(point) instanceof Stump);
        BiPredicate<Point, Point> withinReach = (p1, p2) -> p1.adjacent(p2);
        PathingStrategy pathing = new AStarPathingStrategy();
        List<Point> path;
        path = pathing.computePath(this.getPosition(), destPos, canPassThroughDude, withinReach ,PathingStrategy.CARDINAL_NEIGHBORS);

        if (path.size() == 0){
            return this.getPosition();
        }
        else{
            return path.get(0);
        }
    }
}
