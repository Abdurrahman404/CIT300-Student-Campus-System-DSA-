package student;

import java.util.Locale;

/** One student object is shared by the linked list, BST and hash table. */
public final class Student {
    private final String studentId;
    private String name;
    private String programme;
    private double marks;

    public Student(String studentId, String name, String programme, double marks) {
        this.studentId = normalizeId(studentId);
        updateDetails(name, programme, marks);
    }

    public static String normalizeId(String id) {
        String value = requireText(id, "Student ID").toUpperCase(Locale.ROOT);
        if (!value.matches("[A-Z0-9][A-Z0-9_-]*")) {
            throw new IllegalArgumentException("Student ID may contain letters, digits, - and _ only.");
        }
        return value;
    }

    public static String requireText(String text, String field) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
        String value = text.trim();
        for (int i = 0; i < value.length(); i++) {
            if (Character.isISOControl(value.charAt(i))) {
                throw new IllegalArgumentException(field + " contains an invalid control character.");
            }
        }
        return value;
    }

    public static boolean validMarks(double marks) {
        return Double.isFinite(marks) && marks >= 0 && marks <= 100;
    }

    /** Validate all values first, so a failed update changes nothing. */
    public void updateDetails(String name, String programme, double marks) {
        String checkedName = requireText(name, "Name");
        String checkedProgramme = requireText(programme, "Programme");
        if (!validMarks(marks)) {
            throw new IllegalArgumentException("Marks must be a finite number from 0 to 100.");
        }
        this.name = checkedName;
        this.programme = checkedProgramme;
        this.marks = marks;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getProgramme() { return programme; }
    public double getMarks() { return marks; }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%-14s | %-22s | %-24s | %6.2f",
                studentId, name, programme, marks);
    }
}
