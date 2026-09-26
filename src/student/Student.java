package student;

import java.util.Locale;
import java.util.Objects;

public interface Student {
    String getStudentId();

    static String normalizeId(String studentId) {
        return Objects.requireNonNull(studentId, "Student ID cannot be null.")
                .trim()
                .toUpperCase(Locale.ROOT);
    }
}