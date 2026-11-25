package resolver;

import admin.DataStore;
import admin.DataStore.Category;
import admin.DataStore.Department;
import admin.Issue;
import admin.Issue.InvalidComplaintException;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ============================================================================
 * CLASS: ResolverModule
 * ============================================================================
 * Field officer workbench for inspecting assigned complaints, updating status,
 * logging on-site progress remarks, and viewing assignment history.
 * 
 * Demonstrates:
 *   - Polymorphism (Profile display through User superclass method overloading/overriding)
 *   - Robust Exception Handling (InvalidComplaintException, safeInt)
 *   - Collections usage (List, Map lookups)
 */
public class ResolverModule {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void handleLogin(Scanner sc, DataStore ds) {
        System.out.println("\n--------------------------------------------------------------");
        System.out.println(">>> RESOLVER (FIELD OFFICER) LOGIN");
        System.out.println("--------------------------------------------------------------");
        System.out.println("  Sample Logins:");
        System.out.println("  - rajesh.resolver@civic.gov / Pass@1001 (Roads)");
        System.out.println("  - amit.resolver@civic.gov   / Pass@1003 (Water)\n");

        System.out.print("  Enter Official Email: ");
        String email = sc.nextLine().trim();
        System.out.print("  Enter Password      : ");
        String pass = sc.nextLine().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            System.out.println("  [!] Email and password fields cannot be empty.");
            return;
        }

        Employee emp = ds.findResolverByEmail(email);
        if (emp != null && emp.getPassword().equals(pass)) {
            if (!emp.isActive()) {
                System.out.println("  [!] Your resolver account is currently INACTIVE. Contact Administrator.");
                return;
            }
            System.out.println("  [+] Authentication successful! Welcome, " + emp.getName() + " (" + emp.getRole() + ")!");
            showDashboard(sc, ds, emp);
        } else {
            System.out.println("  [!] Invalid resolver credentials. Access denied.");
        }
    }

    private static void showDashboard(Scanner sc, DataStore ds, Employee resolver) {
        boolean active = true;
        while (active) {
            System.out.println("\n==============================================================");
            System.out.println("  RESOLVER WORKBENCH | " + resolver.getName() + " (" + resolver.getId() + ")");
            System.out.println("==============================================================");
            System.out.println("  [1] View My Assigned Complaints");
            System.out.println("  [2] View Complaint Details & Site Evidence");
            System.out.println("  [3] Update Complaint Status (e.g. In Progress, Resolved)");
            System.out.println("  [4] Add On-Site Remarks / Progress Notes");
            System.out.println("  [5] View Complete Assignment History");
            System.out.println("  [6] View Completed Complaints");
            System.out.println("  [7] View / Update Profile");
            System.out.println("  [8] Logout to Main Menu");
            System.out.println("--------------------------------------------------------------");
            System.out.print("Enter choice [1-8]: ");

            String input = sc.nextLine().trim();
            switch (input) {
                case "1": viewAssigned(ds, resolver); break;
                case "2": viewDetails(sc, ds, resolver); break;
                case "3": updateStatus(sc, ds, resolver); break;
                case "4": addRemark(sc, ds, resolver); break;
                case "5": viewHistory(ds, resolver); break;
                case "6": viewCompleted(ds, resolver); break;
                case "7": viewProfile(sc, ds, resolver); break;
                case "8": active = false; System.out.println("  [i] Resolver logged out."); break;
                default: System.out.println("  [!] Invalid choice. Please enter a number between 1 and 8.");
            }
        }
    }

    private static void viewAssigned(DataStore ds, Employee resolver) {
        System.out.println("\n--- [ MY ASSIGNED COMPLAINTS ] ---");
        List<Issue> list = ds.getIssuesByResolver(resolver.getId());
        if (list.isEmpty()) {
            System.out.println("  No complaints currently assigned to your queue.");
            return;
        }
        printTable(list, ds);
    }

    private static void viewDetails(Scanner sc, DataStore ds, Employee resolver) {
        System.out.print("\n  Enter Complaint/Issue ID: ");
        String id = sc.nextLine().trim();

        try {
            if (id.isEmpty()) {
                throw new InvalidComplaintException("Complaint ID cannot be empty.");
            }

            Issue issue = ds.getIssueById(id);
            if (issue == null || !isAssigned(ds, issue.getId(), resolver.getId())) {
                throw new InvalidComplaintException("Complaint '" + id + "' not found or is not assigned to your account.");
            }

            Category cat = ds.getCategoryById(issue.getCategoryId());
            Department dept = ds.getDepartmentById(issue.getDepartmentId());

            System.out.println("\n  ==================== COMPLAINT DETAILS ====================");
            System.out.println("  Complaint ID: " + issue.getId());
            System.out.println("  Title       : " + issue.getTitle());
            System.out.println("  Description : " + issue.getDescription());
            System.out.println("  Category    : " + (cat != null ? cat.getName() : "N/A"));
            System.out.println("  Status      : " + issue.getStatus());
            System.out.println("  Department  : " + (dept != null ? dept.getName() : "Unassigned"));
            System.out.println("  Location    : " + issue.getFormattedLocation());
            System.out.println("  Reported At : " + issue.getCreatedAt().format(DTF));
            System.out.println("  Photos      : " + issue.getEvidencePhotos());
            System.out.println("  ===========================================================");
        } catch (InvalidComplaintException e) {
            System.out.println("  [!] Access Error: " + e.getMessage());
        }
    }

    private static void updateStatus(Scanner sc, DataStore ds, Employee resolver) {
        System.out.println("\n--- [ UPDATE COMPLAINT STATUS ] ---");
        System.out.print("  Enter Assigned Complaint ID: ");
        String id = sc.nextLine().trim();

        try {
            if (id.isEmpty()) {
                throw new InvalidComplaintException("Complaint ID cannot be empty.");
            }

            Issue issue = ds.getIssueById(id);
            if (issue == null || !isAssigned(ds, issue.getId(), resolver.getId())) {
                throw new InvalidComplaintException("Complaint '" + id + "' not found or is not assigned to your account.");
            }

            System.out.println("  Complaint: " + issue.getTitle() + " | Current Status: " + issue.getStatus());
            String cur = issue.getStatus();
            if ("Resolved".equalsIgnoreCase(cur) || "Rejected".equalsIgnoreCase(cur)) {
                throw new InvalidComplaintException("This complaint is already closed (" + cur + ") and cannot be updated.");
            }

            String nextStatus = "";
            if ("Assigned".equalsIgnoreCase(cur)) {
                nextStatus = "In Progress";
            } else if ("In Progress".equalsIgnoreCase(cur)) {
                nextStatus = "Resolved";
            } else {
                throw new InvalidComplaintException("Current status is '" + cur + "'. Awaiting administrative assignment first.");
            }

            System.out.print("  Advance status to '" + nextStatus + "'? (y/N): ");
            if (!"y".equalsIgnoreCase(sc.nextLine().trim())) return;

            System.out.print("  Mandatory Work Remarks / Progress Details: ");
            String remarks = sc.nextLine().trim();
            if (remarks.isEmpty()) {
                System.out.println("  [!] Remarks cannot be empty.");
                return;
            }

            if (ds.updateIssueStatus(issue.getId(), nextStatus, remarks, "Resolver", resolver.getName())) {
                System.out.println("  [+] Complaint status successfully updated to: " + nextStatus);
            }
        } catch (InvalidComplaintException e) {
            System.out.println("  [!] Status Update Error: " + e.getMessage());
        }
    }

    private static void addRemark(Scanner sc, DataStore ds, Employee resolver) {
        System.out.print("\n  Enter Assigned Complaint ID: ");
        String id = sc.nextLine().trim();

        try {
            if (id.isEmpty()) {
                throw new InvalidComplaintException("Complaint ID cannot be empty.");
            }

            Issue issue = ds.getIssueById(id);
            if (issue == null || !isAssigned(ds, issue.getId(), resolver.getId())) {
                throw new InvalidComplaintException("Complaint '" + id + "' not found or is not assigned to your account.");
            }

            System.out.print("  Enter Site Remark / Inspection Note: ");
            String note = sc.nextLine().trim();
            if (note.isEmpty()) {
                System.out.println("  [!] Remark cannot be blank.");
                return;
            }

            ds.addRemark(issue.getId(), note, "Resolver", resolver.getName());
            System.out.println("  [+] Remark successfully appended to the audit trail.");
        } catch (InvalidComplaintException e) {
            System.out.println("  [!] Remark Logging Error: " + e.getMessage());
        }
    }

    private static void viewHistory(DataStore ds, Employee resolver) {
        System.out.println("\n--- [ MY ASSIGNMENT HISTORY ] ---");
        List<resolver.Assignment> list = ds.getAssignmentsForResolver(resolver.getId());
        if (list.isEmpty()) {
            System.out.println("  No assignment history found.");
            return;
        }

        System.out.println("  ---------------------------------------------------------------------------------------------------");
        System.out.printf("  %-10s | %-10s | %-16s | %-16s | %-25s%n", "Assign ID", "Issue ID", "Assigned On", "Completed On", "Instructions");
        System.out.println("  ---------------------------------------------------------------------------------------------------");
        for (resolver.Assignment a : list) {
            String comp = (a.getCompletionDate() != null) ? a.getCompletionDate().format(DTF) : "IN PROGRESS";
            System.out.printf("  %-10s | %-10s | %-16s | %-16s | %-25s%n",
                    a.getId(), a.getIssueId(), a.getAssignedAt().format(DTF), comp, a.getNotes());
        }
        System.out.println("  ---------------------------------------------------------------------------------------------------");
    }

    private static void viewCompleted(DataStore ds, Employee resolver) {
        System.out.println("\n--- [ MY COMPLETED COMPLAINTS ] ---");
        List<Issue> list = ds.getIssuesByResolver(resolver.getId());
        List<Issue> comp = new ArrayList<>();
        for (Issue i : list) {
            if ("Resolved".equalsIgnoreCase(i.getStatus())) comp.add(i);
        }
        if (comp.isEmpty()) {
            System.out.println("  No completed complaints yet.");
            return;
        }
        printTable(comp, ds);
    }

    private static void viewProfile(Scanner sc, DataStore ds, Employee resolver) {
        Department dept = ds.getDepartmentById(resolver.getDepartmentId());
        String dName = (dept != null) ? dept.getName() : "Unassigned";

        // Runtime Polymorphism: Overloaded displayProfile with department context
        resolver.displayProfile(dName);

        System.out.print("  Update phone number? (y/N): ");
        if ("y".equalsIgnoreCase(sc.nextLine().trim())) {
            System.out.print("  New 10-digit Phone: ");
            String p = sc.nextLine().trim();
            if (p.matches("^[0-9]{10}$")) {
                resolver.setPhone(p);
                System.out.println("  [+] Phone updated.");
            } else {
                System.out.println("  [!] Invalid phone number format.");
            }
        }
    }

    private static boolean isAssigned(DataStore ds, String issueId, String resolverId) {
        for (resolver.Assignment a : ds.getAssignmentsForIssue(issueId)) {
            if (a.getEmployeeId().equalsIgnoreCase(resolverId)) return true;
        }
        return false;
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

    private static String truncate(String s, int max) {
        if (s == null) return "N/A";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}
