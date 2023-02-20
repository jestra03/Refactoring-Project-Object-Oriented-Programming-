import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

import processing.core.*;

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;

    private final int VIEW_WIDTH = 640;
    private final int VIEW_HEIGHT = 480;
    private final int TILE_WIDTH = 32;
    private final int TILE_HEIGHT = 32;

    private final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    private final String IMAGE_LIST_FILE_NAME = "imagelist";
    private final String DEFAULT_IMAGE_NAME = "background_default";
    private final int DEFAULT_IMAGE_COLOR = 0x808080;

    private final String FAST_FLAG = "-fast";
    private final String FASTER_FLAG = "-faster";
    private final String FASTEST_FLAG = "-fastest";
    private final double FAST_SCALE = 0.5;
    private final double FASTER_SCALE = 0.25;
    private final double FASTEST_SCALE = 0.10;

    private String loadFile = "world.sav";
    private long startTimeMillis = 0;
    private double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(world, scheduler, imageStore);
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = (appTime - scheduler.getCurrentTime())/timeScale;
        this.update(frameTime);
        view.drawViewport();
    }

    public void update(double frameTime){
        scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() {
        Point pressed = mouseToPoint();
        System.out.println("CLICK! " + pressed.x + ", " + pressed.y);

        Optional<Entity> entityOptional = world.getOccupant(pressed);
        if (entityOptional.isPresent()) {
            Entity entity = entityOptional.get();
            int health = 0;
            if (entity instanceof Growable) {
                health = ((Growable)entity).getHealth();
                System.out.println(entity.getId() + ": " + entity.getClass() + " : " + health);
            }
            else if (entity instanceof Hippie) {
                System.out.println(entity.getId() + ": " + entity.getClass() + " : " + ((Hippie)entity).getType() + " " + ((Hippie)entity).getWaiting());
            }
            else {
                System.out.println(entity.getId() + ": " + entity.getClass() + " : " + health);
            }
        }
        else {
            Point cpos = new Point(pressed.x, pressed.y);
            Point[] points = {new Point(pressed.x+1, pressed.y), new Point(pressed.x-1, pressed.y), new Point(pressed.x, pressed.y+1), new Point(pressed.x, pressed.y-1)};
            Random rand = new Random();

            Background background = new Background("peacegrass", imageStore.getImageList("peacegrass"));
            world.setBackgroundCell(cpos, background);
            Entity closestDude = world.getNearest(cpos, "DudeFull", "DudeNotFull");
            closestDude.setImages(imageStore.getImageList("lumberjack"));
            ((Dude)closestDude).setLumberJack();

            for (int i = 0;i < 2;i++) {
                int randInt = rand.nextInt(0,4);
                Point point = points[randInt] ;
                if (world.withinBounds(point) && !world.isOccupied(point)) {
                    Hippie hippie = new Hippie(WorldModel.HIPPIE_KEY, point, this.imageStore.getImageList("hippie"), .5, .2, 0, "treehugger");
                    world.addEntity(hippie);
                    hippie.scheduleActions(scheduler, world, imageStore);
                }
            }

            /* OPTIONAL CODE: CONVERT GRASS BLOCKS AROUND MOUSE CLICK TO VARIOUS PEACE & HIPPIE SYMBOL BLOCKS
            Point[] neighborP = {new Point(pressed.x+1, pressed.y), new Point(pressed.x-1, pressed.y), new Point(pressed.x, pressed.y+1),
            new Point(pressed.x, pressed.y-1), new Point(pressed.x-1, pressed.y-1),new Point(pressed.x-1, pressed.y+1),
            new Point(pressed.x+1, pressed.y-1),new Point(pressed.x+1, pressed.y+1)};
            Point targetPos = closestDude.get().getPosition();
            world.removeEntityAt(targetPos);
            DudeNotFull lumberjack = new DudeNotFull("dude", targetPos, imageStore.getImageList("lumberjack"), 0, 1, 4, 0);
            world.addEntity(lumberjack);
            lumberjack.scheduleActions(scheduler, world, imageStore);
            */

            /*
            for (Point point:neighborP) {
                if (world.withinBounds(point) && !world.isOccupied(point) && world.getBackgroundCell(point).getId().equals("grass")) {
                    int randInt = rand.nextInt(0, 5);
                    String pgrass = "pgrass" + Integer.toString(randInt);
                    background = new Background(pgrass, imageStore.getImageList(pgrass));
                    world.setBackgroundCell(point, background);
                }
            }
            */
        }

    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            view.shiftView(dx, dy);
            // Functions.shiftView(view, dx, dy);
        }
    }

    public Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME)); //changed
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    public void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            world.load(in, imageStore, createDefaultBackground(imageStore));
            // Functions.load(world, in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            world.load(in, imageStore, createDefaultBackground(imageStore));
            // Functions.load(world, in, imageStore, createDefaultBackground(imageStore));
        }
    }
    public void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in,imageStore, this);
            // ImageStore.loadImages(in, imageStore,this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Actionable) {
                ((Actionable) entity).scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    private Point mouseToPoint() {
        return view.getViewport().viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
        // WorldView.viewportToWorld(view.viewport, mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }

}

