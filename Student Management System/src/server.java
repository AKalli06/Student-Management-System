import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class server {

    private static volatile boolean socketRunning = false;
    private static ServerSocket serverSocketRef;
    private static final List<Socket> connectedClients = new ArrayList<>();

    public static class Student {
        String id;
        String fullName;
        int yearOfStudy;
        double avgGrade;
    }

    public static Student[] arr = new Student[1000];
    public static int n;

    // MAIN METHOD
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        n = systemServices.loadStudents(arr);
        runMenu(input);
    }

    // CLIENT MENU (socket)
    private static void sendMenu(PrintWriter out) {
        out.println("=== STUDENT ANALYTICS MENU ===");
        out.println("1. Show overall statistics");
        out.println("2. Show students by year of study");
        out.println("3. Search students by name substring");
        out.println("4. Sort students");
        out.println("5. Export failing students to file");
        out.println("6. Add student to database");
        out.println("7. Exit");
        out.println("");   // END OF MENU BLOCK
    }

    // LOCAL MENU (console)
    public static void localMenu() {
        System.out.println("=== STUDENT ANALYTICS MENU ===");
        System.out.println("1. Show overall statistics");
        System.out.println("2. Show students by year of study");
        System.out.println("3. Search students by name substring");
        System.out.println("4. Sort students");
        System.out.println("5. Export failing students to file");
        System.out.println("6. Add student to database");
        System.out.println("7. Start remote access");
        System.out.println("8. End remote access");
        System.out.println("9. Exit");
    }

    // CONSOLE MENU
    public static void runMenu(Scanner input) {
        int option = 0;

        while (option != 9) {
            localMenu();

            try {
                System.out.print("Choose an option: ");
                option = input.nextInt();
            } catch (Exception e) {
                System.out.println("Invalid input.");
                input.nextLine();
                continue;
            }

            switch (option) {
                case 1:
                    System.out.print(systemServices.overallStatistics(n, arr));
                    break;

                case 2:
                    System.out.println("Enter year of study (1-4): ");
                    String year = input.next();
                    System.out.print(systemServices.getStudentsByYearAsString(n, arr, year));
                    break;

                case 3:
                    System.out.println("Enter name substring: ");
                    input.nextLine();
                    String subname = input.nextLine();
                    System.out.print(systemServices.getStudentsByNameAsString(n, arr, subname));
                    break;

                case 4:
                    System.out.println("1.Ascending\n2.Descending\n3.Alphabetically\nChoose the order:");
                    String order = input.next();
                    System.out.print(systemServices.getStudentsSortedAsString(n, arr, order));
                    break;

                case 5:
                    systemServices.exportFailingStudents(n, arr, true, new PrintWriter(System.out, true));
                    break;

                case 6:
                    Student s = new Student();
                    input.nextLine();
                    System.out.print("Enter ID: ");
                    s.id = input.nextLine();
                    System.out.print("Enter full name: ");
                    s.fullName = input.nextLine();
                    System.out.print("Enter year of study: ");
                    s.yearOfStudy = input.nextInt();
                    System.out.print("Enter average grade: ");
                    s.avgGrade = input.nextDouble();
                    systemServices.addStudentToFile(s, true, new PrintWriter(System.out, true));
                    n = systemServices.loadStudents(arr);
                    break;

                case 7:
                    startSocketServer();
                    break;

                case 8:
                    stopSocketServer();
                    break;

                case 9:
                    System.out.println("Goodbye :(");
                    break;

                default:
                    System.out.println("This option not implemented in console yet.");
                    break;
            }
        }
    }

    // SOCKET MENU
    public static void runMenu(BufferedReader in, PrintWriter out) throws Exception {
        while (true) {

            sendMenu(out);

            String request = in.readLine();
            if (request == null) return;

            int choice;
            try {
                choice = Integer.parseInt(request);
            } catch (Exception e) {
                out.println("Invalid input.");
                out.println("");
                continue;
            }

            switch (choice) {

                case 1:
                    out.print(systemServices.overallStatistics(n, arr));
                    out.println("");
                    break;

                case 2:
                    out.println("Enter year of study (1-4):");
                    out.println("");
                    String yearStr = in.readLine();
                    out.print(systemServices.getStudentsByYearAsString(n, arr, yearStr));
                    out.println("");
                    break;

                case 3:
                    out.println("Enter name substring:");
                    out.println("");
                    String substr = in.readLine();
                    out.print(systemServices.getStudentsByNameAsString(n, arr, substr));
                    out.println("");
                    break;

                case 4:
                    out.println("1.Ascending\n2.Descending\n3.Alphabetically\nChoose the order:");
                    out.println("");
                    String order = in.readLine();
                    out.print(systemServices.getStudentsSortedAsString(n, arr, order));
                    out.println("");
                    break;

                case 5:
                    systemServices.exportFailingStudents(n, arr, false, out);
                    break;

                case 6:
                    Student s = new Student();

                    out.println("Enter ID:");
                    out.println("");
                    s.id = in.readLine();

                    out.println("Enter full name:");
                    out.println("");
                    s.fullName = in.readLine();

                    out.println("Enter year of study:");
                    out.println("");
                    s.yearOfStudy = Integer.parseInt(in.readLine());

                    out.println("Enter average grade:");
                    out.println("");
                    s.avgGrade = Double.parseDouble(in.readLine());

                    systemServices.addStudentToFile(s, false, out);
                    n = systemServices.loadStudents(arr);
                    break;

                case 7:
                    out.println("Goodbye Client.");
                    out.println("");
                    return;

                default:
                    out.println("Option not implemented over yet.");
                    out.println("");
                    break;
            }
        }
    }

    // START SOCKET SERVER
    public static void startSocketServer() {
        socketRunning = true;

        new Thread(() -> {
            try {
                int socketPort=50;
                serverSocketRef = new ServerSocket(socketPort);
                while (socketRunning) {
                    Socket client = serverSocketRef.accept();
                    connectedClients.add(client);

                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);

                    String line;
                    System.out.println();
                    while (!(line = in.readLine()).isEmpty()) System.out.println(line);
                    runMenu(in, out);
                    while (!(line = in.readLine()).isEmpty()) System.out.println(line);

                    connectedClients.remove(client);
                    client.close();
                }
            } catch (Exception ignored) {}
        }).start();
    }

    // STOP SOCKET SERVER
    public static void stopSocketServer() {
        socketRunning = false;
        try {
            for (Socket s : connectedClients)
                try {
                    s.close();
                } catch (Exception ignored) {}
            serverSocketRef.close();
        } catch (Exception ignored) {}
    }
}