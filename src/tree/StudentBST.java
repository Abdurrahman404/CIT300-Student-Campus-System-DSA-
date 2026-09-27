package tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import student.Student;

/** Binary search tree ordered lexicographically by normalized student ID. */
public final class StudentBST {
    private TreeNode root;

    public boolean insert(Student student) {
        Objects.requireNonNull(student, "Student cannot be null.");
        if (root == null) { root = new TreeNode(student); return true; }
        TreeNode current = root;
        while (true) {
            int comparison = student.getStudentId().compareTo(current.student.getStudentId());
            if (comparison == 0) return false;
            if (comparison < 0) {
                if (current.left == null) { current.left = new TreeNode(student); return true; }
                current = current.left;
            } else {
                if (current.right == null) { current.right = new TreeNode(student); return true; }
                current = current.right;
            }
        }
    }

    public Student search(String studentId) {
        String key = Student.normalizeId(studentId);
        TreeNode current = root;
        while (current != null) {
            int comparison = key.compareTo(current.student.getStudentId());
            if (comparison == 0) return current.student;
            current = comparison < 0 ? current.left : current.right;
        }
        return null;
    }

    public boolean delete(String studentId) {
        String key = Student.normalizeId(studentId);
        if (search(key) == null) return false;
        root = delete(root, key);
        return true;
    }

    private TreeNode delete(TreeNode node, String key) {
        if (node == null) return null;
        int comparison = key.compareTo(node.student.getStudentId());
        if (comparison < 0) node.left = delete(node.left, key);
        else if (comparison > 0) node.right = delete(node.right, key);
        else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            // Two children: replace the record with the in-order successor.
            TreeNode successor = node.right;
            while (successor.left != null) successor = successor.left;
            node.student = successor.student;
            node.right = delete(node.right, successor.student.getStudentId());
        }
        return node;
    }

    public List<Student> inOrder() {
        List<Student> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(TreeNode node, List<Student> result) {
        if (node == null) return;
        inOrder(node.left, result);
        result.add(node.student);
        inOrder(node.right, result);
    }
}
