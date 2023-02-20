import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    private int numRows;
    private int numCols;
    private Background[][] background;
    private Entity[][] occupancy;
    private Set<Entity> entities;

    public static final String TREE_KEY = "tree";
    public static final String STUMP_KEY = "stump";
    public static final String SAPLING_KEY = "sapling";
    private final String HOUSE_KEY = "house";
    private final String FAIRY_KEY = "fairy";
    private final int PROPERTY_KEY = 0;
    private final String OBSTACLE_KEY = "obstacle";
    private final String DUDE_KEY = "dude";
    public static final String HIPPIE_KEY = "hippie";
    private final int HIPPIE_NUM_PROPERTIES = 2;
    private final int HIPPIE_ANIMATION_PERIOD = 0;
    private final int HIPPIE_ACTION_PERIOD = 1;

    private final int STUMP_NUM_PROPERTIES = 0;
    private final int ENTITY_NUM_PROPERTIES = 4;
    private final int SAPLING_NUM_PROPERTIES = 1;
    private final int OBSTACLE_NUM_PROPERTIES = 1;
    private final int DUDE_NUM_PROPERTIES = 3;
    private final int HOUSE_NUM_PROPERTIES = 0;

    private final int FAIRY_NUM_PROPERTIES = 2;
    private final int TREE_NUM_PROPERTIES = 3;
    private final int SAPLING_HEALTH = 0;
    private final int DUDE_ACTION_PERIOD = 0;
    private final int DUDE_ANIMATION_PERIOD = 1;
    private final int DUDE_LIMIT = 2;

    private final int TREE_ANIMATION_PERIOD = 0;
    private final int TREE_ACTION_PERIOD = 1;
    private final int TREE_HEALTH = 2;
    private final int FAIRY_ANIMATION_PERIOD = 0;
    private final int FAIRY_ACTION_PERIOD = 1;

    private final int PROPERTY_ID = 1;
    private final int PROPERTY_COL = 2;
    private final int PROPERTY_ROW = 3;

    private final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    private final int SAPLING_HEALTH_LIMIT = 5;

    private final int OBSTACLE_ANIMATION_PERIOD = 0;

    public WorldModel() {

    }

    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log() {
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if (log != null) list.add(log);
        }
        return list;
    }

    public void parseBackgroundRow(String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if (row < this.numRows) {
            int rows = Math.min(cells.length, this.numCols);
            for (int col = 0; col < rows; col++) {
                this.background[row][col] = new Background(cells[col], imageStore.getImageList(cells[col]));
                // world.background[row][col] = new Background(cells[col], Functions.getImageList(imageStore, cells[col]));
            }
        }
    }

    public void parseSapling(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == SAPLING_NUM_PROPERTIES) {
            int health = Integer.parseInt(properties[SAPLING_HEALTH]);
            Entity entity = createSapling(id, pt, imageStore.getImageList(SAPLING_KEY)); // changed
            this.tryAddEntity(entity);
            // Functions.tryAddEntity(this, entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", SAPLING_KEY, SAPLING_NUM_PROPERTIES));
        }
    }

    public void parseDude(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Entity entity = createDudeNotFull(id, pt, Double.parseDouble(properties[DUDE_ACTION_PERIOD]), Double.parseDouble(properties[DUDE_ANIMATION_PERIOD]), Integer.parseInt(properties[DUDE_LIMIT]), imageStore.getImageList(DUDE_KEY));
            // Entity entity = createDudeNotFull(id, pt, Double.parseDouble(properties[DUDE_ACTION_PERIOD]), Double.parseDouble(properties[DUDE_ANIMATION_PERIOD]), Integer.parseInt(properties[DUDE_LIMIT]), Functions.getImageList(imageStore, DUDE_KEY));
            this.tryAddEntity(entity);
            // Functions.tryAddEntity(this, entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", DUDE_KEY, DUDE_NUM_PROPERTIES));
        }
    }

    public void parseFairy(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == FAIRY_NUM_PROPERTIES) {
            Entity entity = createFairy(id, pt, Double.parseDouble(properties[FAIRY_ACTION_PERIOD]), Double.parseDouble(properties[FAIRY_ANIMATION_PERIOD]), imageStore.getImageList(FAIRY_KEY));
            // Entity entity = createFairy(id, pt, Double.parseDouble(properties[FAIRY_ACTION_PERIOD]), Double.parseDouble(properties[FAIRY_ANIMATION_PERIOD]), Functions.getImageList(imageStore, FAIRY_KEY));
            this.tryAddEntity(entity);
            // Functions.tryAddEntity(this, entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", FAIRY_KEY, FAIRY_NUM_PROPERTIES));
        }
    }

    public void parseTree(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == TREE_NUM_PROPERTIES) {
            Entity entity = createTree(id, pt, Double.parseDouble(properties[TREE_ACTION_PERIOD]), Double.parseDouble(properties[TREE_ANIMATION_PERIOD]), Integer.parseInt(properties[TREE_HEALTH]), imageStore.getImageList(TREE_KEY));
            // Entity entity = createTree(id, pt, Double.parseDouble(properties[TREE_ACTION_PERIOD]), Double.parseDouble(properties[TREE_ANIMATION_PERIOD]), Integer.parseInt(properties[TREE_HEALTH]), Functions.getImageList(imageStore, TREE_KEY));
            this.tryAddEntity(entity);
            // Functions.tryAddEntity(this, entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", TREE_KEY, TREE_NUM_PROPERTIES));
        }
    }

    public void parseObstacle(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Entity entity = createObstacle(id, pt, Double.parseDouble(properties[OBSTACLE_ANIMATION_PERIOD]), imageStore.getImageList(OBSTACLE_KEY));
            // Entity entity = createObstacle(id, pt, Double.parseDouble(properties[OBSTACLE_ANIMATION_PERIOD]), Functions.getImageList(imageStore, OBSTACLE_KEY));
            this.tryAddEntity(entity);
            // Functions.tryAddEntity(this, entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", OBSTACLE_KEY, OBSTACLE_NUM_PROPERTIES));
        }
    }

    public void parseHouse(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == HOUSE_NUM_PROPERTIES) {
            Entity entity = createHouse(id, pt, imageStore.getImageList(HOUSE_KEY));
            // Entity entity = createHouse(id, pt, Functions.getImageList(imageStore, HOUSE_KEY));
            this.tryAddEntity(entity);
            // Functions.tryAddEntity(this, entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", HOUSE_KEY, HOUSE_NUM_PROPERTIES));
        }
    }

    public void parseStump(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == STUMP_NUM_PROPERTIES) {
            Entity entity = createStump(id, pt, imageStore.getImageList(STUMP_KEY));
            // Entity entity = createStump(id, pt, Functions.getImageList(imageStore, STUMP_KEY));
            this.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", STUMP_KEY, STUMP_NUM_PROPERTIES));
        }
    }

    public void tryAddEntity(Entity entity) {
        if (this.isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(entity);
    }

    public boolean withinBounds(Point pos) {
        return pos.y >= 0 && pos.y < this.numRows && pos.x >= 0 && pos.x < this.numCols;
    }

    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }



    // There is no setBackground() in given InitialForestProject Files, only in given UML
    public Optional<Entity> getOccupant(Point pos) {
        if (this.isOccupied(pos)) {
            return Optional.of(this.getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    public Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.y][pos.x];
    }

    /*
   Assumes that there is no entity currently occupying the
   intended destination cell.
*/
    public void setOccupancyCell(Point pos, Entity entity) {
        this.occupancy[pos.y][pos.x] = entity;
    }

    public Background getBackgroundCell(Point pos) {
        return this.background[pos.y][pos.x];
    }

    public void setBackgroundCell(Point pos, Background background) {
        this.background[pos.y][pos.x] = background;
    }

    public Entity createHouse(String id, Point position, List<PImage> images) {
        return new House(id, position, images);
    }

    public Entity createObstacle(String id, Point position, double animationPeriod, List<PImage> images) {
        return new Obstacle(id, position, images, animationPeriod);
    }

    public Entity createTree(String id, Point position, double actionPeriod, double animationPeriod, int health, List<PImage> images) {
        return new Tree(id, position, images, actionPeriod, animationPeriod, health);
    }

    public Entity createStump(String id, Point position, List<PImage> images) {
        return new Stump(id, position, images);
    }

    // health starts at 0 and builds up until ready to convert to Tree
    public Entity createSapling(String id, Point position, List<PImage> images) {
        return new Sapling(id, position, images, SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, SAPLING_HEALTH_LIMIT);
        // return new Entity(EntityKind.SAPLING, id, position, images, 0,
        // 0, SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, SAPLING_HEALTH_LIMIT);
    }

    public Entity createFairy(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Fairy(id, position, images, actionPeriod, animationPeriod);
    }

    // need resource count, though it always starts at 0
    public Entity createDudeNotFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new DudeNotFull(id, position, images, actionPeriod, animationPeriod, resourceLimit, 0);
    }

    // don't technically need resource count ... full
    public Entity createDudeFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new DudeFull(id, position, images, actionPeriod, animationPeriod, resourceLimit);
    }

    public void parseSaveFile(Scanner saveFile, ImageStore imageStore, Background defaultBackground) {
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while (saveFile.hasNextLine()) {
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if (line.endsWith(":")) {
                headerLine = lineCounter;
                lastHeader = line;
                switch (line) {
                    case "Backgrounds:" -> this.background = new Background[this.numRows][this.numCols];
                    case "Entities:" -> {
                        this.occupancy = new Entity[this.numRows][this.numCols];
                        this.entities = new HashSet<>();
                    }
                }
            } else {
                switch (lastHeader) {
                    case "Rows:" -> this.numRows = Integer.parseInt(line);
                    case "Cols:" -> this.numCols = Integer.parseInt(line);
                    case "Backgrounds:" -> this.parseBackgroundRow(line, lineCounter - headerLine - 1, imageStore);
                    case "Entities:" -> this.parseEntity(line, imageStore);
                }
            }
        }
    }

    public void parseEntity(String line, ImageStore imageStore) {
        String[] properties = line.split(" ", ENTITY_NUM_PROPERTIES + 1);
        if (properties.length >= ENTITY_NUM_PROPERTIES) {
            String key = properties[PROPERTY_KEY];
            String id = properties[PROPERTY_ID];
            Point pt = new Point(Integer.parseInt(properties[PROPERTY_COL]), Integer.parseInt(properties[PROPERTY_ROW]));

            properties = properties.length == ENTITY_NUM_PROPERTIES ?
                    new String[0] : properties[ENTITY_NUM_PROPERTIES].split(" ");

            switch (key) {
                case OBSTACLE_KEY -> this.parseObstacle(properties, pt, id, imageStore);
                case DUDE_KEY -> this.parseDude(properties, pt, id, imageStore);
                case FAIRY_KEY -> this.parseFairy(properties, pt, id, imageStore);
                case HOUSE_KEY -> this.parseHouse(properties, pt, id, imageStore);
                case TREE_KEY -> this.parseTree(properties, pt, id, imageStore);
                case SAPLING_KEY -> this.parseSapling(properties, pt, id, imageStore);
                case STUMP_KEY -> this.parseStump(properties, pt, id, imageStore);
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        } else {
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }



    // processLine is in the given UML but not in the given InitialForestProject

    public void load(Scanner saveFile, ImageStore imageStore, Background defaultBackground) {
        parseSaveFile(saveFile, imageStore, defaultBackground);
        if (this.background == null) {
            this.background = new Background[this.numRows][this.numCols];
            for (Background[] row : this.background)
                Arrays.fill(row, defaultBackground);
        }
        if (this.occupancy == null) {
            this.occupancy = new Entity[this.numRows][this.numCols];
            this.entities = new HashSet<>();
        }
    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getNumCol() {
        return this.numCols;
    }

    public Set<Entity> getEntities() {
        return this.entities;
    }

    public void addEntity(Entity entity) {
        if (this.withinBounds(entity.getPosition())) {
            this.setOccupancyCell(entity.getPosition(), entity);
            this.getEntities().add(entity);
        }
    }

    public void moveEntity(EventScheduler scheduler, Point pos, Entity entity) {
        Point oldPos = entity.getPosition();
        if (this.withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            Optional<Entity> occupant = this.getOccupant(pos);
            occupant.ifPresent(target -> this.removeEntity(scheduler, target));
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    public void removeEntity(EventScheduler scheduler, Entity entity) {
        scheduler.unscheduleAllEvents(entity);
        this.removeEntityAt(entity.getPosition());
    }

    public void removeEntityAt(Point pos) {
        if (this.withinBounds(pos) && this.getOccupancyCell(pos) != null) {
            Entity entity = this.getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.getEntities().remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }

    // helper method
    public Optional<Entity> nearestEntity(List<Entity> entities, Point pos) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = nearest.getPosition().distanceSquared(pos);

            for (Entity other : entities) {
                int otherDistance = other.getPosition().distanceSquared(pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    public Entity getNearestEntity(List<Entity> entities, Point pos) {
        if (entities.isEmpty()) {
            return null;
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = nearest.getPosition().distanceSquared(pos);

            for (Entity other : entities) {
                int otherDistance = other.getPosition().distanceSquared(pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return nearest;
        }
    }

    // used by Moveable subclasses to find target entity
    public Optional<Entity> findNearest(Point pos, String entityKind) {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : this.getEntities()) {
            if (entity.getClass().getName().equals(entityKind)) {
                ofType.add(entity);
            }
        }
        return nearestEntity(ofType, pos);
    }

    public Entity getNearest(Point pos, String entityKind) {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : this.getEntities()) {
            if (entity.getClass().getName().equals(entityKind)) {
                ofType.add(entity);
            }
        }
        return getNearestEntity(ofType, pos);
    }

    public Entity getNearest(Point pos, String entityKind1, String entityKind2) {
        List<Entity> ofType = new LinkedList<>();

        for (Entity entity : this.getEntities()) {
            if (entity.getClass().getName().equals(entityKind1)) {
                ofType.add(entity);
            }
            else if (entity.getClass().getName().equals(entityKind2)) {
                ofType.add(entity);
            }
        }
        return getNearestEntity(ofType, pos);
    }

    // overloading (searching for two entities)
    public Optional<Entity> findNearest(Point pos, String entityKind1, String entityKind2) {
        List<Entity> ofType = new LinkedList<>();

        for (Entity entity : this.getEntities()) {
            if (entity.getClass().getName().equals(entityKind1)) {
                ofType.add(entity);
            }
            else if (entity.getClass().getName().equals(entityKind2)) {
                ofType.add(entity);
            }
        }

        return nearestEntity(ofType, pos);
    }


}
