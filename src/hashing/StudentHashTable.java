package hashing;

import java.util.Objects;
import student.Student;

/** Custom hash table: an entry array with a linked chain for each bucket. */
public final class StudentHashTable {
    private static final class Entry {
        private final String key;
        private final Student student;
        private Entry next;
        private Entry(String key, Student student, Entry next) {
            this.key = key;
            this.student = student;
            this.next = next;
        }
    }

    private Entry[] buckets;
    private int size;

    public StudentHashTable() { this(31); }

    public StudentHashTable(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be positive.");
        buckets = new Entry[capacity];
    }

    private int index(String normalizedKey) {
        return Math.floorMod(normalizedKey.hashCode(), buckets.length);
    }

    public boolean put(Student student) {
        Objects.requireNonNull(student, "Student cannot be null.");
        String key = student.getStudentId();
        if (get(key) != null) return false;
        if (size + 1 > buckets.length * 0.75) resize();
        int bucket = index(key);
        buckets[bucket] = new Entry(key, student, buckets[bucket]);
        size++;
        return true;
    }

    public Student get(String studentId) {
        String key = Student.normalizeId(studentId);
        for (Entry entry = buckets[index(key)]; entry != null; entry = entry.next) {
            if (entry.key.equals(key)) return entry.student;
        }
        return null;
    }

    public Student remove(String studentId) {
        String key = Student.normalizeId(studentId);
        int bucket = index(key);
        Entry previous = null;
        Entry current = buckets[bucket];
        while (current != null) {
            if (current.key.equals(key)) {
                if (previous == null) buckets[bucket] = current.next;
                else previous.next = current.next;
                size--;
                return current.student;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }

    /** Rehash existing entries when the load factor would exceed 0.75. */
    private void resize() {
        Entry[] oldBuckets = buckets;
        buckets = new Entry[oldBuckets.length * 2 + 1];
        for (Entry head : oldBuckets) {
            Entry current = head;
            while (current != null) {
                Entry next = current.next;
                int bucket = index(current.key);
                current.next = buckets[bucket];
                buckets[bucket] = current;
                current = next;
            }
        }
    }

    public int size() { return size; }
}
