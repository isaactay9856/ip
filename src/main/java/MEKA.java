import java.util.ArrayList;
import java.util.Scanner;

/**
 * A simple chatbot that stores tasks, lists them, and exits when the user says goodbye.
 */
public class MEKA {
    private static final String NUMBER_REQUIRED_MESSAGE =
            "The following command requires a number to proceed.";
    private static final String DESCRIPTION_REQUIRED_MESSAGE =
            "The following command requires a task description to proceed.";
    private static final String INVALID_TASK_NUMBER_MESSAGE =
            "The task number does not exist in the list.";
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "I do not understand this command. Please input a valid command.";

    public static void main(String[] args) {
        String banner = "███╗   ███╗███████╗██╗  ██╗ █████╗\n"
                + "████╗ ████║██╔════╝██║ ██╔╝██╔══██╗\n"
                + "██╔████╔██║█████╗  █████╔╝ ███████║\n"
                + "██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║\n"
                + "██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║\n"
                + "╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝\n";

        String separator = "____________________________________________________________";
        ArrayList<Task> tasks = new ArrayList<>();

        System.out.println(separator);
        System.out.println(banner);
        System.out.println(" Hello! I'm MEKA.");
        System.out.println(" What can I do for you?");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

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
                    Task task = tasks.get(taskNumber - 1);
                    task.markAsDone();
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + task);

                } else if (isCommand(command, "unmark")) {
                    int taskNumber = parseTaskNumber(command, "unmark");
                    Task task = tasks.get(taskNumber - 1);
                    task.unmark();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + task);

                } else if (isCommand(command, "delete")) {
                    int taskNumber = parseTaskNumber(command, "delete");
                    Task task = tasks.remove(taskNumber - 1);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + task);
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");

                } else if (isCommand(command, "todo")) {
                    String description = parseDescription(command, "todo");
                    Task task = new Todo(description);
                    tasks.add(task);
                    printTaskAdded(task, tasks.size());

                } else if (isCommand(command, "deadline")) {
                    int byIndex = command.indexOf(" /by ");
                    String description = byIndex < 0
                            ? parseDescription(command, "deadline")
                            : command.substring("deadline".length(), byIndex).trim();
                    requireDescription(description);
                    if (byIndex < 0) {
                        throw new MekaException(UNKNOWN_COMMAND_MESSAGE);
                    }
                    String by = command.substring(byIndex + " /by ".length()).trim();
                    Task task = new Deadline(description, by);
                    tasks.add(task);
                    printTaskAdded(task, tasks.size());

                } else if (isCommand(command, "event")) {
                    int fromIndex = command.indexOf(" /from ");
                    int toIndex = fromIndex < 0 ? -1 : command.indexOf(" /to ", fromIndex);
                    String description = fromIndex < 0
                            ? parseDescription(command, "event")
                            : command.substring("event".length(), fromIndex).trim();
                    requireDescription(description);
                    if (fromIndex < 0 || toIndex < 0) {
                        throw new MekaException(UNKNOWN_COMMAND_MESSAGE);
                    }
                    String from = command.substring(fromIndex + " /from ".length(), toIndex).trim();
                    String to = command.substring(toIndex + " /to ".length()).trim();
                    Task task = new Event(description, from, to);
                    tasks.add(task);
                    printTaskAdded(task, tasks.size());

                } else {
                    throw new MekaException(UNKNOWN_COMMAND_MESSAGE);
                }
            } catch (MekaException exception) {
                System.out.println(" " + exception.getMessage());
            } catch (NumberFormatException exception) {
                System.out.println(" " + NUMBER_REQUIRED_MESSAGE);
            } catch (IndexOutOfBoundsException exception) {
                System.out.println(" " + INVALID_TASK_NUMBER_MESSAGE);
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
     * Extracts a task number from a mark or unmark command.
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
        if (description.isEmpty()) {
            throw new MekaException(DESCRIPTION_REQUIRED_MESSAGE);
        }
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
