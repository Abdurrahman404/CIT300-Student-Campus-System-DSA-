package graph;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/** A vertex with its display name and adjacent location keys. */
public final class Location {
    private final String name;
    private final String key;
    // Package access keeps graph mutations inside CampusGraph.
    final Set<String> neighbours = new LinkedHashSet<>();

    public Location(String name) {
        this.name = cleanName(name);
        this.key = this.name.toLowerCase(Locale.ROOT);
    }

    public static String cleanName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Location name cannot be empty.");
        }
        return name.trim().replaceAll("\\s+", " ");
    }

    public static String keyOf(String name) {
        return cleanName(name).toLowerCase(Locale.ROOT);
    }

    public String getName() { return name; }
    public String getKey() { return key; }
}
