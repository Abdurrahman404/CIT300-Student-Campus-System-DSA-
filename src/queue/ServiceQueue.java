package queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import student.Student;

/** Custom linked queue. The oldest request is processed first (FIFO). */
public final class ServiceQueue {
    /** Nested here to keep the requested one-file queue folder. */
    public static final class Request {
        private final int requestNumber;
        private final String studentId;
        private final String description;

        public Request(int requestNumber, String studentId, String description) {
            if (requestNumber < 1) throw new IllegalArgumentException("Request number must be positive.");
            this.requestNumber = requestNumber;
            this.studentId = Student.normalizeId(studentId);
            this.description = Student.requireText(description, "Request description");
        }

        public int getRequestNumber() { return requestNumber; }
        public String getStudentId() { return studentId; }
        public String getDescription() { return description; }

        @Override
        public String toString() {
            return "Request #" + requestNumber + " | " + studentId + " | " + description;
        }
    }

    private static final class Node {
        private final Request request;
        private Node next;
        private Node(Request request) { this.request = request; }
    }

    private Node front;
    private Node rear;
    private int size;

    public void enqueue(Request request) {
        Node node = new Node(Objects.requireNonNull(request, "Request cannot be null."));
        if (rear == null) front = node;
        else rear.next = node;
        rear = node;
        size++;
    }

    public Request dequeue() {
        if (front == null) return null;
        Request request = front.request;
        front = front.next;
        if (front == null) rear = null;
        size--;
        return request;
    }

    public boolean hasPendingFor(String studentId) {
        String key = Student.normalizeId(studentId);
        for (Node current = front; current != null; current = current.next) {
            if (current.request.getStudentId().equals(key)) return true;
        }
        return false;
    }

    public List<Request> toList() {
        List<Request> result = new ArrayList<>();
        for (Node current = front; current != null; current = current.next) result.add(current.request);
        return result;
    }

    public int size() { return size; }
    public boolean isEmpty() { return front == null; }
}
