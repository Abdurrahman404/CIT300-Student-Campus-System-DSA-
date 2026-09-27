package tree;

import student.Student;

/** One node of the BST. Only the tree package changes its child links. */
final class TreeNode {
    Student student;
    TreeNode left;
    TreeNode right;

    TreeNode(Student student) {
        this.student = student;
    }
}
