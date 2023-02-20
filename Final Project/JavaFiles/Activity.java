public class Activity implements Action {
    private final Actionable entity;
    private final WorldModel world;
    private final ImageStore imageStore;

    public Activity(Actionable entity, WorldModel world, ImageStore imageStore) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public void executeAction(EventScheduler scheduler) {
        ((Executable)this.entity).execute(this.world, this.imageStore, scheduler);
    }

    public static Action createActivityAction(Actionable entity, WorldModel world, ImageStore imageStore) {
        return new Activity(entity, world, imageStore);
    }

}


