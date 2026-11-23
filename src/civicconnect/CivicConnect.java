
package civicconnect;

import java.util.Scanner;

public class CivicConnect {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("       WELCOME TO CIVICCONNECT");
        System.out.println(" Civic Issue Reporting System");
        System.out.println("==================================");

        boolean running = true;

        while (running) {
            displayMainMenu();

            int choice = readInt("Enter your choice: ");

            switch (choice) {

                case 1 -> System.out.println("Citizen Management selected.");

                case 2 -> System.out.println("Issue Management selected.");

                case 3 -> System.out.println("Category Management selected.");

                case 4 -> System.out.println("Location Management selected.");

                case 5 -> System.out.println("Department Management selected.");

                case 6 -> System.out.println("Employee Management selected.");

                case 7 -> System.out.println("Assignment Management selected.");

                case 8 -> System.out.println("Status Update Management selected.");

                case 9 -> System.out.println("Feedback Management selected.");

                case 10 -> System.out.println("Reports and Statistics selected.");

                case 0 -> {
                    running = false;
                    System.out.println("Thank you for using CivicConnect!");
                }

                default -> System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private static void displayMainMenu() {

        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1.  Citizen Management");
        System.out.println("2.  Issue Management");
        System.out.println("3.  Category Management");
        System.out.println("4.  Location Management");
        System.out.println("5.  Department Management");
        System.out.println("6.  Employee Management");
        System.out.println("7.  Assignment Management");
        System.out.println("8.  Status Update Management");
        System.out.println("9.  Feedback Management");
        System.out.println("10. Reports and Statistics");
        System.out.println("0.  Exit");
        System.out.println("===============================");
    }

    private static int readInt(String message) {

        while (true) {
            System.out.print(message);

            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
