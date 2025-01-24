import java.util.Optional;

public interface Book<K , V> extends Iterable<V> {
    boolean insert (K key, V value);
    boolean delete (K key);
    Optional<V> get (K key);
    int size ();
}