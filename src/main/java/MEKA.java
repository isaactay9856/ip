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
        String[] tasks = new String[100];
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
            } else {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println(" added: " + command);
            }
            System.out.println(separator);
        }
    }
}
