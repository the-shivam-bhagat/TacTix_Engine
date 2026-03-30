import java.io.IOException;
import java.util.List;


/// Abstraction for player persistence.
public interface PlayerStore {

    /// Load all persisted players. Registry handles its own internal structure.
    List<Player> loadAll() throws IOException;

    /// Persist the current player collection.
    void saveAll(Iterable<Player> players);
}