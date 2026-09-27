package student;

/** One link in StudentLinkedList. Only the student package changes links. */
final class StudentNode {
    final Student student;
    StudentNode next;

    StudentNode(Student student) {
        this.student = student;
    }
}
