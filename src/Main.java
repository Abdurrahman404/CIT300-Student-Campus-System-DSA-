import graph.CampusGraph;
import graph.Location;
import hashing.StudentHashTable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import queue.ServiceQueue;
import stack.ActionStack;
import student.Student;
import student.StudentLinkedList;
import tree.StudentBST;

/** Console menu and integration  */
public final class Main {
    private final StudentLinkedList students = new StudentLinkedList();
    private final StudentBST tree = new StudentBST();
    private final StudentHashTable hashTable = new StudentHashTable();
    private final ActionStack actions = new ActionStack();
    private final ServiceQueue serviceQueue = new ServiceQueue();
    private final CampusGraph campus = new CampusGraph();
    private final Scanner input = new Scanner(System.in);
    private int nextRequestNumber = 1;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        seedCampus();
        System.out.println("UNIVERSITY STUDENT RECORD AND CAMPUS ROUTE MANAGEMENT SYSTEM");
        System.out.println("CIT300 | Java console application");
        System.out.println("Four sample campus locations are loaded. Student records start empty.");
        System.out.println("Data is kept for this session and resets when the application closes.");
        boolean running = true;
        try {
            while (running) {
                printMenu();
                int choice = readInt("Select an option: ", 1, 19);
                System.out.println();
                try {
                    switch (choice) {
                        case 1: addStudent(); break;
                        case 2: updateStudent(); break;
                        case 3: deleteStudent(); break;
                        case 4: printStudents(students.toList(), "LINKED LIST - INSERTION ORDER"); break;
                        case 5: addServiceRequest(); break;
                        case 6: processServiceRequest(); break;
                        case 7: displayPendingRequests(); break;
                        case 8: displayActions(); break;
                        case 9: printStudents(tree.inOrder(), "BST - STUDENT ID ORDER"); break;
                        case 10: searchStudent(); break;
                        case 11: addLocation(); break;
                        case 12: removeLocation(); break;
                        case 13: changeConnection(true); break;
                        case 14: changeConnection(false); break;
                        case 15: displayCampus(); break;
                        case 16: traverse(true); break;
                        case 17: traverse(false); break;
                        case 18: showHelp(); break;
                        case 19: running = !readYesNo("Exit and discard this session's data?"); break;
                        default: throw new IllegalStateException("Unexpected menu option.");
                    }
                } catch (IllegalArgumentException exception) {
                    error(exception.getMessage());
                }
                if (running) readLine("\nPress Enter to continue...");
            }
            System.out.println("Thank you. Application closed safely.");
        } catch (NoSuchElementException exception) {
            System.out.println("\nInput closed. Session ended.");
        } finally {
            input.close();
        }
    }

    private void printMenu() {
        System.out.println("\n=========================== MAIN MENU ===========================");
        System.out.println(" 1. Add student                 11. Add campus location");
        System.out.println(" 2. Update student              12. Remove campus location");
        System.out.println(" 3. Delete student              13. Add connection / road");
        System.out.println(" 4. Display linked list         14. Remove connection / road");
        System.out.println(" 5. Add service request         15. Display campus connections");
        System.out.println(" 6. Process next request        16. Traverse campus with BFS");
        System.out.println(" 7. Display pending requests    17. Traverse campus with DFS");
        System.out.println(" 8. Display action stack        18. Help / data structures");
        System.out.println(" 9. Display BST                 19. Exit");
        System.out.println("10. Search using hashing");
        System.out.println("=================================================================");
    }

    private void addStudent() {
        String id = readStudentId("Student ID: ");
        if (hashTable.get(id) != null) { error("A student with ID " + id + " already exists."); return; }
        String name = readRequired("Name: ");
        String programme = readRequired("Programme: ");
        double marks = readMarks("Marks (0-100): ");
        Student student = new Student(id, name, programme, marks);
        // All three structures refer to the same Student object.
        students.add(student);
        tree.insert(student);
        hashTable.put(student);
        record("Added student " + id);
        success("Student record added successfully.");
    }

    private void updateStudent() {
        String id = readStudentId("Student ID to update: ");
        Student student = students.find(id);
        if (student == null) { error("Student record not found."); return; }
        System.out.println("Current: " + student);
        String name = readRequired("New name: ");
        String programme = readRequired("New programme: ");
        double marks = readMarks("New marks (0-100): ");
        // The immutable student ID keeps the BST and hash index valid.
        students.update(id, name, programme, marks);
        record("Updated student " + id);
        success("Student updated in the linked list, BST and hash table.");
    }

    private void deleteStudent() {
        String id = readStudentId("Student ID to delete: ");
        Student student = hashTable.get(id);
        if (student == null) { error("Student record not found."); return; }
        if (serviceQueue.hasPendingFor(id)) {
            error("Process this student's pending service requests before deleting.");
            return;
        }
        System.out.println(student);
        if (!readYesNo("Delete this student?")) { System.out.println("Deletion cancelled."); return; }
        students.remove(id);
        tree.delete(id);
        hashTable.remove(id);
        record("Deleted student " + id + " (" + student.getName() + ")");
        success("Student deleted from the linked list, BST and hash table.");
    }

    private void searchStudent() {
        String id = readStudentId("Student ID to search: ");
        Student student = hashTable.get(id);
        if (student == null) { error("Student record not found."); return; }
        System.out.println("HASH TABLE SEARCH RESULT");
        printStudentHeader();
        System.out.println(student);
    }

    private void printStudents(List<Student> records, String title) {
        System.out.println(title);
        if (records.isEmpty()) { System.out.println("No student records available."); return; }
        printStudentHeader();
        for (Student student : records) System.out.println(student);
        System.out.println("Total records: " + records.size());
    }

    private void printStudentHeader() {
        System.out.printf("%-14s | %-22s | %-24s | %6s%n", "Student ID", "Name", "Programme", "Marks");
        System.out.println("---------------+------------------------+--------------------------+-------");
    }

    private void addServiceRequest() {
        String id = readStudentId("Student ID: ");
        if (hashTable.get(id) == null) { error("Cannot add request: student ID not found."); return; }
        String description = readRequired("Request description: ");
        ServiceQueue.Request request = new ServiceQueue.Request(nextRequestNumber++, id, description);
        serviceQueue.enqueue(request);
        record("Queued service request #" + request.getRequestNumber() + " for " + id);
        success("Service request #" + request.getRequestNumber() + " added to the queue.");
    }

    private void processServiceRequest() {
        ServiceQueue.Request request = serviceQueue.dequeue();
        if (request == null) { System.out.println("The service queue is empty."); return; }
        record("Processed service request #" + request.getRequestNumber());
        success("Processed: " + request);
    }

    private void displayPendingRequests() {
        System.out.println("SERVICE QUEUE - OLDEST REQUEST FIRST (FIFO)");
        if (serviceQueue.isEmpty()) { System.out.println("No pending service requests."); return; }
        for (ServiceQueue.Request request : serviceQueue.toList()) System.out.println(request);
        System.out.println("Pending requests: " + serviceQueue.size());
    }

    private void displayActions() {
        System.out.println("ACTION STACK - NEWEST ACTION FIRST (LIFO)");
        if (actions.isEmpty()) { System.out.println("No actions recorded yet."); return; }
        for (String action : actions.recentActions()) System.out.println(action);
    }

    private void addLocation() {
        String name = readRequired("Location name: ");
        if (!campus.addLocation(name)) { error("Campus location already exists."); return; }
        record("Added campus location " + Location.cleanName(name));
        success("Campus location added.");
    }

    private void removeLocation() {
        String name = readRequired("Location to remove: ");
        if (!campus.containsLocation(name)) { error("Campus location not found."); return; }
        if (!readYesNo("Remove this location and all its roads?")) {
            System.out.println("Removal cancelled."); return;
        }
        campus.removeLocation(name);
        record("Removed campus location " + Location.cleanName(name));
        success("Campus location and its connections removed.");
    }

    private void changeConnection(boolean adding) {
        String first = readRequired("First location: ");
        String second = readRequired("Second location: ");
        if (!campus.containsLocation(first) || !campus.containsLocation(second)) {
            error("Both campus locations must exist first."); return;
        }
        if (Location.keyOf(first).equals(Location.keyOf(second))) {
            error("A location cannot connect to itself."); return;
        }
        boolean changed = adding ? campus.addConnection(first, second) : campus.removeConnection(first, second);
        if (!changed) { error(adding ? "Connection already exists." : "Connection not found."); return; }
        record((adding ? "Connected " : "Disconnected ") + first + " and " + second);
        success(adding ? "Campus connection added." : "Campus connection removed.");
    }

    private void displayCampus() {
        System.out.println("CAMPUS NETWORK - ADJACENCY LIST (TWO-WAY ROADS)");
        Map<String, List<String>> network = campus.snapshot();
        if (network.isEmpty()) { System.out.println("No campus locations available."); return; }
        for (Map.Entry<String, List<String>> entry : network.entrySet()) {
            String neighbours = entry.getValue().isEmpty() ? "No direct connections" : String.join(", ", entry.getValue());
            System.out.println(entry.getKey() + " -> " + neighbours);
        }
    }

    private void traverse(boolean breadthFirst) {
        String start = readRequired("Starting location: ");
        if (!campus.containsLocation(start)) { error("Starting location not found."); return; }
        List<String> order = breadthFirst ? campus.bfs(start) : campus.dfs(start);
        System.out.println((breadthFirst ? "BFS" : "DFS") + " traversal: " + String.join(" -> ", order));
        System.out.println("Visited " + order.size() + " of " + campus.size() + " locations.");
        System.out.println("Traversal covers locations reachable from the starting point.");
    }

    private void showHelp() {
        System.out.println("Linked list : stores records in insertion order.");
        System.out.println("Stack       : records successful changes, newest first (LIFO).");
        System.out.println("Queue       : serves requests in arrival order (FIFO).");
        System.out.println("BST         : organizes records by student ID; in-order traversal sorts them.");
        System.out.println("Hash table  : finds student IDs using buckets and collision chains.");
        System.out.println("Graph       : stores locations and two-way connections in an adjacency list.");
        System.out.println("BFS / DFS   : visit reachable locations in breadth-first / depth-first order.");
        System.out.println("Student IDs cannot be changed. Marks must be from 0 to 100.");
        System.out.println("Finish pending requests before deleting their student record.");
        System.out.println("Data is held in memory for this session only.");
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        if (!input.hasNextLine()) throw new NoSuchElementException("End of input");
        return input.nextLine().trim();
    }

    private String readRequired(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try { return Student.requireText(value, "Value"); }
            catch (IllegalArgumentException exception) { error(exception.getMessage()); }
        }
    }

    private String readStudentId(String prompt) {
        while (true) {
            try { return Student.normalizeId(readRequired(prompt)); }
            catch (IllegalArgumentException exception) { error(exception.getMessage()); }
        }
    }

    private int readInt(String prompt, int minimum, int maximum) {
        while (true) {
            try {
                int value = Integer.parseInt(readLine(prompt));
                if (value >= minimum && value <= maximum) return value;
            } catch (NumberFormatException ignored) { }
            error("Enter a whole number from " + minimum + " to " + maximum + ".");
        }
    }

    private double readMarks(String prompt) {
        while (true) {
            try {
                double value = Double.parseDouble(readLine(prompt));
                if (Student.validMarks(value)) return value;
            } catch (NumberFormatException ignored) { }
            error("Invalid marks. Enter a number from 0 to 100.");
        }
    }

    private boolean readYesNo(String prompt) {
        while (true) {
            String value = readLine(prompt + " (Y/N): ");
            if (value.equalsIgnoreCase("Y")) return true;
            if (value.equalsIgnoreCase("N")) return false;
            error("Enter Y or N.");
        }
    }

    private void record(String action) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        actions.push("[" + timestamp + "] " + action);
    }

    private void success(String message) { System.out.println("[SUCCESS] " + message); }
    private void error(String message) { System.out.println("[ERROR] " + message); }

    private void seedCampus() {
        campus.addLocation("Main Gate");
        campus.addLocation("Library");
        campus.addLocation("Computer Lab");
        campus.addLocation("Cafeteria");
        campus.addConnection("Main Gate", "Library");
        campus.addConnection("Library", "Computer Lab");
        campus.addConnection("Library", "Cafeteria");
    }
}


// the flow of the program is as follows:
// 1. read input
// 2. process input
// 3. display output
// 4. repeat
// all checked and working fine Abdurrahman grp Leader
