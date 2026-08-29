import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * Console chatbot for creating, updating, deleting, and persistently storing
 * todo, deadline, and event tasks.
 */
public class MEKA {
    /** Location of the task data file relative to the working directory. */
    private static final Path DATA_FILE = Path.of("data", "meka.txt");

    /** Strict parser for user-entered date-times such as {@code 2/12/2019 1800}. */
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final String NUMBER_REQUIRED_MESSAGE =
            "The following command requires a number to proceed.";
    private static final String DESCRIPTION_REQUIRED_MESSAGE =
            "The following command requires a task description to proceed.";
    private static final String DATE_TIME_REQUIRED_MESSAGE =
            "The following command requires date/time details to proceed.";
    private static final String INVALID_DATE_TIME_MESSAGE =
            "Please enter date and time as d/M/yyyy HHmm "
                    + "(for example, 2/12/2019 1800).";
    private static final String INVALID_TASK_NUMBER_MESSAGE =
            "The task number does not exist in the list.";
    private static final String RESERVED_DELIMITER_MESSAGE =
            "Task details cannot contain \" | \" because it is reserved for saved data.";
    private static final String LOAD_ERROR_MESSAGE =
            "I could not load the saved tasks, so I started with an empty task list.";
    private static final String SAVE_ERROR_MESSAGE =
            "I could not save the task list. Your changes are available only for this session.";
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "I do not understand this command. Please input a valid command.";

    /**
     * Prevents construction because MEKA is started through {@link #main(String[])}.
     */
    private MEKA() {
    }

    /**
     * Starts MEKA, loads saved tasks, and processes commands until input ends
     * or the user enters {@code bye}.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        String banner = "███╗   ███╗███████╗██╗  ██╗ █████╗\n"
                + "████╗ ████║██╔════╝██║ ██╔╝██╔══██╗\n"
                + "██╔████╔██║█████╗  █████╔╝ ███████║\n"
                + "██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║\n"
                + "██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║\n"
                + "╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝\n";

        String separator = "____________________________________________________________";
        ArrayList<Task> tasks;
        boolean storageAvailable = true;
        try {
            tasks = loadTasks();
        } catch (DataFileException | IOException | SecurityException exception) {
            tasks = new ArrayList<>();
            storageAvailable = false;
        }

        System.out.println(separator);
        System.out.println(banner);
        System.out.println(" Hello! I'm MEKA.");
        System.out.println(" What can I do for you?");
        if (!storageAvailable) {
            System.out.println(" " + LOAD_ERROR_MESSAGE);
        }
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();

            if (command.equals("bye")) {
                System.out.println(separator);

                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            }

            System.out.println(separator);
            try {
                if (command.equals("list")) {
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + ". " + tasks.get(i));
                    }

                } else if (isCommand(command, "mark")) {
                    int taskNumber = parseTaskNumber(command, "mark");
                    Task task = getTask(tasks, taskNumber);
                    task.markAsDone();
                    saveTasks(tasks, storageAvailable);
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + task);

                } else if (isCommand(command, "unmark")) {
                    int taskNumber = parseTaskNumber(command, "unmark");
                    Task task = getTask(tasks, taskNumber);
                    task.unmark();
                    saveTasks(tasks, storageAvailable);
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + task);

                } else if (isCommand(command, "delete")) {
                    int taskNumber = parseTaskNumber(command, "delete");
                    getTask(tasks, taskNumber);
                    Task task = tasks.remove(taskNumber - 1);
                    saveTasks(tasks, storageAvailable);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + task);
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");

                } else if (isCommand(command, "todo")) {
                    String description = parseDescription(command, "todo");
                    Task task = new Todo(description);
                    tasks.add(task);
                    saveTasks(tasks, storageAvailable);
                    printTaskAdded(task, tasks.size());

                } else if (isCommand(command, "deadline")) {
                    int byIndex = findArgumentMarker(command, "by");
                    String description = byIndex < 0
                            ? parseDescription(command, "deadline")
                            : command.substring("deadline".length(), byIndex).trim();
                    requireDescription(description);
                    if (byIndex < 0) {
                        throw new MekaException(UNKNOWN_COMMAND_MESSAGE);
                    }
                    String by = command.substring(byIndex + " /by".length()).trim();
                    requireDateTime(by);
                    Task task = new Deadline(description, parseDateTime(by));
                    tasks.add(task);
                    saveTasks(tasks, storageAvailable);
                    printTaskAdded(task, tasks.size());

                } else if (isCommand(command, "event")) {
                    int fromIndex = findArgumentMarker(command, "from");
                    int toIndex = fromIndex < 0
                            ? -1
                            : findArgumentMarker(command, "to", fromIndex + " /from".length());
                    String description = fromIndex < 0
                            ? parseDescription(command, "event")
                            : command.substring("event".length(), fromIndex).trim();
                    requireDescription(description);
                    if (fromIndex < 0 || toIndex < 0) {
                        throw new MekaException(UNKNOWN_COMMAND_MESSAGE);
                    }
                    String from = command.substring(fromIndex + " /from".length(), toIndex).trim();
                    String to = command.substring(toIndex + " /to".length()).trim();
                    requireDateTime(from);
                    requireDateTime(to);
                    Task task = new Event(description,
                            parseDateTime(from), parseDateTime(to));
                    tasks.add(task);
                    saveTasks(tasks, storageAvailable);
                    printTaskAdded(task, tasks.size());

                } else {
                    throw new MekaException(UNKNOWN_COMMAND_MESSAGE);
                }
            } catch (MekaException exception) {
                System.out.println(" " + exception.getMessage());
            } catch (NumberFormatException exception) {
                System.out.println(" " + NUMBER_REQUIRED_MESSAGE);
            } catch (IOException | SecurityException exception) {
                storageAvailable = false;
                System.out.println(" " + SAVE_ERROR_MESSAGE);
            }
            System.out.println(separator);
        }
    }

    /**
     * Returns whether the input contains the given command word, optionally
     * followed by arguments.
     *
     * @param input complete user input
     * @param command command word to match
     * @return true if the input represents the command
     */
    private static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Finds a slash-prefixed argument marker such as {@code /by} or
     * {@code /from}.
     *
     * @param input complete user input
     * @param marker marker name without the slash
     * @return the marker's starting index, or -1 if it is not present
     */
    private static int findArgumentMarker(String input, String marker) {
        return findArgumentMarker(input, marker, 0);
    }

    /**
     * Finds a slash-prefixed argument marker at or after a given index.
     * A marker is accepted only when it ends the input or is followed by
     * whitespace, preventing text such as {@code /bye} from matching
     * {@code /by}.
     *
     * @param input complete user input
     * @param marker marker name without the slash
     * @param startIndex index at which to begin searching
     * @return the marker's starting index, or -1 if it is not present
     */
    private static int findArgumentMarker(String input, String marker, int startIndex) {
        String markerText = " /" + marker;
        int markerIndex = input.indexOf(markerText, startIndex);

        while (markerIndex >= 0) {
            int afterMarker = markerIndex + markerText.length();
            if (afterMarker == input.length()
                    || Character.isWhitespace(input.charAt(afterMarker))) {
                return markerIndex;
            }
            markerIndex = input.indexOf(markerText, markerIndex + 1);
        }
        return -1;
    }

    /**
     * Extracts a task number from a mark, unmark, or delete command.
     *
     * @param input complete user input
     * @param command command word at the start of the input
     * @return the supplied task number
     * @throws MekaException if no number was supplied
     * @throws NumberFormatException if the supplied argument is not a number
     */
    private static int parseTaskNumber(String input, String command) throws MekaException {
        String numberText = input.substring(command.length()).trim();
        if (numberText.isEmpty()) {
            throw new MekaException(NUMBER_REQUIRED_MESSAGE);
        }
        return Integer.parseInt(numberText);
    }

    /**
     * Returns an existing task selected by its one-based task number.
     *
     * @param tasks current task list
     * @param taskNumber one-based task number supplied by the user
     * @return the selected task
     * @throws MekaException if the number is outside the task list
     */
    private static Task getTask(ArrayList<Task> tasks, int taskNumber) throws MekaException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new MekaException(INVALID_TASK_NUMBER_MESSAGE);
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Extracts and validates a task description from a task creation command.
     *
     * @param input complete user input
     * @param command command word at the start of the input
     * @return the non-empty task description
     * @throws MekaException if the description is empty
     */
    private static String parseDescription(String input, String command) throws MekaException {
        String description = input.substring(command.length()).trim();
        requireDescription(description);
        return description;
    }

    /**
     * Ensures a task description contains visible characters.
     *
     * @param description task description to validate
     * @throws MekaException if the description is empty
     */
    private static void requireDescription(String description) throws MekaException {
        requireStorableText(description, DESCRIPTION_REQUIRED_MESSAGE);
    }

    /**
     * Ensures a date-time argument is present and can be saved safely.
     *
     * @param dateTime date and time text to validate
     * @throws MekaException if the value is empty or contains the file delimiter
     */
    private static void requireDateTime(String dateTime) throws MekaException {
        requireStorableText(dateTime, DATE_TIME_REQUIRED_MESSAGE);
    }

    /**
     * Parses a user-entered date and time in the {@code d/M/yyyy HHmm} format.
     *
     * @param dateTime date and time text supplied by the user
     * @return the parsed date and time
     * @throws MekaException if the text is not a valid date and time
     */
    private static LocalDateTime parseDateTime(String dateTime) throws MekaException {
        try {
            return LocalDateTime.parse(dateTime, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new MekaException(INVALID_DATE_TIME_MESSAGE);
        }
    }

    /**
     * Validates user-entered text before placing it in the pipe-separated file.
     *
     * @param text text to validate
     * @param emptyMessage message to use when the text is empty
     * @throws MekaException if the text is empty or contains the file delimiter
     */
    private static void requireStorableText(String text, String emptyMessage)
            throws MekaException {
        if (text.isBlank()) {
            throw new MekaException(emptyMessage);
        }
        if (text.contains(" | ")) {
            throw new MekaException(RESERVED_DELIMITER_MESSAGE);
        }
    }

    /**
     * Loads saved tasks from the application's data file.
     *
     * @return the saved tasks, or an empty list if the data file does not exist
     * @throws IOException if the data file cannot be read
     * @throws DataFileException if the data file contains an invalid record
     */
    private static ArrayList<Task> loadTasks() throws IOException, DataFileException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(DATA_FILE)) {
            return tasks;
        }

        int lineNumber = 0;
        for (String originalLine : Files.readAllLines(DATA_FILE)) {
            lineNumber++;
            String line = originalLine;
            if (lineNumber == 1 && line.startsWith("\uFEFF")) {
                line = line.substring(1);
            }
            if (!line.isBlank()) {
                tasks.add(parseSavedTask(line, lineNumber));
            }
        }
        return tasks;
    }

    /**
     * Converts one saved data line back into its corresponding task object,
     * including parsing saved deadline and event values as ISO date-times.
     *
     * @param line pipe-separated task data
     * @param lineNumber line number used to identify invalid data
     * @return the reconstructed task
     * @throws DataFileException if the line contains invalid task data
     */
    private static Task parseSavedTask(String line, int lineNumber)
            throws DataFileException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 1) {
            throw invalidData(lineNumber, "missing task type");
        }

        int expectedFieldCount;
        switch (fields[0]) {
        case "T":
            expectedFieldCount = 3;
            break;
        case "D":
            expectedFieldCount = 4;
            break;
        case "E":
            expectedFieldCount = 5;
            break;
        default:
            throw invalidData(lineNumber, "unknown task type");
        }

        if (fields.length != expectedFieldCount) {
            throw invalidData(lineNumber, "incorrect number of fields");
        }
        if (!fields[1].equals("0") && !fields[1].equals("1")) {
            throw invalidData(lineNumber, "invalid completion status");
        }
        for (int i = 2; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                throw invalidData(lineNumber, "empty task detail");
            }
        }

        Task task;

        try {
            switch (fields[0]) {
            case "T":
                task = new Todo(fields[2]);
                break;
            case "D":
                task = new Deadline(fields[2], LocalDateTime.parse(fields[3]));
                break;
            case "E":
                task = new Event(fields[2], LocalDateTime.parse(fields[3]),
                        LocalDateTime.parse(fields[4]));
                break;
            default:
                throw invalidData(lineNumber, "unknown task type");
            }
        } catch (DateTimeParseException exception) {
            throw invalidData(lineNumber, "invalid date and time");
        }

        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Creates a data-file exception that identifies the invalid line.
     *
     * @param lineNumber one-based line number
     * @param reason reason the record is invalid
     * @return an exception describing the invalid data
     */
    private static DataFileException invalidData(int lineNumber, String reason) {
        return new DataFileException("Invalid data on line " + lineNumber + ": " + reason);
    }

    /**
     * Saves all current tasks to the application's data file.
     *
     * @param tasks tasks to save
     * @param storageAvailable whether loading or an earlier save succeeded
     * @throws IOException if the data directory or file cannot be written
     */
    private static void saveTasks(ArrayList<Task> tasks, boolean storageAvailable)
            throws IOException {
        if (!storageAvailable) {
            throw new IOException("Task storage is unavailable");
        }
        Files.createDirectories(DATA_FILE.getParent());

        ArrayList<String> taskData = new ArrayList<>();
        for (Task task : tasks) {
            taskData.add(task.toDataString());
        }
        Files.write(DATA_FILE, taskData);
    }

    /**
     * Prints a confirmation after a task has been added to the list.
     *
     * @param task task that was added
     * @param taskCount current number of tasks in the list
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }
}
