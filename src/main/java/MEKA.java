import java.util.Scanner;

/**
 * A simple chatbot that echoes commands until the user says goodbye.
 */
public class MEKA {
    public static void main(String[] args) {
        String banner = "███╗   ███╗███████╗██╗  ██╗ █████╗\n"
                + "████╗ ████║██╔════╝██║ ██╔╝██╔══██╗\n"
                + "██╔████╔██║█████╗  █████╔╝ ███████║\n"
                + "██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║\n"
                + "██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║\n"
                + "╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝\n";

        String seperator = "____________________________________________________________";

        System.out.println(seperator);
        System.out.println(banner);
        System.out.println(" Hello! I'm MEKA.");
        System.out.println(" What can I do for you?");
        System.out.println(seperator);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            if (command.equals("bye")) {
                System.out.println(seperator);
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(seperator);
                break;
            }

            System.out.println(seperator);
            System.out.println(" " + command);
            System.out.println(seperator);
        }
    }
}
