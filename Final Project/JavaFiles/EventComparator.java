import java.util.Comparator;

public final class EventComparator implements Comparator<Event> {
    public int compare(Event lft, Event rht) {
        return (int) (1000 * (lft.getTime() - rht.getTime()));
    }
}
