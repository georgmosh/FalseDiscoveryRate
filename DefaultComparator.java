import java.util.Comparator;

/**
 * DefaultComparator.java
 * @authors Georgios M. Moschovis (geomos@kth.se)
 */
final class DefaultComparator implements Comparator {
    public int compare(Object a, Object b) {
        return ((Comparable)a).compareTo(b);
    }
}