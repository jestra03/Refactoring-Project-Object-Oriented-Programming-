import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy implements PathingStrategy {
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        List<Point> path = new ArrayList<>();
        PathNode startNode = new PathNode(start, end, 0);
        HashSet<Point> closedList = new HashSet<>();

        Comparator<PathNode> nodeComparator = Comparator.comparingInt(n -> n.getTotalDistance());
        PriorityQueue<PathNode> openList = new PriorityQueue<>( 100,  nodeComparator);
        openList.add(startNode);

        while (!openList.isEmpty()){
            PathNode currentNode = openList.remove();

            if (withinReach.test(currentNode.getPosition(), end)){
                while (currentNode.getPreviousNode() != null ){
                    path.add(currentNode.getPosition());
                    currentNode = currentNode.getPreviousNode();
                }
                Collections.reverse(path);
                return path;
            }

            List<Point> neighbors = potentialNeighbors.apply(currentNode.getPosition())
                    .filter(canPassThrough)
                    .filter(pt -> !pt.equals(start) && !closedList.contains(pt))
                    .collect(Collectors.toList());
            for (Point p : neighbors){
                PathNode neighbor = new PathNode(p, end, currentNode.getDistanceFromStart() + 1);
                neighbor.setPreviousNode(currentNode);

                boolean alreadyIn = false;

                for(PathNode cur : openList){
                    if (p.equals(cur.getPosition()) && neighbor.getDistanceFromStart() < cur.getDistanceFromStart()){
                        cur.setDistanceFromStart(neighbor.getDistanceFromStart());
                        cur.setPreviousNode(neighbor.getPreviousNode());
                    }
                    if (cur.getPosition().equals(neighbor.getPosition())){
                        alreadyIn = true;
                    }
                }
                if(alreadyIn == false){
                    openList.add(neighbor);
                }
            }
            closedList.add(currentNode.getPosition());
        }
        return path;
    }
}
