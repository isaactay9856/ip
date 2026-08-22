import java.util.Scanner;

/**
 * A simple chatbot that stores tasks, lists them, and exits when the user says goodbye.
 */
public class MEKA {
    public static void main(String[] args) {
        String banner = "███╗   ███╗███████╗██╗  ██╗ █████╗\n"
                + "████╗ ████║██╔════╝██║ ██╔╝██╔══██╗\n"
                + "██╔████╔██║█████╗  █████╔╝ ███████║\n"
                + "██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║\n"
                + "██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║\n"
                + "╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝\n";

        String separator = "____________________________________________________________";
        Task[] tasks = new Task[100];
        int taskCount = 0;

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
            if (command.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }

            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5).trim());
                Task task = tasks[taskNumber - 1];
                task.markAsDone();
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + task);

            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7).trim());
                Task task = tasks[taskNumber - 1];
                task.unmark();
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + task);

            } else if (command.startsWith("todo ")) {
                String description = command.substring("todo ".length()).trim();
                Task task = new Todo(description);
                tasks[taskCount] = task;
                taskCount++;
                printTaskAdded(task, taskCount);

            } else if (command.startsWith("deadline ")) {
                int byIndex = command.indexOf(" /by ");
                String description = command.substring("deadline ".length(), byIndex).trim();
                String by = command.substring(byIndex + " /by ".length()).trim();
                Task task = new Deadline(description, by);
                tasks[taskCount] = task;
                taskCount++;
                printTaskAdded(task, taskCount);

            } else if (command.startsWith("event ")) {
                int fromIndex = command.indexOf(" /from ");
                int toIndex = command.indexOf(" /to ", fromIndex);
                String description = command.substring("event ".length(), fromIndex).trim();
                String from = command.substring(fromIndex + " /from ".length(), toIndex).trim();
                String to = command.substring(toIndex + " /to ".length()).trim();
                Task task = new Event(description, from, to);
                tasks[taskCount] = task;
                taskCount++;
                printTaskAdded(task, taskCount);

            } else {
                Task t = new Task(command);
                tasks[taskCount] = t;
                taskCount++;
                System.out.println(" added: " + command);
            }
            System.out.println(separator);
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
