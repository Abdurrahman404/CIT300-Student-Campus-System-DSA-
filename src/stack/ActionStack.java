package stack;

import java.util.ArrayList;
import java.util.List;

/** Custom linked stack: newest action is always at the top (LIFO). */
public class ActionStack {
    private static class Node {
        private final String action;
        private final Node next;
        private Node(String action, Node next) { this.action = action; this.next = next; }
    }

    private Node top;
    private int size;

    public void push(String action) {
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("Action cannot be empty.");
        }
        top = new Node(action, top);
        size++;
    }

    public String pop() {
        if (top == null) return null;
        String value = top.action;
        top = top.next;
        size--;
        return value;
    }

    public List<String> recentActions() {
        List<String> result = new ArrayList<>();
        for (Node current = top; current != null; current = current.next) result.add(current.action);
        return result;
    }

    public int size() { return size; }
    public boolean isEmpty() { return top == null; }
}
