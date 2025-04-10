import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * An interface that defines the resources that can borrowed
 */
interface Borrowable {
    int getBorrowCount();

    void setBorrowCount(int borrowCount);

    int getTotalBorrowCount();

    void increaseTotalBorrowCount();

    long getBorrowTime();

    void increaseBorrowTime(long minutes);
}

/**
 * The main functions that used for get commands.
 * It continuously reads commands from the standard input until the "finish"
 * command is entered.
 */
public class Main {
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
 * The CommandHandling class is responsible for handling  and processing user
 * commands.
 * Each command is processed to perform actions related to student and staff
 * management.
 */
class CommandHandeling {

    /**
     * Processes a single command by determining the action and parameters.
     * It then calls the appropriate method based on the action.
     *
     * @param command The command string entered by the user.
     */
    public static String processCommand(String command) {
        String action;
        String[] parameters;
        if (command.contains("#")) {
            String[] commandParts = command.split("[#\\|]");
            action = commandParts[0];
            parameters = Arrays.copyOfRange(commandParts, 1, commandParts.length);
        } else {
            action = command;
            parameters = null;
        }

        String feedback = actionProcess(action, parameters);
        return feedback;
    }

    /**
     * this function help processCommand to find witch action should be run.
     * @param action
     * @param parameters
     * @return
     */
    private static String actionProcess(String action, String[] parameters) {
        String feedback = null;
        User user;
        Library library;
        Resource resource;

        switch (action) {
            case "add-library":
                feedback = Admin.adminAuthenticator(parameters[0], parameters[1]);
                if (!feedback.equals("success")) {
                    break;
                }
                feedback = Library.addLibrary(parameters[2], parameters[3], parameters[4], Integer.parseInt(parameters[5]), parameters[6]);
                break;
            case "add-category":
                feedback = Admin.adminAuthenticator(parameters[0], parameters[1]);
                if (!feedback.equals("success")) {
                    break;
                }
                feedback = Category.addCategory(parameters[2], parameters[3], parameters[4]);
                break;
            case "add-student":
            case "add-manager":
            case "add-staff":
                feedback = Admin.adminAuthenticator(parameters[0], parameters[1]);
                if (!feedback.equals("success")) {
                    break;
                }
                if (action.equals("add-student")) {
                    feedback = User.addUser(new Student(parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8]));
                }
                if (action.equals("add-manager")) {
                    feedback = User.addUser(new Manager(parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8], parameters[9]));

                    if (feedback.equals("success") && Library.libraryFinder(parameters[9]) == null) {
                        User.removeUser(parameters[2]);
                        feedback = "not-found";
                    }
                }
                if (action.equals("add-staff") && parameters[9].equals("staff")) {
                    feedback = User.addUser(new Staff(parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8]));
                }
                if (action.equals("add-staff") && parameters[9].equals("professor")) {
                    feedback = User.addUser(new Master(parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8]));
                }
                break;
            case "remove-user":
                feedback = Admin.adminAuthenticator(parameters[0], parameters[1]);
                if (!feedback.equals("success")) {
                    break;
                }
                feedback = User.removeUser(parameters[2]);
                break;
            case "add-book":
                feedback = Manager.managerAuthenticator(parameters[0], parameters[1], parameters[9]);
                if (!feedback.equals("success")) {
                    break;
                }
                feedback = Library.libraryFinder(parameters[9]).addResource(new Book(parameters[2], parameters[3], parameters[4], Integer.parseInt(parameters[7]), parameters[5], parameters[6], parameters[8]));
                break;
            case "add-thesis":
                feedback = Manager.managerAuthenticator(parameters[0], parameters[1], parameters[8]);
                if (!feedback.equals("success")) {
                    break;
                }
                feedback = Library.libraryFinder(parameters[8]).addResource(new Thesis(parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7]));
                break;
            case "add-ganjineh-book":
                feedback = Manager.managerAuthenticator(parameters[0], parameters[1], parameters[9]);
                if (!feedback.equals("success")) {
                    break;
                }
                feedback = Library.libraryFinder(parameters[9]).addResource(new TreasureBook(parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8]));
                break;
            case "add-selling-book":
                feedback = Manager.managerAuthenticator(parameters[0], parameters[1], parameters[11]);
                if (!feedback.equals("success")) {
                    break;
                }
                feedback = Library.libraryFinder(parameters[11]).addResource(new SellingBook(parameters[2], parameters[3], parameters[4], Integer.parseInt(parameters[7]), Integer.parseInt(parameters[8]), Integer.parseInt(parameters[9]), parameters[10]));
                break;
            case "remove-resource":
                feedback = Manager.managerAuthenticator(parameters[0], parameters[1], parameters[3]);
                if (!feedback.equals("success")) {
                    break;
                }
                feedback = Library.libraryFinder(parameters[3]).removeResource(parameters[2]);
                break;
            case "borrow":
                user = User.findUser(parameters[0]);
                if (user == null) {
                    feedback = "not-found";
                    break;
                }
                if (!user.getPassword().equals(parameters[1])) {
                    feedback = "invalid-pass";
                    break;
                }
                library = Library.libraryFinder(parameters[2]);
                if (library == null) {
                    feedback = "not-found";
                    break;
                }
                resource = library.resourceFinder(parameters[3]);
                if (resource == null) {
                    feedback = "not-found";
                    break;
                }
                feedback = library.borrowResource(user, resource, parameters[4] + "|" + parameters[5]);
                break;
            case "return":
                user = User.findUser(parameters[0]);
                if (user == null) {
                    feedback = "not-found";
                    break;
                }
                if (!user.getPassword().equals(parameters[1])) {
                    feedback = "invalid-pass";
                    break;
                }
                library = Library.libraryFinder(parameters[2]);
                if (library == null) {
                    feedback = "not-found";
                    break;
                }
                resource = library.resourceFinder(parameters[3]);
                if (resource == null) {
                    feedback = "not-found";
                    break;
                }
                feedback = library.returnResource(user, resource, parameters[4] + "|" + parameters[5]);
                break;
            case "buy":
                user = User.findUser(parameters[0]);
                if (user == null) {
                    feedback = "not-found";
                    break;
                }
                if (user instanceof Manager) {
                    feedback = "permission-denied";
                    break;
                }
                if (!user.getPassword().equals(parameters[1])) {
                    feedback = "invalid-pass";
                    break;
                }
                library = Library.libraryFinder(parameters[2]);
                if (library == null) {
                    feedback = "not-found";
                    break;
                }
                resource = library.resourceFinder(parameters[3]);
                if (resource == null) {
                    feedback = "not-found";
                    break;
                }
                feedback = library.buy(user, resource);
                break;
            case "read":
                user = User.findUser(parameters[0]);
                if (user == null) {
                    feedback = "not-found";
                    break;
                }
                if (!(user instanceof Master)) {
                    feedback = "permission-denied";
                    break;
                }
                if (!user.getPassword().equals(parameters[1])) {
                    feedback = "invalid-pass";
                    break;
                }
                library = Library.libraryFinder(parameters[2]);
                if (library == null) {
                    feedback = "not-found";
                    break;
                }
                resource = library.resourceFinder(parameters[3]);
                if (resource == null) {
                    feedback = "not-found";
                    break;
                }
                feedback = library.read(user, resource, parameters[4] + "|" + parameters[5]);
                break;
            case "add-comment":
                user = User.findUser(parameters[0]);
                if (user == null) {
                    feedback = "not-found";
                    break;
                }
                if (!(user instanceof Master || user instanceof Student)) {
                    feedback = "permission-denied";
                    break;
                }
                if (!user.getPassword().equals(parameters[1])) {
                    feedback = "invalid-pass";
                    break;
                }
                library = Library.libraryFinder(parameters[2]);
                if (library == null) {
                    feedback = "not-found";
                    break;
                }
                resource = library.resourceFinder(parameters[3]);
                if (resource == null) {
                    feedback = "not-found";
                    break;
                }
                resource.addComment(parameters[4]);
                feedback = "success";
                break;
            case "search":
                ArrayList<String> idList = Library.searchResource(parameters[0]);
                if (idList == null) {
                    feedback = "not-found";
                    break;
                }
                feedback = String.join("|", idList);
                break;
            case "search-user":
                user = User.findUser(parameters[0]);
                if (user == null) {
                    feedback = "not-found";
                    break;
                }
                if (user instanceof Student) {
                    feedback = "permission-denied";
                    break;
                }
                if (!user.getPassword().equals(parameters[1])) {
                    feedback = "invalid-pass";
                    break;
                }
                ArrayList<String> userIdList = User.searchUser(parameters[2]);
                if (userIdList == null) {
                    feedback = "not-found";
                    break;
                }
                feedback = String.join("|", userIdList);
                break;
            case "library-report":
                feedback = Manager.managerAuthenticator(parameters[0], parameters[1], parameters[2]);
                if (!feedback.equals("success")) {
                    break;
                }
                feedback = Library.libraryFinder(parameters[2]).libraryReport();
                break;
            case "category-report":
                feedback = Manager.managerAuthenticator(parameters[0], parameters[1], parameters[3]);
                if (!feedback.equals("success")) {
                    break;
                }
                if (Category.categoryFinder(parameters[2]) == null) {
                    feedback = "not-found";
                    break;
                }
                feedback = Library.libraryFinder(parameters[3]).categoryReport(parameters[2]);
                break;
            case "report-passed-deadline":
                feedback = Manager.managerAuthenticator(parameters[0], parameters[1], parameters[2]);
                if (!feedback.equals("success")) {
                    break;
                }
                ArrayList<String> passedIds = Library.libraryFinder(parameters[2]).reportPassedDeadline(parameters[3] + "|" + parameters[4]);
                if (passedIds.isEmpty()) {
                    feedback = "none";
                    break;
                }
                feedback = String.join("|", passedIds);
                break;
            case "report-penalties-sum":
                feedback = Admin.adminAuthenticator(parameters[0], parameters[1]);
                if (!feedback.equals("success")) {
                    break;
                }
                feedback = Integer.toString(User.reportPenaltiesSum());
                break;
            case "report-sell":
                feedback = Manager.managerAuthenticator(parameters[0], parameters[1], parameters[2]);
                if (!feedback.equals("success")) {
                    break;
                }
                feedback = Library.libraryFinder(parameters[2]).reportSell();
                break;
            case "report-most-popular":
                feedback = Manager.managerAuthenticator(parameters[0], parameters[1], parameters[2]);
                if (!feedback.equals("success")) {
                    break;
                }
                feedback = Library.libraryFinder(parameters[2]).reportMostPopular();
                break;
            default:
                feedback = "invalid command";
                break;
        }

        return feedback;
    }
}

/**
 * Represents an abstract admin class with authentication functionality.
 */
abstract class Admin {
    // Default admin username and password.
    private static String USERNAME = "admin";
    private static String PASSWORD = "AdminPass";

    // getters
    public static String getUsername() {
        return USERNAME;
    }

    /**
     * Authenticates an admin based on the provided username and password.
     *
     * @param username The username to authenticate.
     * @param password The password to validate.
     * @return A status string indicating the result of authentication:
     */
    public static String adminAuthenticator(String username, String password) {
        User user = User.findUser(username);

        if (user != null) {
            return "permission-denied";
        }
        if (user == null && !username.equals(USERNAME)) {
            return "not-found";
        }
        if (!password.equals(PASSWORD)) {
            return "invalid-pass";
        }
        return "success";
    }

}

/**
 * The Library class represents a library entity with its properties and
 * operations.
 */
class Library {
    private static HashMap<String,Library> libraryList = new HashMap<>();

    private HashSet<Resource> resourceList;
    private HashSet<String[]> borrowLog; // userId + resourceId + time
    private String id;
    private String name;
    private String establishmentYear;
    private String address;
    private int deskCount;

    /**
     * Constructs a new Library object with the specified parameters.
     *
     * @param id                The unique identifier for the library.
     * @param name              The name of the library.
     * @param establishmentYear The year the library was established.
     * @param deskCount         The number of desks in the library.
     * @param address           The address of the library.
     */
    public Library(String id, String name, String establishmentYear, int deskCount, String address) {
        this.resourceList = new HashSet<>();
        this.id = id;
        this.name = name;
        this.establishmentYear = establishmentYear;
        this.deskCount = deskCount;
        this.address = address;
        this.borrowLog = new HashSet<>();
    }

    /**
     * Finds and returns the Library object with the given ID.
     *
     * @param id The ID of the library to find.
     * @return The Library object if found, or null if not found.
     */
    public static Library libraryFinder(String id) {
        return libraryList.get(id);
    }

    public static String addLibrary(String id, String name, String establishmentYear, int deskCount, String address) {
        if (libraryList.containsKey(id)) {
            return "duplicate-id";
        }
        Library library = new Library(id, name, establishmentYear, deskCount, address);
        libraryList.put(id,library);
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
            for (Resource resource : library.resourceList) {
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
        for (Resource resource : resourceList) {
            if (resource.getId().equals(id)) {
                return resource;
            }
        }
        return null;
    }

    public String removeResource(String id) {
        Iterator<Resource> it = resourceList.iterator();
        while (it.hasNext()) {
            Resource resource = it.next();
            if (resource.getId().equals(id)) {
                if (resource instanceof Borrowable && ((Borrowable) resource).getBorrowCount() != 0) {
                    return "not-allowed";
                }
                it.remove();
                return "success";
            }
        }
        return "not-found";
    }

    public String addResource(Resource resource) {
        if (resourceFinder(resource.getId()) != null) {
            return "duplicate-id";
        }
        if (Category.categoryFinder(resource.getCategory()) == null) {
            return "not-found";
        }

        resourceList.add(resource);
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

        for (String[] borrowDetail : borrowLog) {
            if (borrowDetail[0].equals(user.getId()) && borrowDetail[2].equals(resource.getId())) {
                return "not-allowed";
            }
        }

        borrowableResource.setBorrowCount(borrowableResource.getBorrowCount() + 1);
        String[] borrowDetail = new String[3];
        borrowDetail[0] = user.getId();
        borrowDetail[1] = resource.getId();
        borrowDetail[2] = time;
        borrowLog.add(borrowDetail);
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
        String[] borrowDetail = new String[3];

        boolean includesResource = false;

        for (String[] detail : borrowLog) {
            if (detail[0].equals(user.getId()) && detail[1].equals(resource.getId())) {
                borrowDetail = detail;
                includesResource = true;
                break;
            }
        }
        if (!includesResource) {
            return "not-found";
        }
        Borrowable borrowableResource = (Borrowable) resource;

        Iterator<String[]> it = borrowLog.iterator();
        while (it.hasNext()) {
            String[] log = it.next();

            if (log[0].equals(user.getId()) && log[1].equals(resource.getId())) {
                it.remove();
                break;
            }
        }

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

        for (String[] Log : treasureBook.getReadLog()) {
            if (haveConflict(startTime, endTime, Log[0], Log[1])) {
                return "not-allowed";
            }
        }

        String[] log = new String[2];
        log[0] = startTime;
        log[1] = endTime;
        treasureBook.getReadLog().add(log);
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

        for (Resource resource : resourceList) {
            switch (resource.getType()) {
                case "Book":
                    bookCount += resource.getCopyCount() - ((Borrowable) resource).getBorrowCount();
                    borrowedBookCount += ((Borrowable) resource).getBorrowCount();
                    break;
                case "Thesis":
                    thesisCount += resource.getCopyCount() - ((Borrowable) resource).getBorrowCount();
                    borrowedThesisCount += ((Borrowable) resource).getBorrowCount();
                    break;
                case "SellingBook":
                    sellingBookCount += resource.getCopyCount();
                    break;
                case "TreasureBook":
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

        for (Resource resource : resourceList) {
            Category resourceCategory = Category.categoryFinder(resource.getCategory());
            while (resourceCategory != null) {
                if (resourceCategory.getId().equals(categoryId)) {
                    switch (resource.getType()) {
                        case "Book":
                            bookCount += resource.getCopyCount() - ((Borrowable) resource).getBorrowCount();
                            break;
                        case "Thesis":
                            thesisCount += resource.getCopyCount() - ((Borrowable) resource).getBorrowCount();
                            break;
                        case "SellingBook":
                            sellingBookCount += resource.getCopyCount();
                            break;
                        case "TreasureBook":
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

        for (String[] log : borrowLog) {
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

        for (Resource resource : resourceList) {
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

        for (Resource resource : resourceList) {
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
    // A static HashSet to store the list of users
    private static HashSet<User> userList = new HashSet<>();

    private String id;
    private String password;
    private String firstName;
    private String lastName;
    private String nationalCode;
    private String birthYear;
    private String address;
    private int penalty;

    private ArrayList<String> borrowList; // resource ids that user has borrowed

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

    // getters
    public static HashSet<User> getUserList() {
        return userList;
    }

    /**
     * Searches for a user by their ID in the list of users.
     *
     * @param id The unique identifier for the user.
     * @return The User object if found, otherwise null.
     */
    public static User findUser(String id) {
        for (User user : userList) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Adds a new user to the list if the ID is not a duplicate and not the admin's
     * username.
     *
     * @param user The User object to be added.
     * @return "duplicate-id" if the ID is already in use or is the admin's
     * username, "success" otherwise.
     */
    public static String addUser(User user) {
        if (findUser(user.getId()) != null || user.getId().equals(Admin.getUsername())) {
            return "duplicate-id";
        }

        userList.add(user);
        return "success";
    }

    /**
     * Removes a user from the list by their ID if they have no penalties and no
     * borrowed items.
     *
     * @param id The unique identifier for the user to be removed.
     * @return "not-allowed" if the user has penalties or borrowed items, "success"
     * if the user is removed, "not-found" if the user does not exist.
     */
    public static String removeUser(String id) {
        Iterator<User> person = userList.iterator();
        while (person.hasNext()) {
            User user = person.next();
            if (user.id.equals(id)) {
                if (user.penalty != 0 || !user.borrowList.isEmpty()) {
                    return "not-allowed";
                } else {
                    userList.remove(user);
                    return "success";
                }
            }
        }
        return "not-found";
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

        for (User user : userList) {
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
        for (User user : userList) {
            penalty += user.getPenalty();
        }

        return penalty;
    }

    public ArrayList<String> getBorrowList() {
        return borrowList;
    }

    public int getBorrowLimit() {
        return 5;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String getAddress() {
        return address;
    }

    public int getPenalty() {
        return penalty;
    }

    // setter
    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

}

class Student extends User {
    public Student(String id, String password, String firstName, String lastName, String nationalCode, String birthYear, String address) {
        super(id, password, firstName, lastName, nationalCode, birthYear, address);
    }

    public int getBorrowLimit() {
        return 3;
    }
}

class Staff extends User {
    public Staff(String id, String password, String firstName, String lastName, String nationalCode, String birthYear, String address) {
        super(id, password, firstName, lastName, nationalCode, birthYear, address);
    }
}

class Master extends User {
    public Master(String id, String password, String firstName, String lastName, String nationalCode, String birthYear, String address) {
        super(id, password, firstName, lastName, nationalCode, birthYear, address);
    }
}

class Manager extends User {
    private String libraryId;

    public Manager(String id, String password, String firstName, String lastName, String nationalCode, String birthYear, String address, String libraryId) {
        super(id, password, firstName, lastName, nationalCode, birthYear, address);
        this.libraryId = libraryId;
    }

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
    // A static HashSet to store the list of categories
    private static HashSet<Category> categoryList = new HashSet<Category>();

    static {
        // resources that doesn't have any category are in this group
        // we will always have a category of "null"
        categoryList.add(new Category("null", "null", null));
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

        for (Category category : categoryList) {
            if (category.id.equals(id)) {
                return category;
            }
        }
        return null;
    }

    /**
     * Adds a new category to the category list.
     */
    public static String addCategory(String id, String name, String parentCategoryId) {
        if (categoryFinder(id) != null) {
            return "duplicate-id";
        }
        if (parentCategoryId != null && categoryFinder(parentCategoryId) == null) {
            return "not-found";
        }
        Category category = new Category(id, name, parentCategoryId);
        categoryList.add(category);
        return "success";
    }

    // Getters
    public HashSet<Category> getCategoryList() {
        return categoryList;
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
 */
class Resource {

    private String id;
    private String title;
    private String author;
    private String category;
    private int copyCount;
    private HashSet<String> comments;

    /**
     * @param id
     * @param title
     * @param author
     * @param copyCount
     * @param category
     * @constructor
     */
    public Resource(String id, String title, String author, int copyCount, String category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.copyCount = copyCount;
        this.category = category;
        this.comments = new HashSet<>();
    }

    public String getType() {
        return "resourceType";
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getCopyCount() {
        return copyCount;
    }

    // setters
    public void setCopyCount(int copyCount) {
        this.copyCount = copyCount;
    }

    public String getCategory() {
        return category;
    }

    /**
     * Adds a comment to the resource.
     *
     * @param comment The comment to add.
     */
    public void addComment(String comment) {
        comments.add(comment);
    }

    /**
     * Checks if the resource's title matches the given search phrase.
     *
     * @param searchPhrase The phrase to search for within the title.
     * @return true if the title contains the search phrase, false otherwise.
     */
    public boolean matchesSearch(String searchPhrase) {
        searchPhrase = searchPhrase.toLowerCase();
        return this.title.toLowerCase().contains(searchPhrase);
    }

}

class Book extends Resource implements Borrowable {
    public int borrowCount;
    private String publisher;
    private String publishYear;
    private long borrowTime;
    private int totalBorrowCount;

    public Book(String id, String title, String author, int copyCount, String publisher, String publishYear, String category) {
        super(id, title, author, copyCount, category);
        this.publisher = publisher;
        this.publishYear = publishYear;
        borrowTime = 0L;
        totalBorrowCount = 0;
    }

    public long getBorrowTime() {
        return borrowTime;
    }

    public void increaseBorrowTime(long minutes) {
        borrowTime += minutes;
    }

    public int getTotalBorrowCount() {
        return totalBorrowCount;
    }

    public void increaseTotalBorrowCount() {
        totalBorrowCount++;
    }

    @Override
    public String getType() {
        return "Book";
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishYear() {
        return publishYear;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }

    @Override
    public boolean matchesSearch(String searchPhrase) {
        searchPhrase = searchPhrase.toLowerCase();

        return this.getTitle().toLowerCase().contains(searchPhrase) || this.getAuthor().toLowerCase().contains(searchPhrase) || this.getPublisher().toLowerCase().contains(searchPhrase);
    }

}

class Thesis extends Resource implements Borrowable {
    private String master;
    private String defenseYear;
    private int borrowCount;
    private long borrowTime;
    private int totalBorrowCount;

    public Thesis(String id, String title, String author, String master, String defenseYear, String category) {
        super(id, title, author, 1, category);
        this.master = master;
        this.defenseYear = defenseYear;
        borrowTime = 0L;
        totalBorrowCount = 0;
    }

    public long getBorrowTime() {
        return borrowTime;
    }

    public void increaseBorrowTime(long minutes) {
        borrowTime += minutes;
    }

    public int getTotalBorrowCount() {
        return totalBorrowCount;
    }

    public void increaseTotalBorrowCount() {
        totalBorrowCount++;
    }

    @Override
    public String getType() {
        return "Thesis";
    }

    public String getMaster() {
        return master;
    }

    public String getDefenseYear() {
        return defenseYear;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }

    @Override
    public boolean matchesSearch(String searchPhrase) {
        searchPhrase = searchPhrase.toLowerCase();

        return this.getTitle().toLowerCase().contains(searchPhrase) || this.getAuthor().toLowerCase().contains(searchPhrase) || this.getMaster().toLowerCase().contains(searchPhrase);
    }

}

class SellingBook extends Resource {
    private int price;
    private int off;
    private int sellCount;
    private int totalSell;

    public SellingBook(String id, String title, String author, int copyCount, int price, int off, String category) {
        super(id, title, author, copyCount, category);
        this.price = price;
        this.off = off;
        sellCount = 0;
        totalSell = 0;
    }

    public int getFinalPrice() {
        double discount = (100.0 - off) / 100.0;
        double discountedPrice = price * discount;
        return (int) Math.floor(discountedPrice);
    }

    public int getSellCount() {
        return this.sellCount;
    }

    public int getTotalSell() {
        return totalSell;
    }

    public void increaseSellCount() {
        sellCount += getFinalPrice();
        totalSell++;
    }

    @Override
    public String getType() {
        return "SellingBook";
    }

    public int getPrice() {
        return price;
    }

    public int getOff() {
        return off;
    }

    @Override
    public boolean matchesSearch(String searchPhrase) {
        searchPhrase = searchPhrase.toLowerCase();

        return this.getTitle().toLowerCase().contains(searchPhrase) || this.getAuthor().toLowerCase().contains(searchPhrase);
    }

}

class TreasureBook extends Resource {
    private String publisher;
    private String publishYear;
    private String donator;

    private HashSet<String[]> readLog;

    public TreasureBook(String id, String title, String author, String publisher, String publishYear, String donator, String category) {
        super(id, title, author, 1, category);
        this.publishYear = publishYear;
        this.publisher = publisher;
        this.donator = donator;
        this.readLog = new HashSet<>();
    }

    @Override
    public String getType() {
        return "TreasureBook";
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishYear() {
        return publishYear;
    }

    public String getDonator() {
        return donator;
    }

    public HashSet<String[]> getReadLog() {
        return readLog;
    }

    @Override
    public boolean matchesSearch(String searchPhrase) {
        searchPhrase = searchPhrase.toLowerCase();

        return this.getTitle().toLowerCase().contains(searchPhrase) || this.getAuthor().toLowerCase().contains(searchPhrase) || this.getPublisher().toLowerCase().contains(searchPhrase);
    }
}