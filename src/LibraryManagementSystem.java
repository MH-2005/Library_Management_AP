/**
 * Library Management System
 * 
 * <p>This system provides a comprehensive solution for managing library resources,
 * users, and operations. It supports various types of resources including books,
 * theses, treasure books, and selling books. The system also handles user management
 * with different user roles such as students, staff, professors, and managers.</p>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Resource management (books, theses, treasure books, selling books)</li>
 *   <li>User management with different roles and permissions</li>
 *   <li>Borrowing and returning resources with automatic penalty calculation</li>
 *   <li>Reading sessions for treasure books</li>
 *   <li>Category management for resources</li>
 *   <li>Reporting functionality for various metrics</li>
 * </ul>
 * 
 * <h2>System Architecture</h2>
 * <p>The system is built using object-oriented principles with inheritance and interfaces
 * to model the different types of resources and users. The main components include:</p>
 * 
 * <ul>
 *   <li><strong>Resources:</strong> Base class for all library resources with specialized subclasses</li>
 *   <li><strong>Users:</strong> Base class for all system users with role-specific subclasses</li>
 *   <li><strong>Library:</strong> Manages collections of resources and operations</li>
 *   <li><strong>Category:</strong> Organizes resources into hierarchical categories</li>
 *   <li><strong>TimeUtils:</strong> Handles all date and time operations</li>
 * </ul>
 * 
 * <h2>Command Interface</h2>
 * <p>The system accepts commands through a text-based interface. Commands are formatted as:</p>
 * <pre>command#param1#param2|param3</pre>
 * 
 * <h3>Example Commands</h3>
 * <ul>
 *   <li><code>add-library#admin#AdminPass#lib1#Central Library#2020#10#123 Main St</code></li>
 *   <li><code>add-book#manager1#pass1#book1#Title#Author#Publisher#2022#category1#5#lib1</code></li>
 *   <li><code>borrow#user1#pass1#lib1#book1#2023-01-01|10:00</code></li>
 *   <li><code>return#user1#pass1#lib1#book1#2023-01-15|14:30</code></li>
 * </ul>
 * 
 * <h2>Time Handling</h2>
 * <p>The system uses the <code>TimeUtils</code> class to handle all date and time operations.
 * It supports multiple date formats:</p>
 * <ul>
 *   <li>yyyy-MM-dd</li>
 *   <li>yyyy-MM-dd|HH:mm</li>
 *   <li>yyyy-MM-ddTHH:mm</li>
 * </ul>
 * 
 * <p>Time calculations are used for:</p>
 * <ul>
 *   <li>Calculating borrowing periods</li>
 *   <li>Determining overdue penalties</li>
 *   <li>Scheduling reading sessions for treasure books</li>
 *   <li>Reporting on passed deadlines</li>
 * </ul>
 * 
 * <h2>Borrowing Rules</h2>
 * <ul>
 *   <li><strong>Students:</strong> Can borrow books for 10 days and theses for 7 days</li>
 *   <li><strong>Staff/Professors:</strong> Can borrow books for 14 days and theses for 10 days</li>
 *   <li><strong>Penalties:</strong> 50 units per day for students, 100 units per day for staff/professors</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>
 * // Initialize the system
 * // Add a library
 * add-library#admin#AdminPass#lib1#Central Library#2020#10#123 Main St
 * 
 * // Add a category
 * add-category#admin#AdminPass#cat1#Fiction#null
 * 
 * // Add a book
 * add-book#manager1#pass1#book1#The Great Gatsby#F. Scott Fitzgerald#Scribner#1925#cat1#5#lib1
 * 
 * // Add a student
 * add-student#admin#AdminPass#student1#pass1#John#Doe#12345#2000#456 Student St
 * 
 * // Borrow a book
 * borrow#student1#pass1#lib1#book1#2023-01-01|10:00
 * 
 * // Return a book
 * return#student1#pass1#lib1#book1#2023-01-15|14:30
 * </pre>
 * 
 * <h2>Error Handling</h2>
 * <p>The system returns specific error codes for different scenarios:</p>
 * <ul>
 *   <li><code>not-found</code>: Resource, user, or library not found</li>
 *   <li><code>duplicate-id</code>: Attempt to create a resource with an existing ID</li>
 *   <li><code>not-allowed</code: Operation not permitted due to constraints</li>
 *   <li><code>invalid-pass</code>: Incorrect password provided</li>
 *   <li><code>permission-denied</code>: User lacks required permissions</li>
 * </ul>
 * 
 * <h2>Future Enhancements</h2>
 * <ul>
 *   <li>Graphical user interface</li>
 *   <li>Database integration for persistent storage</li>
 *   <li>Online reservation system</li>
 *   <li>Email notifications for due dates</li>
 *   <li>Integration with external library systems</li>
 * </ul>
 * 
 * @version 1.0
 */
public class LibraryManagementSystem {
    // This is a documentation class only, no implementation
} 