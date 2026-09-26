package graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Undirected graph represented as a map of vertices and adjacency sets. */
public final class CampusGraph {
    private final Map<String, Location> locations = new LinkedHashMap<>();

    public boolean addLocation(String name) {
        Location location = new Location(name);
        if (locations.containsKey(location.getKey())) return false;
        locations.put(location.getKey(), location);
        return true;
    }

    public boolean removeLocation(String name) {
        String key = Location.keyOf(name);
        if (locations.remove(key) == null) return false;
        for (Location location : locations.values()) location.neighbours.remove(key);
        return true;
    }

    public boolean addConnection(String first, String second) {
        String a = Location.keyOf(first);
        String b = Location.keyOf(second);
        if (!locations.containsKey(a) || !locations.containsKey(b) || a.equals(b)) return false;
        boolean changed = locations.get(a).neighbours.add(b);
        locations.get(b).neighbours.add(a);
        return changed;
    }

    public boolean removeConnection(String first, String second) {
        String a = Location.keyOf(first);
        String b = Location.keyOf(second);
        if (!locations.containsKey(a) || !locations.containsKey(b)) return false;
        boolean changed = locations.get(a).neighbours.remove(b);
        locations.get(b).neighbours.remove(a);
        return changed;
    }

    /** Return a copy so the caller cannot change internal edges. */
    public Map<String, List<String>> snapshot() {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (Location location : locations.values()) {
            List<String> names = new ArrayList<>();
            for (String neighbour : location.neighbours) names.add(locations.get(neighbour).getName());
            result.put(location.getName(), names);
        }
        return result;
    }

    public List<String> bfs(String start) {
        String key = Location.keyOf(start);
        List<String> order = new ArrayList<>();
        if (!locations.containsKey(key)) return order;
        Set<String> visited = new LinkedHashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        visited.add(key);
        queue.addLast(key);
        while (!queue.isEmpty()) {
            String current = queue.removeFirst();
            order.add(locations.get(current).getName());
            for (String neighbour : locations.get(current).neighbours) {
                if (visited.add(neighbour)) queue.addLast(neighbour);
            }
        }
        return order;
    }

    public List<String> dfs(String start) {
        String key = Location.keyOf(start);
        List<String> order = new ArrayList<>();
        if (locations.containsKey(key)) dfs(key, new LinkedHashSet<>(), order);
        return order;
    }

    private void dfs(String key, Set<String> visited, List<String> order) {
        visited.add(key);
        order.add(locations.get(key).getName());
        for (String neighbour : locations.get(key).neighbours) {
            if (!visited.contains(neighbour)) dfs(neighbour, visited, order);
        }
    }

    public boolean containsLocation(String name) { return locations.containsKey(Location.keyOf(name)); }
    public int size() { return locations.size(); }
}
