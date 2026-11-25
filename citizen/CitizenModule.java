package citizen;

import admin.Category;
import admin.DataStore;
import admin.Department;
import admin.Issue;
import admin.StatusUpdate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ============================================================================
 * CLASS: CitizenModule
 * ============================================================================
 * Handles citizen registration, issue reporting, status tracking, resolution
 * history review, feedback submission, and profile management.
 * Demonstrates: Exception handling, input validation, collections usage, and OOP.
 */
public class CitizenModule {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void handleRegistration(Scanner sc, DataStore ds) {
        System.out.println("\n--------------------------------------------------------------");
        System.out.println(">>> CITIZEN REGISTRATION");
        System.out.println("--------------------------------------------------------------");

        System.out.print("  Full Name          : ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) { 
            System.out.println("  [!] Name cannot be empty."); 
            return; 
        }

        System.out.print("  Email Address      : ");
        String email = sc.nextLine().trim();
        if (!email.contains("@") || !email.contains(".")) {
            System.out.println("  [!] Invalid email format (must contain '@' and domain)."); 
            return;
        }
        if (ds.findCitizenByEmail(email) != null) {
            System.out.println("  [!] An account with this email already exists."); 
            return;
        }

        System.out.print("  10-Digit Phone     : ");
        String phone = sc.nextLine().trim();
        if (!phone.matches("^[0-9]{10}$")) {
            System.out.println("  [!] Phone number must consist of exactly 10 digits."); 
            return;
        }

        System.out.print("  Residential Address: ");
        String address = sc.nextLine().trim();
        if (address.isEmpty()) { 
            System.out.println("  [!] Address cannot be empty."); 
            return; 
        }

        System.out.print("  Password (min 6)   : ");
        String pass = sc.nextLine().trim();
        if (pass.length() < 6) {
            System.out.println("  [!] Password must be at least 6 characters."); 
            return;
        }

        String id = ds.nextCitizenId();
        Citizen cit = new Citizen(id, name, email, phone, address, pass, LocalDateTime.now());
        if (ds.addCitizen(cit)) {
            System.out.println("  [+] REGISTRATION SUCCESSFUL!");
            System.out.println("  Assigned Citizen ID: " + id);
            System.out.println("  You can now log in using your registered email and password.");
        }
    }

    public static void handleLogin(Scanner sc, DataStore ds) {
        System.out.println("\n--------------------------------------------------------------");
        System.out.println(">>> CITIZEN LOGIN");
        System.out.println("--------------------------------------------------------------");
        System.out.print("  Enter Registered Email: ");
        String email = sc.nextLine().trim();
        System.out.print("  Enter Password        : ");
        String pass = sc.nextLine().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            System.out.println("  [!] Email and password fields cannot be empty.");
            return;
        }

        Citizen citizen = ds.findCitizenByEmail(email);
        if (citizen != null && citizen.getPassword().equals(pass)) {
            System.out.println("  [+] Authentication successful! Welcome, " + citizen.getName() + " (" + citizen.getRole() + ")!");
            showDashboard(sc, ds, citizen);
        } else {
            System.out.println("  [!] Invalid email or password. Access denied.");
        }
    }

    private static void showDashboard(Scanner sc, DataStore ds, Citizen citizen) {
        boolean active = true;
        while (active) {
            System.out.println("\n==============================================================");
            System.out.println("  CITIZEN DASHBOARD | " + citizen.getName() + " (" + citizen.getId() + ")");
            System.out.println("==============================================================");
            System.out.println("  [1] Report New Civic Complaint / Issue");
            System.out.println("  [2] View My Reported Issues");
            System.out.println("  [3] Track Specific Issue Status");
            System.out.println("  [4] View Issue Details & Evidence");
            System.out.println("  [5] View Resolution History & Audit Log");
            System.out.println("  [6] Submit Feedback (Resolved Issues Only)");
            System.out.println("  [7] View / Update Profile");
            System.out.println("  [8] Logout to Main Menu");
            System.out.println("--------------------------------------------------------------");
            System.out.print("Enter choice [1-8]: ");

            String input = sc.nextLine().trim();
            switch (input) {
                case "1": reportIssue(sc, ds, citizen); break;
                case "2": viewMyIssues(ds, citizen); break;
                case "3": trackIssue(sc, ds, citizen); break;
                case "4": viewDetails(sc, ds, citizen); break;
                case "5": viewHistory(sc, ds, citizen); break;
                case "6": submitFeedback(sc, ds, citizen); break;
                case "7": viewProfile(sc, citizen); break;
                case "8": active = false; System.out.println("  [i] Citizen logged out."); break;
                default: System.out.println("  [!] Invalid choice. Please enter a number between 1 and 8.");
            }
        }
    }

    private static void reportIssue(Scanner sc, DataStore ds, Citizen citizen) {
        System.out.println("\n--- [ REPORT NEW CIVIC COMPLAINT ] ---");
        System.out.print("  Complaint Title     : ");
        String title = sc.nextLine().trim();

        System.out.print("  Description         : ");
        String desc = sc.nextLine().trim();

        System.out.println("  Select Category:");
        List<Category> cats = ds.getAllCategories();
        for (int i = 0; i < cats.size(); i++) {
            System.out.printf("    [%d] %s (Priority: %s)%n", i + 1, cats.get(i).getName(), cats.get(i).getPriority());
        }
        System.out.print("  Choose Category [1-" + cats.size() + "]: ");
        int cIdx = safeInt(sc, 1, cats.size());
        Category cat = cats.get(cIdx - 1);

        System.out.print("  Area / Neighborhood : ");
        String area = sc.nextLine().trim();
        System.out.print("  City                : ");
        String city = sc.nextLine().trim();
        System.out.print("  State               : ");
        String state = sc.nextLine().trim();
        System.out.print("  Nearby Landmark     : ");
        String landmark = sc.nextLine().trim();

        System.out.print("  Evidence Photos (comma-separated filenames): ");
        String photosInput = sc.nextLine().trim();
        List<String> photos = new ArrayList<>();
        if (!photosInput.isEmpty()) {
            for (String p : photosInput.split(",")) {
                if (!p.trim().isEmpty()) photos.add(p.trim());
            }
        } else {
            photos.add("photo_evidence.jpg");
        }

        String issueId = ds.nextIssueId();
        Issue issue = new Issue(issueId, citizen.getId(), title, desc, cat.getId(), area, city, state, landmark, photos, LocalDateTime.now());

        if (title.isEmpty() || desc.isEmpty() || area.isEmpty() || city.isEmpty()) {
            System.out.println("  [!] Complaint Validation Failed: Mandatory fields (Title, Description, Area, City) cannot be blank.");
            return;
        }

        ds.addIssue(issue, citizen.getName());
        System.out.println("  [+] Complaint successfully lodged! Tracking ID: " + issueId);
    }

    private static void viewMyIssues(DataStore ds, Citizen citizen) {
        System.out.println("\n--- [ MY REPORTED COMPLAINTS ] ---");
        List<Issue> list = ds.getIssuesByCitizen(citizen.getId());
        if (list.isEmpty()) {
            System.out.println("  You have not reported any complaints yet.");
            return;
        }
        printTable(list, ds);
    }

    private static void trackIssue(Scanner sc, DataStore ds, Citizen citizen) {
        System.out.print("\n  Enter Complaint/Issue ID (e.g. ISS-1001): ");
        String id = sc.nextLine().trim();

        Issue issue = ds.getIssueById(id);
        if (issue == null || !issue.getCitizenId().equalsIgnoreCase(citizen.getId())) {
            System.out.println("  [!] Tracking Error: Complaint '" + id + "' not found under your account.");
            return;
        }

        System.out.println("\n  +--------------------------------------------------------------+");
        System.out.printf("  | Issue ID   : %-48s|%n", issue.getId());
        System.out.printf("  | Title      : %-48s|%n", truncate(issue.getTitle(), 48));
        System.out.printf("  | Status     : %-48s|%n", issue.getStatus());
        System.out.printf("  | Reported On: %-48s|%n", issue.getCreatedAt().format(DTF));
        System.out.printf("  | Location   : %-48s|%n", truncate(issue.getFormattedLocation(), 48));
        System.out.println("  +--------------------------------------------------------------+");

        printLifecycle(issue.getStatus());
    }

    private static void viewDetails(Scanner sc, DataStore ds, Citizen citizen) {
        System.out.print("\n  Enter Complaint/Issue ID: ");
        String id = sc.nextLine().trim();

        Issue issue = ds.getIssueById(id);
        if (issue == null || !issue.getCitizenId().equalsIgnoreCase(citizen.getId())) {
            System.out.println("  [!] Inquiry Error: Complaint '" + id + "' not found under your account.");
            return;
        }

        Category cat = ds.getCategoryById(issue.getCategoryId());
        Department dept = ds.getDepartmentById(issue.getDepartmentId());
        String deptName = (dept != null) ? dept.getName() : "Pending Administrative Allocation";
        Feedback fb = ds.getFeedbackForIssue(issue.getId());

        System.out.println("\n  ==================== COMPLAINT DETAILS ====================");
        System.out.println("  Complaint ID: " + issue.getId());
        System.out.println("  Title       : " + issue.getTitle());
        System.out.println("  Description : " + issue.getDescription());
        System.out.println("  Category    : " + (cat != null ? cat.getName() : "N/A"));
        System.out.println("  Status      : " + issue.getStatus());
        System.out.println("  Department  : " + deptName);
        System.out.println("  Location    : " + issue.getFormattedLocation());
        System.out.println("  Reported At : " + issue.getCreatedAt().format(DTF));
        System.out.println("  Resolved At : " + (issue.getResolvedAt() != null ? issue.getResolvedAt().format(DTF) : "Open"));
        System.out.println("  Photos      : " + issue.getEvidencePhotos());
        if (fb != null) {
            System.out.println("  Feedback    : Rating " + fb.getRating() + "/5 - " + fb.getComments());
        }
        System.out.println("  ===========================================================");
        printLifecycle(issue.getStatus());
    }

    private static void viewHistory(Scanner sc, DataStore ds, Citizen citizen) {
        System.out.print("\n  Enter Complaint/Issue ID: ");
        String id = sc.nextLine().trim();

        Issue issue = ds.getIssueById(id);
        if (issue == null || !issue.getCitizenId().equalsIgnoreCase(citizen.getId())) {
            System.out.println("  [!] History Error: Complaint '" + id + "' not found under your account.");
            return;
        }

        List<StatusUpdate> history = ds.getStatusUpdatesForIssue(issue.getId());
        System.out.println("\n  ================ AUDIT TRAIL : " + issue.getId() + " ================");
        for (int i = 0; i < history.size(); i++) {
            StatusUpdate su = history.get(i);
            String from = (su.getFromStatus() != null) ? su.getFromStatus() : "Lodged";
            System.out.printf("  [%d] %s | By: %s (%s)%n", i + 1, su.getTimestamp().format(DTF), su.getUpdatedByName(), su.getUpdatedByRole());
            System.out.printf("      %s ---> %s%n", from, su.getToStatus());
            System.out.printf("      Remarks: %s%n", su.getRemarks());
        }
        System.out.println("  =======================================================");
    }

    private static void submitFeedback(Scanner sc, DataStore ds, Citizen citizen) {
        System.out.println("\n--- [ SUBMIT RESOLUTION FEEDBACK ] ---");
        System.out.print("  Enter Resolved Complaint ID: ");
        String id = sc.nextLine().trim();

        Issue issue = ds.getIssueById(id);
        if (issue == null || !issue.getCitizenId().equalsIgnoreCase(citizen.getId())) {
            System.out.println("  [!] Feedback Submission Error: Complaint '" + id + "' not found under your account.");
            return;
        }
        if (!issue.isResolved()) {
            System.out.println("  [!] Feedback Submission Error: Feedback is only allowed after the complaint is RESOLVED. Current: " + issue.getStatus());
            return;
        }
        if (ds.getFeedbackForIssue(issue.getId()) != null) {
            System.out.println("  [!] Feedback Submission Error: Feedback has already been recorded for complaint " + id);
            return;
        }

        System.out.print("  Rating [1 to 5]: ");
        int rating = safeInt(sc, 1, 5);
        System.out.print("  Feedback Comments: ");
        String comments = sc.nextLine().trim();

        if (ds.submitFeedback(issue.getId(), citizen.getId(), rating, comments)) {
            System.out.println("  [+] Thank you! Your feedback has been recorded successfully.");
        }
    }

    private static void viewProfile(Scanner sc, Citizen citizen) {
        // Polymorphic profile display
        citizen.displayProfile();
        System.out.print("  Update phone or address? (y/N): ");
        if ("y".equalsIgnoreCase(sc.nextLine().trim())) {
            System.out.print("  New 10-digit Phone (Enter to keep current): ");
            String p = sc.nextLine().trim();
            if (p.matches("^[0-9]{10}$")) {
                citizen.setPhone(p);
                System.out.println("  [+] Phone updated.");
            }

            System.out.print("  New Address (Enter to keep current): ");
            String a = sc.nextLine().trim();
            if (!a.isEmpty()) {
                citizen.setAddress(a);
                System.out.println("  [+] Address updated.");
            }
        }
    }

    private static void printTable(List<Issue> list, DataStore ds) {
        System.out.println("  ---------------------------------------------------------------------------------------------------");
        System.out.printf("  %-10s | %-28s | %-20s | %-14s | %-16s%n", "ID", "Title", "Category", "Status", "Reported On");
        System.out.println("  ---------------------------------------------------------------------------------------------------");
        for (Issue i : list) {
            Category c = ds.getCategoryById(i.getCategoryId());
            String cName = (c != null) ? c.getName() : "N/A";
            System.out.printf("  %-10s | %-28s | %-20s | %-14s | %-16s%n",
                    i.getId(), truncate(i.getTitle(), 28), truncate(cName, 20), i.getStatus(), i.getCreatedAt().format(DTF));
        }
        System.out.println("  ---------------------------------------------------------------------------------------------------");
    }

    private static void printLifecycle(String status) {
        System.out.println("  Lifecycle: Reported -> Under Review -> Assigned -> In Progress -> Resolved");
        System.out.println("  Current Status: [" + status + "]\n");
    }

    private static String truncate(String s, int max) {
        if (s == null) return "N/A";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    private static int safeInt(Scanner sc, int min, int max) {
        while (true) {
            String line = sc.nextLine().trim();
            try {
                if (line.isEmpty()) {
                    System.out.print("  [!] Input cannot be blank. Enter a number between " + min + " and " + max + ": ");
                    continue;
                }
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) return val;
                System.out.print("  [!] Out of range. Enter a number between " + min + " and " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("  [!] Invalid numeric format. Please enter a valid number [" + min + "-" + max + "]: ");
            }
        }
    }
}
