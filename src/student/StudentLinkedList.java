package student;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Custom singly linked list. Records retain their insertion order. */
public final class StudentLinkedList {
    private StudentNode head;
    private StudentNode tail;
    private int size;

    public boolean add(Student student) {
        Objects.requireNonNull(student, "Student cannot be null.");
        if (find(student.getStudentId()) != null) return false;
        StudentNode node = new StudentNode(student);
        if (head == null) head = node;
        else tail.next = node;
        tail = node;
        size++;
        return true;
    }

    public Student find(String studentId) {
        String key = Student.normalizeId(studentId);
        for (StudentNode current = head; current != null; current = current.next) {
            if (current.student.getStudentId().equals(key)) return current.student;
        }
        return null;
    }

    public boolean update(String id, String name, String programme, double marks) {
        Student student = find(id);
        if (student == null) return false;
        student.updateDetails(name, programme, marks);
        return true;
    }

    public Student remove(String studentId) {
        String key = Student.normalizeId(studentId);
        StudentNode previous = null;
        StudentNode current = head;
        while (current != null) {
            if (current.student.getStudentId().equals(key)) {
                if (previous == null) head = current.next;
                else previous.next = current.next;
                if (current == tail) tail = previous;
                size--;
                return current.student;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }

    public List<Student> toList() {
        List<Student> result = new ArrayList<>();
        for (StudentNode current = head; current != null; current = current.next) {
            result.add(current.student);
        }
        return result;
    }

    public int size() { return size; }
    public boolean isEmpty() { return head == null; }
}
