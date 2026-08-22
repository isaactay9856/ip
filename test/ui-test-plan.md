# UI Test Plan

This file records console UI test cases for the project. Run test cases in the listed order and stop at the first failure.

## Program setup

- **Compile command:** `javac -encoding UTF-8 -d out src/main/java/*.java` using Java 25
- **Run command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp out MEKA` using Java 25
- **Comparison rules:** Exact text comparison, normalizing only CRLF/LF line endings unless stated otherwise.
- **Other setup:** Start with an empty in-memory task list. Capture standard output as UTF-8. No files are required.

## Test cases

### UI-01: Add and list all task types

- **Aim:** Verify that Todo, Deadline, and Event commands create correctly formatted tasks, update the task count, and appear in the list.
- **Inputs (in order):**

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

- **Expected output:**

```text
____________________________________________________________
███╗   ███╗███████╗██╗  ██╗ █████╗
████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
██╔████╔██║█████╗  █████╔╝ ███████║
██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

 Hello! I'm MEKA.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 1. [T][ ] borrow book
 2. [D][ ] return book (by: Sunday)
 3. [E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
