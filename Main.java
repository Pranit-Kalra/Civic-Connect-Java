import admin.AdminModule;
import admin.DataStore;
import citizen.CitizenModule;
import resolver.ResolverModule;

import java.util.Scanner;

/**
 * Main: Entry point for CivicConnect Civic Issue Management System.
 * Connects citizens with municipal authorities through three modular subsystems:
 *   - citizen/
 *   - resolver/
 *   - admin/
 */
public class Main {
    public static void main(String[] args) {
        DataStore ds = DataStore.getInstance();
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n==============================================================");
            System.out.println("                  C I V I C C O N N E C T                     ");
            System.out.println("     Municipal Issue Reporting & Resolution Management Portal ");
            System.out.println("==============================================================");
            System.out.println("  [1] Citizen Login");
            System.out.println("  [2] Citizen Registration (New User)");
            System.out.println("  [3] Municipal Admin Login");
            System.out.println("  [4] Resolver (Municipal Employee) Login");
            System.out.println("  [5] Exit Application");
            System.out.println("==============================================================");
            System.out.print("Enter choice [1-5]: ");

            String input = sc.nextLine().trim();
            switch (input) {
                case "1":
                    CitizenModule.handleLogin(sc, ds);
                    break;
                case "2":
                    CitizenModule.handleRegistration(sc, ds);
                    break;
                case "3":
                    AdminModule.handleLogin(sc, ds);
                    break;
                case "4":
                    ResolverModule.handleLogin(sc, ds);
                    break;
                case "5":
                    running = false;
                    System.out.println("\n  Thank you for using CivicConnect. Have a great day!\n");
                    break;
                default:
                    System.out.println("  [!] Invalid choice. Please enter a number between 1 and 5.");
            }
        }

        sc.close();
    }
}
