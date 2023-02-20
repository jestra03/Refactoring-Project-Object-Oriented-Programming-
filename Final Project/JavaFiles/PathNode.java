public class PathNode {

    private Point position;
    private int hueristicDistance;
    private int distanceFromStart;
    private int totalDistance;
    private PathNode previousNode;
    private Point goal;

    public PathNode(Point position, Point goal, int distanceFromStart){
        this.position = position;
        this.goal = goal;
        this.hueristicDistance = computeHueristic();
        this.distanceFromStart = distanceFromStart;
        this.totalDistance = this.hueristicDistance + this.distanceFromStart;
    }

    public int computeHueristic(){
        int xDistance = Math.abs(this.goal.x - this.position.x);
        int yDistance = Math.abs(this.goal.y - this.position.y);
        return xDistance + yDistance;
    }

    public boolean equals(Object other){
        if (other == null){
            return false;
        }
        if (this == other){
            return true;
        }
        if(other.getClass() != this.getClass()){
            return false;
        }
        PathNode otherNode = (PathNode) other;

        return this.position.equals(otherNode.position);
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + position.x;
        result = result * 31 + position.y;
        return result;
    }

    public int getDistanceFromStart() {
        return distanceFromStart;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setDistanceFromStart(int distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
    }


    public Point getPosition(){return this.position;}

    public PathNode getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(PathNode lastNode) {
        this.previousNode = lastNode;
    }

}
