import java.util.*;

import processing.core.PImage;

import javax.swing.text.Position;

/**
 * An entity that exists in the world.
 */
public class Entity {
    private final String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public Entity(String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }
    // Helper method for testing.

    public PImage getCurrentImage() {
        return this.images.get(this.imageIndex % this.images.size());
    }

    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }

    // added accessor method
    public String getId() {
        return this.id;
    }
    public Point getPosition() {return this.position;}
    public void setPosition(Point point) {this.position = point;}
    public void setImages(List<PImage> imageList) {
        this.images = imageList;
    }
    public List<PImage> getImages() {return this.images;}
}
