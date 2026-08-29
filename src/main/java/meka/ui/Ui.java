package meka.ui;

import java.util.Scanner;

import meka.task.Task;
import meka.task.TaskList;

/**
 * Handles all console input and output for MEKA.
 */
public class Ui {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final String BANNER = "███╗   ███╗███████╗██╗  ██╗ █████╗\n"
            + "████╗ ████║██╔════╝██║ ██╔╝██╔══██╗\n"
            + "██╔████╔██║█████╗  █████╔╝ ███████║\n"
            + "██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║\n"
            + "██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║\n"
            + "╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝\n";
    private static final String LOAD_ERROR_MESSAGE =
            "I could not load the saved tasks, so I started with an empty task list.";
    private static final String SAVE_ERROR_MESSAGE =
            "I could not save the task list. Your changes are available only for this session.";

    /** Source of commands entered by the user. */
    private final Scanner scanner;

    /**
     * Creates a console user interface that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available to read.
     *
     * @return true when standard input has another line.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command and removes surrounding whitespace.
     *
     * @return command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Shows MEKA's banner and greeting.
     */
    public void showWelcome() {
        showLine();
        System.out.println(BANNER);
        System.out.println(" Hello! I'm MEKA.");
        System.out.println(" What can I do for you?");
    }

    /**
     * Shows the message displayed when MEKA exits.
     */
    public void showGoodbye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    /**
     * Shows a horizontal divider between interactions.
     */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /**
     * Shows every task with its one-based task number.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        int taskNumber = 1;
        for (Task task : tasks) {
            System.out.println(" " + taskNumber + ". " + task);
            taskNumber++;
        }
    }

    /**
     * Shows search results with consecutive task numbers.
     *
     * @param matches Matching tasks in their original order.
     */
    public void showMatchingTasks(TaskList matches) {
        System.out.println(" Here are the matching tasks in your list:");
        showTaskList(matches);
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task task that was added.
     * @param taskCount current number of tasks.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows confirmation that a task was marked as done.
     *
     * @param task task that was marked.
     */
    public void showTaskMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    /**
     * Shows confirmation that a task was marked as not done.
     *
     * @param task task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks remaining.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows an error caused by an invalid command.
     *
     * @param message explanation to show the user.
     */
    public void showError(String message) {
        System.out.println(" " + message);
    }

    /**
     * Warns that saved tasks could not be loaded.
     */
    public void showLoadingError() {
        showError(LOAD_ERROR_MESSAGE);
    }

    /**
     * Warns that changes could not be saved.
     */
    public void showSavingError() {
        showError(SAVE_ERROR_MESSAGE);
    }
}
