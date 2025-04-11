import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * An interface that defines the resources that can be borrowed from the library.
 * Implements methods for tracking borrow statistics and time.
 */
interface Borrowable {
    /**
     * Gets the current borrow count for this resource.
     *
     * @return The number of times this resource is currently borrowed.
     */
    int getBorrowCount();

    /**
     * Sets the borrow count for this resource.
     *
     * @param borrowCount The number to set as the borrow count.
     */
    void setBorrowCount(int borrowCount);

    /**
     * Gets the total number of times this resource has been borrowed.
     *
     * @return The total borrow count.
     */
    int getTotalBorrowCount();

    /**
     * Increases the total borrow count by one.
     */
    void increaseTotalBorrowCount();

    /**
     * Gets the total time this resource has been borrowed in minutes.
     *
     * @return The total borrow time in minutes.
     */
    long getBorrowTime();

    /**
     * Increases the borrow time by specified minutes.
     *
     * @param minutes The number of minutes to add to the borrow time.
     */
    void increaseBorrowTime(long minutes);
}

/**
 * The main class that serves as the entry point for the Library Management System.
 * It continuously reads commands from the standard input until the "finish"
 * command is entered.
 */
public class Main {
    /**
     * The main method that starts the Library Management System.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String command;
        while (true) {
            command = scanner.nextLine();
            if (command.equalsIgnoreCase("finish")) {
                break;
            }
            System.out.println(CommandHandeling.processCommand(command));
        }

        scanner.close();
    }
}

/**
 * The CommandHandeling class is responsible for parsing and processing user commands.
 * Each command is processed to perform actions related to library resource and user management.
 */
class CommandHandeling {

    /**
     * Processes a single command by determining the action and parameters.
     * It parses the command string and routes to the appropriate handler.
     *
     * @param command The command string entered by the user.
     * @return A feedback message indicating the result of the command.
     */
    public static String processCommand(String command) {
        String action;
        String[] parameters;
        if (command.contains("#")) {
            // Split command by '#' or '|'
            String[] commandParts = command.split("[#\\|]");
            action = commandParts[0];
            // Extract parameters, handling potential empty strings from splitting
            parameters = Arrays.stream(commandParts, 1, commandParts.length).filter(p -> !p.isEmpty()).toArray(String[]::new);
        } else {
            action = command;
            parameters = new String[0]; // Ensure parameters is never null
        }

        return actionProcess(action, parameters);
    }

    /**
     * Processes the specified action with the given parameters.
     * This method routes the command to the appropriate handler based on the action.
     *
     * @param action     The action to perform.
     * @param parameters The parameters for the action.
     * @return A feedback message indicating the result of the action.
     */
    private static String actionProcess(String action, String[] parameters) {
        String feedback;

        try {
            switch (action) {
                case "add-library":
                    feedback = handleAddLibrary(parameters);
                    break;
                case "add-category":
                    feedback = handleAddCategory(parameters);
                    break;
                case "add-student":
                case "add-manager":
                case "add-staff":
                    feedback = handleAddUser(action, parameters);
                    break;
                case "remove-user":
                    feedback = handleRemoveUser(parameters);
                    break;
                case "add-book":
                    feedback = handleAddBook(parameters);
                    break;
                case "add-thesis":
                    feedback = handleAddThesis(parameters);
                    break;
                case "add-ganjineh-book":
                    feedback = handleAddTreasureBook(parameters);
                    break;
                case "add-selling-book":
                    feedback = handleAddSellingBook(parameters);
                    break;
                case "remove-resource":
                    feedback = handleRemoveResource(parameters);
                    break;
                case "borrow":
                    feedback = handleBorrow(parameters);
                    break;
                case "return":
                    feedback = handleReturn(parameters);
                    break;
                case "buy":
                    feedback = handleBuy(parameters);
                    break;
                case "read":
                    feedback = handleRead(parameters);
                    break;
                case "add-comment":
                    feedback = handleAddComment(parameters);
                    break;
                case "search":
                    feedback = handleSearchResource(parameters);
                    break;
                case "search-user":
                    feedback = handleSearchUser(parameters);
                    break;
                case "library-report":
                    feedback = handleLibraryReport(parameters);
                    break;
                case "category-report":
                    feedback = handleCategoryReport(parameters);
                    break;
                case "report-passed-deadline":
                    feedback = handleReportPassedDeadline(parameters);
                    break;
                case "report-penalties-sum":
                    feedback = handleReportPenaltiesSum(parameters);
                    break;
                case "report-sell":
                    feedback = handleReportSell(parameters);
                    break;
                case "report-most-popular":
                    feedback = handleReportMostPopular(parameters);
                    break;
                default:
                    feedback = "invalid-command"; // Consistent naming
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            // Catch common errors due to incorrect parameter count or type
            feedback = "invalid-parameters";
        } catch (Exception e) {
            // Catch unexpected errors
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace(); // Log the stack trace for debugging
            feedback = "internal-error";
        }

        return feedback;
    }

    /**
     * Handles the 'add-library' command.
     * Requires admin authentication.
     *
     * @param parameters Command parameters [adminId, adminPass, libraryId, libraryName, year, desks, address].
     * @return Feedback string.
     */
    private static String handleAddLibrary(String[] parameters) {
        String feedback = Admin.adminAuthenticator(parameters[0], parameters[1]);
        if (!feedback.equals("success")) {
            return feedback;
        }
        return Library.addLibrary(parameters[2], parameters[3], parameters[4], Integer.parseInt(parameters[5]), parameters[6]);
    }

    /**
     * Handles the 'add-category' command.
     * Requires admin authentication.
     *
     * @param parameters Command parameters [adminId, adminPass, categoryId, categoryName, parentCategoryId].
     * @return Feedback string.
     */
    private static String handleAddCategory(String[] parameters) {
        String feedback = Admin.adminAuthenticator(parameters[0], parameters[1]);
        if (!feedback.equals("success")) {
            return feedback;
        }
        return Category.addCategory(parameters[2], parameters[3], parameters[4]);
    }

    /**
     * Handles the 'add-student', 'add-manager', 'add-staff' commands.
     * Requires admin authentication.
     *
     * @param action     The specific add user action ('add-student', 'add-manager', 'add-staff').
     * @param parameters Command parameters depending on the action.
     * @return Feedback string.
     */
    private static String handleAddUser(String action, String[] parameters) {
        String feedback = Admin.adminAuthenticator(parameters[0], parameters[1]);
        if (!feedback.equals("success")) {
            return feedback;
        }

        User userToAdd = null;
        switch (action) {
            case "add-student":
                userToAdd = new Student(parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8]);
                break;
            case "add-manager":
                // Check if library exists before adding manager
                if (Library.libraryFinder(parameters[9]) == null) {
                    return "not-found";
                }
                userToAdd = new Manager(parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8], parameters[9]);
                break;
            case "add-staff":
                if (parameters.length > 9 && parameters[9].equals("professor")) {
                    userToAdd = new Master(parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8]);
                } else if (parameters.length > 9 && parameters[9].equals("staff")) {
                    userToAdd = new Staff(parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8]);
                } else {
                    return "invalid-parameters"; // Or a more specific error
                }
                break;
        }

        if (userToAdd != null) {
            return User.addUser(userToAdd);
        } else {
            // Should not happen if logic is correct, but as a safeguard
            return "internal-error";
        }
    }

    /**
     * Handles the 'remove-user' command.
     * Requires admin authentication.
     *
     * @param parameters Command parameters [adminId, adminPass, userIdToRemove].
     * @return Feedback string.
     */
    private static String handleRemoveUser(String[] parameters) {
        String feedback = Admin.adminAuthenticator(parameters[0], parameters[1]);
        if (!feedback.equals("success")) {
            return feedback;
        }
        return User.removeUser(parameters[2]);
    }

    /**
     * Handles the 'add-book' command.
     * Requires manager authentication for the target library.
     *
     * @param parameters Command parameters [managerId, managerPass, bookId, title, author, publisher, year, copies, category, libraryId].
     * @return Feedback string.
     */
    private static String handleAddBook(String[] parameters) {
        String libraryId = parameters[9];
        String feedback = Manager.managerAuthenticator(parameters[0], parameters[1], libraryId);
        if (!feedback.equals("success")) {
            return feedback;
        }
        Library library = Library.libraryFinder(libraryId);
        // Library existence already checked in managerAuthenticator, but check again for safety
        if (library == null) return "not-found";
        Book book = new Book(parameters[2], parameters[3], parameters[4], Integer.parseInt(parameters[7]), parameters[5], parameters[6], parameters[8]);
        return library.addResource(book);
    }

    /**
     * Handles the 'add-thesis' command.
     * Requires manager authentication for the target library.
     *
     * @param parameters Command parameters [managerId, managerPass, thesisId, title, author, studentName, year, category, libraryId].
     * @return Feedback string.
     */
    private static String handleAddThesis(String[] parameters) {
        String libraryId = parameters[8];
        String feedback = Manager.managerAuthenticator(parameters[0], parameters[1], libraryId);
        if (!feedback.equals("success")) {
            return feedback;
        }
        Library library = Library.libraryFinder(libraryId);
        if (library == null) return "not-found";
        Thesis thesis = new Thesis(parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7]);
        return library.addResource(thesis);
    }

    /**
     * Handles the 'add-ganjineh-book' command.
     * Requires manager authentication for the target library.
     *
     * @param parameters Command parameters [managerId, managerPass, bookId, title, author, publisher, year, donator, category, libraryId].
     * @return Feedback string.
     */
    private static String handleAddTreasureBook(String[] parameters) {
        String libraryId = parameters[9];
        String feedback = Manager.managerAuthenticator(parameters[0], parameters[1], libraryId);
        if (!feedback.equals("success")) {
            return feedback;
        }
        Library library = Library.libraryFinder(libraryId);
        if (library == null) return "not-found";
        TreasureBook book = new TreasureBook(parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8]);
        return library.addResource(book);
    }

    /**
     * Handles the 'add-selling-book' command.
     * Requires manager authentication for the target library.
     *
     * @param parameters Command parameters [managerId, managerPass, bookId, title, author, publisher, year, copies, price, discount, category, libraryId].
     * @return Feedback string.
     */
    private static String handleAddSellingBook(String[] parameters) {
        String libraryId = parameters[11];
        String feedback = Manager.managerAuthenticator(parameters[0], parameters[1], libraryId);
        if (!feedback.equals("success")) {
            return feedback;
        }
        Library library = Library.libraryFinder(libraryId);
        if (library == null) return "not-found";
        SellingBook book = new SellingBook(parameters[2], parameters[3], parameters[4], Integer.parseInt(parameters[7]), Integer.parseInt(parameters[8]), Integer.parseInt(parameters[9]), parameters[10]);
        return library.addResource(book);
    }

    /**
     * Handles the 'remove-resource' command.
     * Requires manager authentication for the target library.
     *
     * @param parameters Command parameters [managerId, managerPass, resourceId, libraryId].
     * @return Feedback string.
     */
    private static String handleRemoveResource(String[] parameters) {
        String libraryId = parameters[3];
        String feedback = Manager.managerAuthenticator(parameters[0], parameters[1], libraryId);
        if (!feedback.equals("success")) {
            return feedback;
        }
        Library library = Library.libraryFinder(libraryId);
        if (library == null) return "not-found";
        return library.removeResource(parameters[2]);
    }

    /**
     * Handles the 'borrow' command.
     * Authenticates the user and finds the library/resource.
     *
     * @param parameters Command parameters [userId, userPass, libraryId, resourceId, date, time].
     * @return Feedback string.
     */
    private static String handleBorrow(String[] parameters) {
        User user = User.findUser(parameters[0]);
        if (user == null) return "not-found";
        if (!user.getPassword().equals(parameters[1])) return "invalid-pass";

        Library library = Library.libraryFinder(parameters[2]);
        if (library == null) return "not-found";

        Resource resource = library.resourceFinder(parameters[3]);
        if (resource == null) return "not-found";

        String dateTime = parameters[4] + "|" + parameters[5];
        return library.borrowResource(user, resource, dateTime);
    }

    /**
     * Handles the 'return' command.
     * Authenticates the user and finds the library/resource.
     *
     * @param parameters Command parameters [userId, userPass, libraryId, resourceId, date, time].
     * @return Feedback string.
     */
    private static String handleReturn(String[] parameters) {
        User user = User.findUser(parameters[0]);
        if (user == null) return "not-found";
        if (!user.getPassword().equals(parameters[1])) return "invalid-pass";

        Library library = Library.libraryFinder(parameters[2]);
        if (library == null) return "not-found";

        Resource resource = library.resourceFinder(parameters[3]);
        if (resource == null) return "not-found";

        String dateTime = parameters[4] + "|" + parameters[5];
        return library.returnResource(user, resource, dateTime);
    }

    /**
     * Handles the 'buy' command.
     * Authenticates the user (non-manager) and finds the library/resource.
     *
     * @param parameters Command parameters [userId, userPass, libraryId, resourceId].
     * @return Feedback string.
     */
    private static String handleBuy(String[] parameters) {
        User user = User.findUser(parameters[0]);
        if (user == null) return "not-found";
        if (user instanceof Manager) return "permission-denied";
        if (!user.getPassword().equals(parameters[1])) return "invalid-pass";

        Library library = Library.libraryFinder(parameters[2]);
        if (library == null) return "not-found";

        Resource resource = library.resourceFinder(parameters[3]);
        if (resource == null) return "not-found";

        return library.buy(user, resource);
    }

    /**
     * Handles the 'read' command.
     * Authenticates the user (must be Master) and finds the library/resource.
     *
     * @param parameters Command parameters [userId, userPass, libraryId, resourceId, date, time].
     * @return Feedback string.
     */
    private static String handleRead(String[] parameters) {
        User user = User.findUser(parameters[0]);
        if (user == null) return "not-found";
        if (!(user instanceof Master)) return "permission-denied";
        if (!user.getPassword().equals(parameters[1])) return "invalid-pass";

        Library library = Library.libraryFinder(parameters[2]);
        if (library == null) return "not-found";

        Resource resource = library.resourceFinder(parameters[3]);
        if (resource == null) return "not-found";

        String dateTime = parameters[4] + "|" + parameters[5];
        return library.read(user, resource, dateTime);
    }

    /**
     * Handles the 'add-comment' command.
     * Authenticates the user (Student or Master) and finds the library/resource.
     *
     * @param parameters Command parameters [userId, userPass, libraryId, resourceId, commentText].
     * @return Feedback string.
     */
    private static String handleAddComment(String[] parameters) {
        User user = User.findUser(parameters[0]);
        if (user == null) return "not-found";
        // Allow only students and masters to comment
        if (!(user instanceof Student || user instanceof Master)) return "permission-denied";
        if (!user.getPassword().equals(parameters[1])) return "invalid-pass";

        Library library = Library.libraryFinder(parameters[2]);
        if (library == null) return "not-found";

        Resource resource = library.resourceFinder(parameters[3]);
        if (resource == null) return "not-found";

        resource.addComment(parameters[4]);
        return "success";
    }

    /**
     * Handles the 'search' command for resources.
     *
     * @param parameters Command parameters [searchPhrase].
     * @return Feedback string (list of IDs or 'not-found').
     */
    private static String handleSearchResource(String[] parameters) {
        ArrayList<String> idList = Library.searchResource(parameters[0]);
        return (idList == null || idList.isEmpty()) ? "not-found" : String.join("|", idList);
    }

    /**
     * Handles the 'search-user' command.
     * Authenticates the user (non-Student).
     *
     * @param parameters Command parameters [userId, userPass, searchPhrase].
     * @return Feedback string (list of IDs or 'not-found').
     */
    private static String handleSearchUser(String[] parameters) {
        User user = User.findUser(parameters[0]);
        if (user == null) return "not-found";
        if (user instanceof Student) return "permission-denied"; // Only non-students can search users
        if (!user.getPassword().equals(parameters[1])) return "invalid-pass";

        ArrayList<String> userIdList = User.searchUser(parameters[2]);
        return (userIdList == null || userIdList.isEmpty()) ? "not-found" : String.join("|", userIdList);
    }

    /**
     * Handles the 'library-report' command.
     * Requires manager authentication.
     *
     * @param parameters Command parameters [managerId, managerPass, libraryId].
     * @return Feedback string (report data or error message).
     */
    private static String handleLibraryReport(String[] parameters) {
        String libraryId = parameters[2];
        String feedback = Manager.managerAuthenticator(parameters[0], parameters[1], libraryId);
        if (!feedback.equals("success")) {
            return feedback;
        }
        Library library = Library.libraryFinder(libraryId);
        if (library == null) return "not-found";
        return library.libraryReport();
    }

    /**
     * Handles the 'category-report' command.
     * Requires manager authentication and validates category.
     *
     * @param parameters Command parameters [managerId, managerPass, categoryId, libraryId].
     * @return Feedback string (report data or error message).
     */
    private static String handleCategoryReport(String[] parameters) {
        String libraryId = parameters[3];
        String categoryId = parameters[2];
        String feedback = Manager.managerAuthenticator(parameters[0], parameters[1], libraryId);
        if (!feedback.equals("success")) {
            return feedback;
        }
        if (Category.categoryFinder(categoryId) == null) {
            return "not-found";
        }
        Library library = Library.libraryFinder(libraryId);
        if (library == null) return "not-found";
        return library.categoryReport(categoryId);
    }

    /**
     * Handles the 'report-passed-deadline' command.
     * Requires manager authentication.
     *
     * @param parameters Command parameters [managerId, managerPass, libraryId, date, time].
     * @return Feedback string (list of IDs or 'none').
     */
    private static String handleReportPassedDeadline(String[] parameters) {
        String libraryId = parameters[2];
        String feedback = Manager.managerAuthenticator(parameters[0], parameters[1], libraryId);
        if (!feedback.equals("success")) {
            return feedback;
        }
        Library library = Library.libraryFinder(libraryId);
        if (library == null) return "not-found";

        String dateTime = parameters[3] + "|" + parameters[4];
        ArrayList<String> passedIds = library.reportPassedDeadline(dateTime);
        return (passedIds == null || passedIds.isEmpty()) ? "none" : String.join("|", passedIds);
    }

    /**
     * Handles the 'report-penalties-sum' command.
     * Requires admin authentication.
     *
     * @param parameters Command parameters [adminId, adminPass].
     * @return Feedback string (total penalty sum or error message).
     */
    private static String handleReportPenaltiesSum(String[] parameters) {
        String feedback = Admin.adminAuthenticator(parameters[0], parameters[1]);
        if (!feedback.equals("success")) {
            return feedback;
        }
        return Integer.toString(User.reportPenaltiesSum());
    }

    /**
     * Handles the 'report-sell' command.
     * Requires manager authentication.
     *
     * @param parameters Command parameters [managerId, managerPass, libraryId].
     * @return Feedback string (sell report or error message).
     */
    private static String handleReportSell(String[] parameters) {
        String libraryId = parameters[2];
        String feedback = Manager.managerAuthenticator(parameters[0], parameters[1], libraryId);
        if (!feedback.equals("success")) {
            return feedback;
        }
        Library library = Library.libraryFinder(libraryId);
        if (library == null) return "not-found";
        return library.reportSell();
    }

    /**
     * Handles the 'report-most-popular' command.
     * Requires manager authentication.
     *
     * @param parameters Command parameters [managerId, managerPass, libraryId].
     * @return Feedback string (popularity report or error message).
     */
    private static String handleReportMostPopular(String[] parameters) {
        String libraryId = parameters[2];
        String feedback = Manager.managerAuthenticator(parameters[0], parameters[1], libraryId);
        if (!feedback.equals("success")) {
            return feedback;
        }
        Library library = Library.libraryFinder(libraryId);
        if (library == null) return "not-found";
        return library.reportMostPopular();
    }
}

/**
 * Abstract class representing an administrator with authentication capabilities.
 */
abstract class Admin {
    /**
     * Administrator username
     */
    private static final String USERNAME = "admin";
    /**
     * Administrator password
     */
    private static final String PASSWORD = "AdminPass";

    /**
     * Gets the administrator username.
     *
     * @return The administrator username.
     */
    public static String getUsername() {
        return USERNAME;
    }

    /**
     * Authenticates an administrator based on username and password.
     *
     * @param username The username to authenticate.
     * @param password The password to authenticate.
     * @return "success" if authentication succeeds, appropriate error message otherwise.
     */
    public static String adminAuthenticator(String username, String password) {
        User user = User.findUser(username);

        if (user != null) {
            return "permission-denied";
        }
        if (!username.equals(USERNAME)) {
            return "not-found";
        }
        if (!password.equals(PASSWORD)) {
            return "invalid-pass";
        }
        return "success";
    }

}

/**
 * Represents a library in the system with resources and user interactions.
 * Manages borrowing, returning, and reporting on resources.
 */
class Library {
    /**
     * Map of all libraries in the system, indexed by library ID
     */
    private static HashMap<String, Library> libraryList = new HashMap<>();

    /**
     * Map of resources available in this library, indexed by resource ID
     */
    private HashMap<String, Resource> resourceMap;
    /**
     * Log of all borrowing transactions indexed by (userId+resourceId)
     * Value is array containing: [userId, resourceId, time]
     */
    private HashMap<String, String[]> borrowLog;
    /**
     * Unique identifier for this library
     */
    private String id;
    /**
     * Name of the library
     */
    private String name;
    /**
     * Year the library was established
     */
    private String establishmentYear;
    /**
     * Physical address of the library
     */
    private String address;
    /**
     * Number of desks available in the library
     */
    private int deskCount;

    /**
     * Constructs a new Library with the specified details.
     *
     * @param id                The unique identifier for the library.
     * @param name              The name of the library.
     * @param establishmentYear The year the library was established.
     * @param deskCount         The number of desks available in the library.
     * @param address           The physical address of the library.
     */
    public Library(String id, String name, String establishmentYear, int deskCount, String address) {
        this.id = id;
        this.name = name;
        this.establishmentYear = establishmentYear;
        this.deskCount = deskCount;
        this.address = address;
        this.resourceMap = new HashMap<>();
        this.borrowLog = new HashMap<>();
    }

    /**
     * Finds a library by its ID.
     *
     * @param id The unique identifier of the library to find.
     * @return The Library object if found, otherwise null.
     */
    public static Library libraryFinder(String id) {
        return libraryList.get(id);
    }

    /**
     * Adds a new library to the system if the ID is not already in use.
     *
     * @param id                The unique identifier for the new library.
     * @param name              The name of the library.
     * @param establishmentYear The year the library was established.
     * @param deskCount         The number of desks available in the library.
     * @param address           The physical address of the library.
     * @return "duplicate-id" if the ID is already in use, "success" otherwise.
     */
    public static String addLibrary(String id, String name, String establishmentYear, int deskCount, String address) {
        if (libraryList.containsKey(id)) {
            return "duplicate-id";
        }

        Library library = new Library(id, name, establishmentYear, deskCount, address);
        libraryList.put(id, library);
        return "success";
    }

    /**
     * method to search in the resources
     *
     * @param searchPhrase What we want ot search
     * @return the list of ids found
     */
    public static ArrayList<String> searchResource(String searchPhrase) {
        ArrayList<String> idList = new ArrayList<>();

        for (Library library : libraryList.values()) {
            for (Resource resource : library.resourceMap.values()) {
                if (resource.matchesSearch(searchPhrase)) {
                    idList.add(resource.getId());
                }
            }
        }

        Collections.sort(idList);
        if (idList.isEmpty()) {
            return null;
        }
        return idList;
    }

    /**
     * Finds and returns the resource object with the given ID within the library.
     *
     * @param id The ID of the resource to find.
     * @return The resource object if found, or null if not found.
     */
    public Resource resourceFinder(String id) {
        return resourceMap.get(id);
    }

    /**
     * Removes a resource from the library if it's not currently borrowed.
     *
     * @param id The ID of the resource to remove.
     * @return "success" if the resource was successfully removed,
     * "not-allowed" if the resource is currently borrowed,
     * "not-found" if the resource doesn't exist.
     */
    public String removeResource(String id) {
        Resource resource = resourceMap.get(id);
        if (resource == null) {
            return "not-found";
        }

        if (resource instanceof Borrowable && ((Borrowable) resource).getBorrowCount() != 0) {
            return "not-allowed";
        }

        resourceMap.remove(id);
        return "success";
    }

    /**
     * Adds a new resource to the library if the ID is not a duplicate.
     *
     * @param resource The resource to add to the library.
     * @return "duplicate-id" if a resource with the same ID already exists,
     * "not-found" if the category doesn't exist,
     * "success" if the resource was successfully added.
     */
    public String addResource(Resource resource) {
        if (resourceMap.containsKey(resource.getId())) {
            return "duplicate-id";
        }
        if (Category.categoryFinder(resource.getCategory()) == null) {
            return "not-found";
        }

        resourceMap.put(resource.getId(), resource);
        return "success";
    }

    /**
     * method to borrow resource form the library
     *
     * @param user
     * @param resource
     * @param time
     * @return the result of the borrow
     */
    public String borrowResource(User user, Resource resource, String time) {
        if (user.getBorrowList().size() == user.getBorrowLimit()) {
            return "not-allowed";
        }
        if (user.getPenalty() != 0) {
            return "not-allowed";
        }
        if (!(resource instanceof Borrowable)) {
            return "not-allowed";
        }
        Borrowable borrowableResource = (Borrowable) resource;
        if (borrowableResource.getBorrowCount() == resource.getCopyCount()) {
            return "not-allowed";
        }

        String userResourceKey = user.getId() + resource.getId();
        if (borrowLog.containsKey(userResourceKey)) {
            return "not-allowed";
        }

        borrowableResource.setBorrowCount(borrowableResource.getBorrowCount() + 1);
        String[] borrowDetail = new String[3];
        borrowDetail[0] = user.getId();
        borrowDetail[1] = resource.getId();
        borrowDetail[2] = time;
        borrowLog.put(userResourceKey, borrowDetail);
        user.getBorrowList().add(resource.getId());

        return "success";
    }

    /**
     * method to return the borrowed books to the library
     *
     * @param user
     * @param resource
     * @param returnTime
     * @return
     */
    public String returnResource(User user, Resource resource, String returnTime) {
        String userResourceKey = user.getId() + resource.getId();
        String[] borrowDetail = borrowLog.get(userResourceKey);
        if (borrowDetail == null) {
            return "not-found";
        }
        Borrowable borrowableResource = (Borrowable) resource;

        borrowLog.remove(userResourceKey);
        user.getBorrowList().remove(resource.getId());

        borrowableResource.setBorrowCount(borrowableResource.getBorrowCount() - 1);
        Long time = hoursDifference(returnTime, borrowDetail[2]);
        long timeMinutes = minutesDifference(returnTime, borrowDetail[2]);
        borrowableResource.increaseBorrowTime(timeMinutes);
        borrowableResource.increaseTotalBorrowCount();
        long penalty = 0;

        if (user instanceof Student) {
            if (resource instanceof Book) {
                penalty = (time - 10 * 24) * 50;
            }
            if (resource instanceof Thesis) {
                penalty = (time - 7 * 24) * 50;
            }
        } else {
            if (resource instanceof Book) {
                penalty = (time - 14 * 24) * 100;
            }
            if (resource instanceof Thesis) {
                penalty = (time - 10 * 24) * 100;
            }
        }
        penalty = (penalty >= 0) ? penalty : 0;

        user.setPenalty(user.getPenalty() + (int) penalty);
        if (penalty == 0) return "success";
        else return Long.toString(penalty);
    }

    /**
     * method to buy selling books from the library
     *
     * @param user
     * @param resource
     * @return the result of actions
     */
    public String buy(User user, Resource resource) {
        if (!(resource instanceof SellingBook)) {
            return "not-allowed";
        }
        if (user.getPenalty() != 0) {
            return "not-allowed";
        }
        if (resource.getCopyCount() == 0) {
            return "not-allowed";
        }
        ((SellingBook) resource).increaseSellCount();
        resource.setCopyCount(resource.getCopyCount() - 1);

        return "success";
    }

    /**
     * method to read the treasure books in the library
     *
     * @param user
     * @param resource
     * @param time
     * @return the result of action
     */
    public String read(User user, Resource resource, String time) {
        if (!(resource instanceof TreasureBook)) {
            return "not-allowed";
        }
        TreasureBook treasureBook = (TreasureBook) resource;
        if (user.getPenalty() != 0) {
            return "not-allowed";
        }

        String startTime = time;
        String endTime = addTwoHours(time);

        for (String[] readSession : treasureBook.getReadLog()) {
            if (haveConflict(startTime, endTime, readSession[0], readSession[1])) {
                return "not-allowed";
            }
        }

        treasureBook.addReadingSession(startTime, endTime);
        return "success";
    }

    /**
     * method to report the library resources
     */
    public String libraryReport() {
        int bookCount = 0;
        int thesisCount = 0;
        int borrowedBookCount = 0;
        int borrowedThesisCount = 0;
        int treasureBookCount = 0;
        int sellingBookCount = 0;

        for (Resource resource : resourceMap.values()) {
            switch (resource.getType()) {
                case "book":
                    bookCount += resource.getCopyCount() - ((Borrowable) resource).getBorrowCount();
                    borrowedBookCount += ((Borrowable) resource).getBorrowCount();
                    break;
                case "thesis":
                    thesisCount += resource.getCopyCount() - ((Borrowable) resource).getBorrowCount();
                    borrowedThesisCount += ((Borrowable) resource).getBorrowCount();
                    break;
                case "selling-book":
                    sellingBookCount += resource.getCopyCount();
                    break;
                case "treasure-book":
                    treasureBookCount += resource.getCopyCount();
            }
        }

        return bookCount + " " + thesisCount + " " + borrowedBookCount + " " + borrowedThesisCount + " " + treasureBookCount + " " + sellingBookCount;
    }

    /**
     * method to report the categories of library resources
     **/
    public String categoryReport(String categoryId) {
        int bookCount = 0;
        int thesisCount = 0;
        int sellingBookCount = 0;
        int treasureBookCount = 0;

        for (Resource resource : resourceMap.values()) {
            Category resourceCategory = Category.categoryFinder(resource.getCategory());
            while (resourceCategory != null) {
                if (resourceCategory.getId().equals(categoryId)) {
                    switch (resource.getType()) {
                        case "book":
                            bookCount += resource.getCopyCount() - ((Borrowable) resource).getBorrowCount();
                            break;
                        case "thesis":
                            thesisCount += resource.getCopyCount() - ((Borrowable) resource).getBorrowCount();
                            break;
                        case "selling-book":
                            sellingBookCount += resource.getCopyCount();
                            break;
                        case "treasure-book":
                            treasureBookCount += resource.getCopyCount();
                    }
                    break;
                }
                resourceCategory = Category.categoryFinder(resourceCategory.getParentCategoryId());
            }
        }
        return bookCount + " " + thesisCount + " " + treasureBookCount + " " + sellingBookCount;
    }

    /**
     * method to report the ids of resources that aren't returned until deadline
     *
     * @param currentTime
     * @return list of resources's ids that have passes the deadline
     */
    public ArrayList<String> reportPassedDeadline(String currentTime) {
        ArrayList<String> idList = new ArrayList<>();

        for (String[] log : borrowLog.values()) {
            User user = User.findUser(log[0]);
            Resource resource = resourceFinder(log[1]);

            Long time = hoursDifference(currentTime, log[2]);

            if (user instanceof Student) {
                if (resource instanceof Book) {
                    time -= 10 * 24;
                }
                if (resource instanceof Thesis) {
                    time -= 7 * 24;
                }
            } else {
                if (resource instanceof Book) {
                    time -= 14 * 24;
                }
                if (resource instanceof Thesis) {
                    time -= 10 * 24;
                }
            }

            if (time > 0 && !idList.contains(resource.getId())) {
                idList.add(resource.getId());
            }
        }

        Collections.sort(idList);
        return idList;
    }

    public String reportSell() {
        int totalSold = 0;
        int mostSold = 0;
        String mostSoldBook = null;
        int mostSoldBookCount = 0;
        int totalSellCount = 0;

        for (Resource resource : resourceMap.values()) {
            if (resource instanceof SellingBook) {
                int sellCount = ((SellingBook) resource).getSellCount();
                totalSold += sellCount;
                totalSellCount += ((SellingBook) resource).getTotalSell();

                if (sellCount > mostSold) {
                    mostSold = sellCount;
                    mostSoldBook = resource.getId();
                    mostSoldBookCount = ((SellingBook) resource).getTotalSell();
                }
            }
        }

        return totalSellCount + " " + totalSold + "\n" + mostSoldBook + " " + mostSoldBookCount + " " + mostSold;
    }

    public String reportMostPopular() {
        String mostPopularBook = null;
        long bookBorrowedCount = 0L;
        long maxBookBorrowedTime = 0L;
        String mostPopularThesis = null;
        long thesisBorrowedCount = 0L;
        long maxThesisBorrowedTime = 0L;

        for (Resource resource : resourceMap.values()) {
            if (resource instanceof Borrowable) {
                Borrowable item = (Borrowable) resource;
                if (resource instanceof Book) {
                    if (item.getBorrowTime() > maxBookBorrowedTime) {
                        maxBookBorrowedTime = item.getBorrowTime();
                        mostPopularBook = resource.getId();
                        bookBorrowedCount = item.getTotalBorrowCount();
                    }
                } else if (resource instanceof Thesis) {
                    if (item.getBorrowTime() > maxThesisBorrowedTime) {
                        maxThesisBorrowedTime = item.getBorrowTime();
                        mostPopularThesis = resource.getId();
                        thesisBorrowedCount = item.getTotalBorrowCount();
                    }
                }
            }
        }

        String book = (mostPopularBook == null) ? "null" : mostPopularBook + " " + bookBorrowedCount + " " + (long) Math.ceil((double) maxBookBorrowedTime / (24 * 60));
        String thesis = (mostPopularThesis == null) ? "null" : mostPopularThesis + " " + thesisBorrowedCount + " " + (long) Math.ceil((double) maxThesisBorrowedTime / (24 * 60));
        return book + "\n" + thesis;
    }

    private long hoursDifference(String end, String start) {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-d|HH:mm");
        LocalDateTime startDate, endDate;

        try {
            startDate = LocalDateTime.parse(start, formatter1);
        } catch (DateTimeParseException e) {

            startDate = LocalDateTime.parse(start, formatter2);
        }

        try {
            endDate = LocalDateTime.parse(end, formatter1);
        } catch (DateTimeParseException e) {
            endDate = LocalDateTime.parse(end, formatter2);
        }

        return java.time.Duration.between(startDate, endDate).toHours();
    }

    private long minutesDifference(String end, String start) {
        return hoursDifference(end, start) * 60;
    }

    private boolean haveConflict(String start1, String end1, String start2, String end2) {
        String bigStart = (minutesDifference(start2, start1) > 0) ? start2 : start1;
        String smallEnd = (minutesDifference(end2, end1) > 0) ? end1 : end2;
        return minutesDifference(smallEnd, bigStart) > 0;
    }

    private String addTwoHours(String inputDateTime) {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-d|HH:mm");
        LocalDateTime originalDateTime;

        try {
            originalDateTime = LocalDateTime.parse(inputDateTime, formatter1);
        } catch (DateTimeParseException e) {
            originalDateTime = LocalDateTime.parse(inputDateTime, formatter2);
        }
        LocalDateTime newDateTime = originalDateTime.plusHours(2);

        return newDateTime.format(formatter1);
    }
}

/**
 * Abstract class representing a generic user in the library system.
 * It contains common attributes and methods shared by all user types.
 */
abstract class User {
    /**
     * Map of all users registered in the system, indexed by ID
     */
    private static HashMap<String, User> userMap = new HashMap<>();

    /**
     * Unique identifier for the user
     */
    private String id;
    /**
     * User's password for system access
     */
    private String password;
    /**
     * User's first name
     */
    private String firstName;
    /**
     * User's last name
     */
    private String lastName;
    /**
     * User's national identification code
     */
    private String nationalCode;
    /**
     * User's year of birth
     */
    private String birthYear;
    /**
     * User's residential address
     */
    private String address;
    /**
     * Penalty amount the user owes (if any)
     */
    private int penalty;
    /**
     * List of resource IDs the user has borrowed
     */
    private ArrayList<String> borrowList;

    /**
     * Constructor for the User class.
     *
     * @param id           Unique identifier for the user.
     * @param password     User's password for system access.
     * @param firstName    User's first name.
     * @param lastName     User's last name.
     * @param nationalCode User's national identification code.
     * @param birthYear    User's year of birth.
     * @param address      User's residential address.
     */
    public User(String id, String password, String firstName, String lastName, String nationalCode, String birthYear, String address) {
        this.id = id;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalCode = nationalCode;
        this.birthYear = birthYear;
        this.address = address;
        this.penalty = 0;
        this.borrowList = new ArrayList<>();
    }

    /**
     * Gets the list of all users in the system.
     *
     * @return The set of all User objects.
     */
    public static HashSet<User> getUserList() {
        return new HashSet<>(userMap.values());
    }

    /**
     * Searches for a user by their ID in the list of users.
     *
     * @param id The unique identifier for the user.
     * @return The User object if found, otherwise null.
     */
    public static User findUser(String id) {
        return userMap.get(id);
    }

    /**
     * Adds a new user to the list if the ID is not a duplicate and not the admin's username.
     *
     * @param user The User object to be added.
     * @return "duplicate-id" if the ID is already in use or is the admin's username, "success" otherwise.
     */
    public static String addUser(User user) {
        if (userMap.containsKey(user.getId()) || user.getId().equals(Admin.getUsername())) {
            return "duplicate-id";
        }

        userMap.put(user.getId(), user);
        return "success";
    }

    /**
     * Removes a user from the list by their ID if they have no penalties and no borrowed items.
     *
     * @param id The unique identifier for the user to be removed.
     * @return "not-allowed" if the user has penalties or borrowed items,
     * "success" if the user is removed,
     * "not-found" if the user does not exist.
     */
    public static String removeUser(String id) {
        User user = userMap.get(id);
        if (user == null) {
            return "not-found";
        }

        if (user.penalty != 0 || !user.borrowList.isEmpty()) {
            return "not-allowed";
        }

        userMap.remove(id);
        return "success";
    }

    /**
     * Searches for users whose first or last name contains the search phrase.
     *
     * @param searchPhrase The phrase to search for within user names.
     * @return A sorted list of user IDs that match the search criteria, or null if
     * no matches are found.
     */
    public static ArrayList<String> searchUser(String searchPhrase) {
        searchPhrase = searchPhrase.toLowerCase();
        ArrayList<String> idList = new ArrayList<>();

        for (User user : userMap.values()) {
            if (user.getFirstName().toLowerCase().contains(searchPhrase) || user.getLastName().toLowerCase().contains(searchPhrase)) {
                idList.add(user.getId());
            }
        }

        Collections.sort(idList);

        if (idList.isEmpty()) {
            return null;
        }
        return idList;
    }

    /**
     * Calculates the sum of penalties across all users.
     *
     * @return The total sum of penalties.
     */
    public static int reportPenaltiesSum() {
        int penalty = 0;
        for (User user : userMap.values()) {
            penalty += user.getPenalty();
        }

        return penalty;
    }

    /**
     * Gets the list of resources borrowed by this user.
     *
     * @return ArrayList of borrowed resource IDs.
     */
    public ArrayList<String> getBorrowList() {
        return borrowList;
    }

    /**
     * Gets the maximum number of resources this user can borrow at once.
     * Subclasses may override this method to provide different limits.
     *
     * @return The borrow limit for this user type.
     */
    public int getBorrowLimit() {
        return 5;
    }

    /**
     * Gets the unique identifier for this user.
     *
     * @return The user ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the password for this user's account.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the first name of this user.
     *
     * @return The user's first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name of this user.
     *
     * @return The user's last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the national code for this user.
     *
     * @return The user's national identification code.
     */
    public String getNationalCode() {
        return nationalCode;
    }

    /**
     * Gets the birth year of this user.
     *
     * @return The user's year of birth.
     */
    public String getBirthYear() {
        return birthYear;
    }

    /**
     * Gets the residential address of this user.
     *
     * @return The user's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the current penalty amount for this user.
     *
     * @return The penalty amount.
     */
    public int getPenalty() {
        return penalty;
    }

    /**
     * Sets the penalty amount for this user.
     *
     * @param penalty The new penalty amount.
     */
    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }
}

/**
 * Represents a student user in the library system.
 * Students have specific borrow limits.
 */
class Student extends User {
    /**
     * Constructs a new Student with the specified details.
     *
     * @param id           Unique identifier for the student.
     * @param password     Student's password for system access.
     * @param firstName    Student's first name.
     * @param lastName     Student's last name.
     * @param nationalCode Student's national identification code.
     * @param birthYear    Student's year of birth.
     * @param address      Student's residential address.
     */
    public Student(String id, String password, String firstName, String lastName, String nationalCode, String birthYear, String address) {
        super(id, password, firstName, lastName, nationalCode, birthYear, address);
    }

    /**
     * {@inheritDoc}
     * Students can borrow up to 3 resources at a time.
     */
    @Override
    public int getBorrowLimit() {
        return 3;
    }
}

/**
 * Represents a staff member in the library system.
 * Staff members have standard user privileges.
 */
class Staff extends User {
    /**
     * Constructs a new Staff member with the specified details.
     *
     * @param id           Unique identifier for the staff member.
     * @param password     Staff's password for system access.
     * @param firstName    Staff's first name.
     * @param lastName     Staff's last name.
     * @param nationalCode Staff's national identification code.
     * @param birthYear    Staff's year of birth.
     * @param address      Staff's residential address.
     */
    public Staff(String id, String password, String firstName, String lastName, String nationalCode, String birthYear, String address) {
        super(id, password, firstName, lastName, nationalCode, birthYear, address);
    }
}

/**
 * Represents a professor/master user in the library system.
 * Masters have additional privileges like reading treasure books.
 */
class Master extends User {
    /**
     * Constructs a new Master with the specified details.
     *
     * @param id           Unique identifier for the master.
     * @param password     Master's password for system access.
     * @param firstName    Master's first name.
     * @param lastName     Master's last name.
     * @param nationalCode Master's national identification code.
     * @param birthYear    Master's year of birth.
     * @param address      Master's residential address.
     */
    public Master(String id, String password, String firstName, String lastName, String nationalCode, String birthYear, String address) {
        super(id, password, firstName, lastName, nationalCode, birthYear, address);
    }
}

/**
 * Represents a manager user with additional library management capabilities.
 * Managers are associated with a specific library they can manage.
 */
class Manager extends User {
    /**
     * ID of the library this manager is responsible for
     */
    private String libraryId;

    /**
     * Constructs a new Manager with the specified details.
     *
     * @param id           Unique identifier for the manager.
     * @param password     Manager's password for system access.
     * @param firstName    Manager's first name.
     * @param lastName     Manager's last name.
     * @param nationalCode Manager's national identification code.
     * @param birthYear    Manager's year of birth.
     * @param address      Manager's residential address.
     * @param libraryId    ID of the library this manager is responsible for.
     */
    public Manager(String id, String password, String firstName, String lastName, String nationalCode, String birthYear, String address, String libraryId) {
        super(id, password, firstName, lastName, nationalCode, birthYear, address);
        this.libraryId = libraryId;
    }

    /**
     * Authenticates a manager for a specific library operation.
     * Verifies that the user exists, is a manager, has valid credentials,
     * and is authorized to manage the specified library.
     *
     * @param userId    The ID of the manager to authenticate.
     * @param password  The password to verify.
     * @param libraryId The ID of the library being accessed.
     * @return "success" if authentication succeeds, appropriate error message otherwise.
     */
    public static String managerAuthenticator(String userId, String password, String libraryId) {
        User user = User.findUser(userId);
        if (user == null) {
            return "not-found";
        }
        if (!(user instanceof Manager)) {
            return "permission-denied";
        }
        if (!user.getPassword().equals(password)) {
            return "invalid-pass";
        }
        Library library = Library.libraryFinder(libraryId);
        if (library == null) {
            return "not-found";
        }
        if (!((Manager) user).getLibraryId().equals(libraryId)) {
            return "permission-denied";
        }
        return "success";
    }

    /**
     * Gets the ID of the library this manager is responsible for.
     *
     * @return The library ID.
     */
    public String getLibraryId() {
        return this.libraryId;
    }
}

/**
 * Represents a category with a unique ID, name, and an optional parent
 * category.
 * Provides functionality to manage and find categories.
 */
class Category {
    // A static HashMap to store the list of categories
    private static HashMap<String, Category> categoryMap = new HashMap<>();

    static {
        // resources that doesn't have any category are in this group
        // we will always have a category of "null"
        categoryMap.put("null", new Category("null", "null", null));
    }

    private String id;
    private String name;
    private String parentCategoryId;

    /**
     * Constructs a new Category instance.
     *
     * @param id               The unique identifier for the category.
     * @param name             The human-readable name for the category.
     * @param parentCategoryId The ID of the parent category. Can be null or "null"
     *                         if there is no parent.
     */
    public Category(String id, String name, String parentCategoryId) {
        this.id = id;
        this.name = name;
        if (parentCategoryId == null || parentCategoryId.equals("null")) {
            this.parentCategoryId = null;
        } else {
            this.parentCategoryId = parentCategoryId;
        }
    }

    /**
     * Finds a category by its ID.
     *
     * @param id The ID of the category to find.
     * @return The Category object if found, otherwise null.
     */
    public static Category categoryFinder(String id) {
        if (id == null) {
            return null;
        }
        return categoryMap.get(id);
    }

    /**
     * Adds a new category to the category list.
     */
    public static String addCategory(String id, String name, String parentCategoryId) {
        if (categoryMap.containsKey(id)) {
            return "duplicate-id";
        }
        if (parentCategoryId != null && !categoryMap.containsKey(parentCategoryId)) {
            return "not-found";
        }
        Category category = new Category(id, name, parentCategoryId);
        categoryMap.put(id, category);
        return "success";
    }

    // Getters
    public HashSet<Category> getCategoryList() {
        return new HashSet<>(categoryMap.values());
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getParentCategoryId() {
        return this.parentCategoryId;
    }
}

/**
 * Represents a generic resource with basic attributes and functionalities.
 * Base class for all library resources including books, theses, etc.
 */
class Resource {
    /**
     * Unique identifier for the resource
     */
    private String id;
    /**
     * Title of the resource
     */
    private String title;
    /**
     * Author(s) of the resource
     */
    private String author;
    /**
     * Category the resource belongs to
     */
    private String category;
    /**
     * Number of available copies
     */
    private int copyCount;
    /**
     * List of comments/reviews for this resource
     */
    private ArrayList<String> comments;

    /**
     * Constructs a new Resource with the specified details.
     *
     * @param id        Unique identifier for the resource.
     * @param title     Title of the resource.
     * @param author    Author(s) of the resource.
     * @param copyCount Number of available copies.
     * @param category  Category the resource belongs to.
     */
    public Resource(String id, String title, String author, int copyCount, String category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.copyCount = copyCount;
        this.category = category;
        this.comments = new ArrayList<>();
    }

    /**
     * Gets the type of this resource.
     * This method should be overridden by subclasses to return their specific type.
     *
     * @return A string representing the resource type.
     */
    public String getType() {
        return "resourceType";
    }

    /**
     * Gets the unique identifier for this resource.
     *
     * @return The resource ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the title of this resource.
     *
     * @return The resource title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the author(s) of this resource.
     *
     * @return The resource author(s).
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Gets the number of available copies of this resource.
     *
     * @return The copy count.
     */
    public int getCopyCount() {
        return copyCount;
    }

    /**
     * Sets the number of available copies of this resource.
     *
     * @param copyCount The new copy count.
     */
    public void setCopyCount(int copyCount) {
        this.copyCount = copyCount;
    }

    /**
     * Gets the category this resource belongs to.
     *
     * @return The category ID.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Adds a comment or review to this resource.
     *
     * @param comment The comment text to add.
     */
    public void addComment(String comment) {
        comments.add(comment);
    }

    /**
     * Gets the list of all comments for this resource.
     *
     * @return An unmodifiable list of comments.
     */
    public List<String> getComments() {
        return Collections.unmodifiableList(comments);
    }

    /**
     * Checks if this resource matches the given search phrase.
     * Default implementation checks if the title contains the search phrase.
     * Subclasses can override this to search in additional fields.
     *
     * @param searchPhrase The phrase to search for.
     * @return true if the resource matches the search criteria, false otherwise.
     */
    public boolean matchesSearch(String searchPhrase) {
        searchPhrase = searchPhrase.toLowerCase();
        return this.title.toLowerCase().contains(searchPhrase);
    }
}

/**
 * Represents a book in the library collection.
 * Books can be borrowed and have additional attributes like publisher and publish year.
 */
class Book extends Resource implements Borrowable {
    /**
     * Current number of borrowed copies
     */
    private int borrowCount;
    /**
     * Publisher of the book
     */
    private String publisher;
    /**
     * Year the book was published
     */
    private String publishYear;
    /**
     * Total time this book has been borrowed (in minutes)
     */
    private long borrowTime;
    /**
     * Total number of times this book has been borrowed
     */
    private int totalBorrowCount;

    /**
     * Constructs a new Book with the specified details.
     *
     * @param id          Unique identifier for the book.
     * @param title       Title of the book.
     * @param author      Author(s) of the book.
     * @param copyCount   Number of available copies.
     * @param publisher   Publisher of the book.
     * @param publishYear Year the book was published.
     * @param category    Category the book belongs to.
     */
    public Book(String id, String title, String author, int copyCount, String publisher, String publishYear, String category) {
        super(id, title, author, copyCount, category);
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.borrowCount = 0;
        this.borrowTime = 0;
        this.totalBorrowCount = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getBorrowTime() {
        return borrowTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increaseBorrowTime(long minutes) {
        this.borrowTime += minutes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalBorrowCount() {
        return totalBorrowCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increaseTotalBorrowCount() {
        this.totalBorrowCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "book";
    }

    /**
     * Gets the publisher of this book.
     *
     * @return The book's publisher.
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Gets the publication year of this book.
     *
     * @return The book's publication year.
     */
    public String getPublishYear() {
        return publishYear;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBorrowCount() {
        return borrowCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }

    /**
     * {@inheritDoc}
     * Checks if this book matches the search phrase in title, author, or publisher.
     */
    @Override
    public boolean matchesSearch(String searchPhrase) {
        searchPhrase = searchPhrase.toLowerCase();
        return super.matchesSearch(searchPhrase) || this.getAuthor().toLowerCase().contains(searchPhrase) || this.publisher.toLowerCase().contains(searchPhrase);
    }
}

/**
 * Represents a thesis in the library collection.
 * Theses can be borrowed and have specific attributes like master name and defense year.
 */
class Thesis extends Resource implements Borrowable {
    /**
     * The master associated with this thesis
     */
    private String master;
    /**
     * The year this thesis was defended
     */
    private String defenseYear;
    /**
     * Current number of borrowed copies
     */
    private int borrowCount;
    /**
     * Total time this thesis has been borrowed (in minutes)
     */
    private long borrowTime;
    /**
     * Total number of times this thesis has been borrowed
     */
    private int totalBorrowCount;

    /**
     * Constructs a new Thesis with the specified details.
     *
     * @param id          Unique identifier for the thesis.
     * @param title       Title of the thesis.
     * @param author      Author of the thesis.
     * @param master      Master associated with this thesis.
     * @param defenseYear Year the thesis was defended.
     * @param category    Category the thesis belongs to.
     */
    public Thesis(String id, String title, String author, String master, String defenseYear, String category) {
        super(id, title, author, 1, category);
        this.master = master;
        this.defenseYear = defenseYear;
        this.borrowTime = 0L;
        this.totalBorrowCount = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getBorrowTime() {
        return borrowTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increaseBorrowTime(long minutes) {
        this.borrowTime += minutes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalBorrowCount() {
        return totalBorrowCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increaseTotalBorrowCount() {
        this.totalBorrowCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "thesis";
    }

    /**
     * Gets the master associated with this thesis.
     *
     * @return The master's name.
     */
    public String getMaster() {
        return master;
    }

    /**
     * Gets the defense year of this thesis.
     *
     * @return The defense year.
     */
    public String getDefenseYear() {
        return defenseYear;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBorrowCount() {
        return borrowCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }

    /**
     * {@inheritDoc}
     * Checks if this thesis matches the search phrase in title, author, or master.
     */
    @Override
    public boolean matchesSearch(String searchPhrase) {
        searchPhrase = searchPhrase.toLowerCase();

        return this.getTitle().toLowerCase().contains(searchPhrase) || this.getAuthor().toLowerCase().contains(searchPhrase) || this.getMaster().toLowerCase().contains(searchPhrase);
    }
}

/**
 * Represents a selling book in the library collection.
 * These books can be purchased by users and have price and discount information.
 */
class SellingBook extends Resource {
    /**
     * Original price of the book
     */
    private int price;
    /**
     * Discount percentage off the original price
     */
    private int off;
    /**
     * Total money made from selling this book
     */
    private int sellCount;
    /**
     * Number of copies sold
     */
    private int totalSell;

    /**
     * Constructs a new SellingBook with the specified details.
     *
     * @param id        Unique identifier for the book.
     * @param title     Title of the book.
     * @param author    Author(s) of the book.
     * @param copyCount Number of available copies.
     * @param price     Original price of the book.
     * @param off       Discount percentage off the original price.
     * @param category  Category the book belongs to.
     */
    public SellingBook(String id, String title, String author, int copyCount, int price, int off, String category) {
        super(id, title, author, copyCount, category);
        this.price = price;
        this.off = off;
        this.sellCount = 0;
        this.totalSell = 0;
    }

    /**
     * Calculates the final price after applying the discount.
     *
     * @return The final price after discount.
     */
    public int getFinalPrice() {
        double discount = (100.0 - off) / 100.0;
        double discountedPrice = price * discount;
        return (int) Math.floor(discountedPrice);
    }

    /**
     * Gets the total money made from selling this book.
     *
     * @return The total money made.
     */
    public int getSellCount() {
        return this.sellCount;
    }

    /**
     * Gets the number of copies sold.
     *
     * @return The number of copies sold.
     */
    public int getTotalSell() {
        return totalSell;
    }

    /**
     * Increases the sell count and total copies sold when a book is purchased.
     */
    public void increaseSellCount() {
        this.sellCount += getFinalPrice();
        this.totalSell++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "selling-book";
    }

    /**
     * Gets the original price of this book.
     *
     * @return The original price.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Gets the discount percentage off the original price.
     *
     * @return The discount percentage.
     */
    public int getOff() {
        return off;
    }

    /**
     * {@inheritDoc}
     * Checks if this book matches the search phrase in title or author.
     */
    @Override
    public boolean matchesSearch(String searchPhrase) {
        searchPhrase = searchPhrase.toLowerCase();

        return this.getTitle().toLowerCase().contains(searchPhrase) || this.getAuthor().toLowerCase().contains(searchPhrase);
    }
}

/**
 * Represents a treasure book in the library collection.
 * These are special books that can only be read in the library.
 */
class TreasureBook extends Resource {
    /**
     * The publisher of the book
     */
    private String publisher;
    /**
     * The year the book was published
     */
    private String publishYear;
    /**
     * The person who donated the book
     */
    private String donator;
    /**
     * Map of reading sessions for this book - key: sessionId, value: [startTime, endTime]
     */
    private HashMap<String, String[]> readLog;

    /**
     * Current session counter for generating unique session IDs
     */
    private int sessionCounter;

    /**
     * Constructs a new TreasureBook with the specified details.
     *
     * @param id          The unique identifier for the book.
     * @param title       The title of the book.
     * @param author      The author of the book.
     * @param publisher   The publisher of the book.
     * @param publishYear The year the book was published.
     * @param donator     The person who donated the book.
     * @param category    The category the book belongs to.
     */
    public TreasureBook(String id, String title, String author, String publisher, String publishYear, String donator, String category) {
        super(id, title, author, 1, category);
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.donator = donator;
        this.readLog = new HashMap<>();
        this.sessionCounter = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "treasure-book";
    }

    /**
     * Gets the publisher of this book.
     *
     * @return The publisher name.
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Gets the publication year of this book.
     *
     * @return The publication year.
     */
    public String getPublishYear() {
        return publishYear;
    }

    /**
     * Gets the name of the person who donated this book.
     *
     * @return The donator's name.
     */
    public String getDonator() {
        return donator;
    }

    /**
     * Gets the reading log for this book.
     *
     * @return A HashSet containing the reading log entries.
     */
    public Collection<String[]> getReadLog() {
        return readLog.values();
    }

    /**
     * Adds a new reading session to the log.
     *
     * @param startTime The start time of the reading session
     * @param endTime   The end time of the reading session
     */
    public void addReadingSession(String startTime, String endTime) {
        String sessionId = "session_" + (++sessionCounter);
        String[] session = new String[2];
        session[0] = startTime;
        session[1] = endTime;
        readLog.put(sessionId, session);
    }

    /**
     * {@inheritDoc}
     * Checks if this book matches the search phrase in title, author, or publisher.
     */
    @Override
    public boolean matchesSearch(String searchPhrase) {
        searchPhrase = searchPhrase.toLowerCase();

        return this.getTitle().toLowerCase().contains(searchPhrase) || this.getAuthor().toLowerCase().contains(searchPhrase) || this.getPublisher().toLowerCase().contains(searchPhrase);
    }
}