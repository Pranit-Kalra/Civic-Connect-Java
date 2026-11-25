package admin;

import admin.DataStore.Category;
import admin.DataStore.Department;
import admin.Issue.InvalidComplaintException;
import admin.Issue.StatusUpdate;
import citizen.Citizen;
import citizen.Feedback;
import resolver.Employee;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * ============================================================================
 * CLASS: AdminModule
 * ============================================================================
 * Central municipal authority console for managing citizens, categories,
 * departments, field resolvers, issue assignment, status monitoring, and statistics.
 * 
 * Demonstrates:
 *   - Polymorphism (profile display via User references)
 *   - Robust Exception Handling (custom InvalidComplaintException, safeInt)
 *   - Collections handling (HashMap, ArrayList, HashSet)
 */
public class AdminModule {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void handleLogin(Scanner sc, DataStore ds) {
        System.out.println("\n--------------------------------------------------------------");
        System.out.println(">>> MUNICIPAL ADMINISTRATOR LOGIN");
        System.out.println("--------------------------------------------------------------");
        System.out.println("  Default Predefined Credentials:");
        System.out.println("  Email   : admin@civicconnect.gov");
        System.out.println("  Password: Admin@123\n");

        System.out.print("  Admin Email   : ");
        String email = sc.nextLine().trim();
        System.out.print("  Admin Password: ");
        String pass = sc.nextLine().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            System.out.println("  [!] Email and password fields cannot be blank.");
            return;
        }

        AdminUser admin = ds.getAdminUser();
        if (admin.getEmail().equalsIgnoreCase(email) && admin.getPassword().equals(pass)) {
            System.out.println("  [+] Authentication successful! Welcome, " + admin.getName() + " (" + admin.getRole() + ")!");
            showDashboard(sc, ds, admin);
        } else {
            System.out.println("  [!] Invalid administrator credentials. Access denied.");
        }
    }

    private static void showDashboard(Scanner sc, DataStore ds, AdminUser admin) {
        boolean active = true;
        while (active) {
            System.out.println("\n==============================================================");
            System.out.println("  MUNICIPAL ADMIN CONTROL DESK | " + admin.getName() + " (" + admin.getRole() + ")");
            System.out.println("==============================================================");
            System.out.println("  [1]  View Citizens / Search by ID");
            System.out.println("  [2]  View / Search / Filter Issues");
            System.out.println("  [3]  Manage Categories (Add, Edit, View, Delete)");
            System.out.println("  [4]  Manage Departments (Add, Edit, View)");
            System.out.println("  [5]  Manage Resolvers (Register, Assign Dept, Toggle Active)");
            System.out.println("  [6]  Assign Issue to Resolver");
            System.out.println("  [7]  Monitor Status & Audit Trail");
            System.out.println("  [8]  Reject Invalid / Out-of-Scope Report");
            System.out.println("  [9]  View Citizen Feedback");
            System.out.println("  [10] System Statistics & Performance");
            System.out.println("  [11] Logout to Main Menu");
            System.out.println("--------------------------------------------------------------");
            System.out.print("Enter choice [1-11]: ");

            String input = sc.nextLine().trim();
            switch (input) {
                case "1": viewCitizens(sc, ds); break;
                case "2": viewIssues(sc, ds); break;
                case "3": manageCategories(sc, ds); break;
                case "4": manageDepartments(sc, ds); break;
                case "5": manageResolvers(sc, ds); break;
                case "6": assignIssue(sc, ds, admin); break;
                case "7": monitorHistory(sc, ds); break;
                case "8": rejectIssue(sc, ds, admin); break;
                case "9": viewFeedback(ds); break;
                case "10": viewStats(ds); break;
                case "11": active = false; System.out.println("  [i] Administrator logged out."); break;
                default: System.out.println("  [!] Invalid choice. Please enter a number between 1 and 11.");
            }
        }
    }

    private static void viewCitizens(Scanner sc, DataStore ds) {
        System.out.println("\n--- [ CITIZEN DIRECTORY ] ---");
        System.out.println("  [1] List All Citizens");
        System.out.println("  [2] Search Citizen by ID");
        System.out.print("  Choice [1-2]: ");
        String c = sc.nextLine().trim();

        if ("1".equals(c)) {
            System.out.println("  ---------------------------------------------------------------------------------------------------");
            System.out.printf("  %-10s | %-20s | %-25s | %-12s | %-20s%n", "ID", "Name", "Email", "Phone", "Address");
            System.out.println("  ---------------------------------------------------------------------------------------------------");
            for (Citizen cit : ds.getAllCitizens()) {
                System.out.printf("  %-10s | %-20s | %-25s | %-12s | %-20s%n",
                        cit.getId(), truncate(cit.getName(), 20), truncate(cit.getEmail(), 25), cit.getPhone(), truncate(cit.getAddress(), 20));
            }
            System.out.println("  ---------------------------------------------------------------------------------------------------");
        } else if ("2".equals(c)) {
            System.out.print("  Enter Citizen ID: ");
            String id = sc.nextLine().trim();
            Citizen cit = ds.getCitizenById(id);
            if (cit != null) {
                // Runtime Polymorphism: call displayProfile on User subclass
                cit.displayProfile();
            } else {
                System.out.println("  [!] Citizen with ID '" + id + "' was not found.");
            }
        } else {
            System.out.println("  [!] Invalid choice.");
        }
    }

    private static void viewIssues(Scanner sc, DataStore ds) {
        System.out.println("\n--- [ ISSUES REPOSITORY ] ---");
        System.out.println("  [1] View All");
        System.out.println("  [2] Search by Issue ID");
        System.out.println("  [3] Filter by Category");
        System.out.println("  [4] Filter by Status");
        System.out.print("  Choice [1-4]: ");
        String c = sc.nextLine().trim();

        List<Issue> all = ds.getAllIssues();
        List<Issue> results = new ArrayList<>();

        try {
            if ("1".equals(c)) {
                results = all;
            } else if ("2".equals(c)) {
                System.out.print("  Enter Issue ID (e.g. ISS-1001): ");
                String id = sc.nextLine().trim();
                if (id.isEmpty()) {
                    throw new InvalidComplaintException("Issue ID cannot be empty.");
                }
                Issue issue = ds.getIssueById(id);
                if (issue != null) {
                    results.add(issue);
                } else {
                    throw new InvalidComplaintException("No issue found with ID: " + id);
                }
            } else if ("3".equals(c)) {
                List<Category> cats = ds.getAllCategories();
                for (int i = 0; i < cats.size(); i++) {
                    System.out.printf("    [%d] %s%n", i + 1, cats.get(i).getName());
                }
                System.out.print("  Select Category: ");
                int ci = safeInt(sc, 1, cats.size());
                String catId = cats.get(ci - 1).getId();
                for (Issue i : all) {
                    if (i.getCategoryId().equalsIgnoreCase(catId)) results.add(i);
                }
            } else if ("4".equals(c)) {
                String[] statuses = {"Reported", "Under Review", "Assigned", "In Progress", "Resolved", "Rejected"};
                for (int i = 0; i < statuses.length; i++) {
                    System.out.printf("    [%d] %s%n", i + 1, statuses[i]);
                }
                System.out.print("  Select Status: ");
                int si = safeInt(sc, 1, statuses.length);
                String st = statuses[si - 1];
                for (Issue i : all) {
                    if (i.getStatus().equalsIgnoreCase(st)) results.add(i);
                }
            } else {
                System.out.println("  [!] Invalid choice.");
                return;
            }

            if (results.isEmpty()) {
                System.out.println("  No issues found matching criteria.");
            } else {
                printIssueTable(results, ds);
            }
        } catch (InvalidComplaintException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }

    private static void manageCategories(Scanner sc, DataStore ds) {
        System.out.println("\n--- [ MANAGE CATEGORIES ] ---");
        System.out.println("  [1] View All");
        System.out.println("  [2] Add Category");
        System.out.println("  [3] Delete Category");
        System.out.print("  Choice [1-3]: ");
        String c = sc.nextLine().trim();

        if ("1".equals(c)) {
            for (Category cat : ds.getAllCategories()) {
                System.out.printf("  [%s] %-25s | Priority: %-8s | %s%n", cat.getId(), cat.getName(), cat.getPriority(), cat.getDescription());
            }
        } else if ("2".equals(c)) {
            System.out.print("  Category Name: ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) { System.out.println("  [!] Name cannot be blank."); return; }

            System.out.print("  Description  : ");
            String desc = sc.nextLine().trim();
            System.out.print("  Priority (Low/Medium/High/Critical): ");
            String p = sc.nextLine().trim();
            if (p.isEmpty()) p = "Medium";

            Category cat = new Category(ds.nextCategoryId(), name, desc, p);
            if (ds.addCategory(cat)) {
                System.out.println("  [+] Category added successfully: " + cat.getId());
            } else {
                System.out.println("  [!] Category name already exists.");
            }
        } else if ("3".equals(c)) {
            System.out.print("  Enter Category ID to delete: ");
            String id = sc.nextLine().trim();
            if (ds.deleteCategory(id)) {
                System.out.println("  [+] Category deleted successfully.");
            } else {
                System.out.println("  [!] Cannot delete: Category is actively referenced by existing issues or not found.");
            }
        } else {
            System.out.println("  [!] Invalid choice.");
        }
    }

    private static void manageDepartments(Scanner sc, DataStore ds) {
        System.out.println("\n--- [ MANAGE DEPARTMENTS ] ---");
        System.out.println("  [1] View All Departments");
        System.out.println("  [2] Add New Department");
        System.out.print("  Choice [1-2]: ");
        String c = sc.nextLine().trim();

        if ("1".equals(c)) {
            for (Department d : ds.getAllDepartments()) {
                System.out.printf("  [%s] %-30s | Phone: %-15s | Address: %s%n", d.getId(), d.getName(), d.getContactNumber(), d.getOfficeAddress());
            }
        } else if ("2".equals(c)) {
            System.out.print("  Department Name : ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) { System.out.println("  [!] Name cannot be empty."); return; }
            System.out.print("  Contact Number  : ");
            String phone = sc.nextLine().trim();
            System.out.print("  Office Address  : ");
            String addr = sc.nextLine().trim();
            System.out.print("  Scope/Desc      : ");
            String desc = sc.nextLine().trim();

            Department dept = new Department(ds.nextDepartmentId(), name, phone, addr, desc);
            if (ds.addDepartment(dept)) {
                System.out.println("  [+] Department added successfully: " + dept.getId());
            } else {
                System.out.println("  [!] Department already exists.");
            }
        } else {
            System.out.println("  [!] Invalid choice.");
        }
    }

    private static void manageResolvers(Scanner sc, DataStore ds) {
        System.out.println("\n--- [ MANAGE RESOLVERS (FIELD EMPLOYEES) ] ---");
        System.out.println("  [1] List All Resolvers");
        System.out.println("  [2] Register New Resolver");
        System.out.println("  [3] Toggle Active/Inactive Status");
        System.out.print("  Choice [1-3]: ");
        String c = sc.nextLine().trim();

        if ("1".equals(c)) {
            System.out.println("  ---------------------------------------------------------------------------------------------------");
            System.out.printf("  %-10s | %-20s | %-24s | %-10s | %-20s | %-8s%n", "ID", "Name", "Email", "Dept ID", "Designation", "Active");
            System.out.println("  ---------------------------------------------------------------------------------------------------");
            for (Employee e : ds.getAllResolvers()) {
                System.out.printf("  %-10s | %-20s | %-24s | %-10s | %-20s | %-8s%n",
                        e.getId(), truncate(e.getName(), 20), truncate(e.getEmail(), 24), e.getDepartmentId(), truncate(e.getDesignation(), 20), e.isActive() ? "YES" : "NO");
            }
            System.out.println("  ---------------------------------------------------------------------------------------------------");
        } else if ("2".equals(c)) {
            System.out.print("  Full Name       : ");
            String name = sc.nextLine().trim();
            System.out.print("  Official Email  : ");
            String email = sc.nextLine().trim();
            System.out.print("  Contact Phone   : ");
            String phone = sc.nextLine().trim();
            System.out.print("  Designation     : ");
            String desig = sc.nextLine().trim();
            System.out.print("  Initial Password: ");
            String pass = sc.nextLine().trim();

            if (name.isEmpty() || email.isEmpty() || pass.length() < 6) {
                System.out.println("  [!] Validation failed: Required fields cannot be empty and password must be >= 6 chars.");
                return;
            }

            List<Department> depts = ds.getAllDepartments();
            for (int i = 0; i < depts.size(); i++) {
                System.out.printf("    [%d] %s (%s)%n", i + 1, depts.get(i).getName(), depts.get(i).getId());
            }
            System.out.print("  Select Department: ");
            int dIdx = safeInt(sc, 1, depts.size());
            String deptId = depts.get(dIdx - 1).getId();

            Employee emp = new Employee(ds.nextResolverId(), name, email, phone, pass, desig, deptId, LocalDate.now(), true);
            if (ds.addResolver(emp)) {
                System.out.println("  [+] Resolver registered successfully: " + emp.getId());
            } else {
                System.out.println("  [!] Email is already in use.");
            }
        } else if ("3".equals(c)) {
            System.out.print("  Enter Resolver ID: ");
            String id = sc.nextLine().trim();
            Employee e = ds.getResolverById(id);
            if (e != null) {
                e.setActive(!e.isActive());
                System.out.println("  [+] Status toggled to: " + (e.isActive() ? "ACTIVE" : "INACTIVE"));
            } else {
                System.out.println("  [!] Resolver not found.");
            }
        } else {
            System.out.println("  [!] Invalid choice.");
        }
    }

    private static void assignIssue(Scanner sc, DataStore ds, AdminUser admin) {
        System.out.println("\n--- [ ASSIGN ISSUE TO RESOLVER ] ---");
        System.out.print("  Enter Issue ID (e.g. ISS-1005): ");
        String issueId = sc.nextLine().trim();

        try {
            if (issueId.isEmpty()) {
                throw new InvalidComplaintException("Issue ID cannot be empty.");
            }

            Issue issue = ds.getIssueById(issueId);
            if (issue == null) {
                throw new InvalidComplaintException("Issue not found with ID: " + issueId);
            }

            if ("Resolved".equalsIgnoreCase(issue.getStatus()) || "Rejected".equalsIgnoreCase(issue.getStatus())) {
                throw new InvalidComplaintException("Cannot assign: This issue is already closed (" + issue.getStatus() + ").");
            }

            System.out.println("  Issue: " + issue.getTitle() + " | Current Status: " + issue.getStatus());
            System.out.println("  Select Department:");
            List<Department> depts = ds.getAllDepartments();
            for (int i = 0; i < depts.size(); i++) {
                System.out.printf("    [%d] %s%n", i + 1, depts.get(i).getName());
            }
            System.out.print("  Choose Department: ");
            int dChoice = safeInt(sc, 1, depts.size());
            Department dept = depts.get(dChoice - 1);

            List<Employee> resolvers = ds.getActiveResolversByDepartment(dept.getId());
            if (resolvers.isEmpty()) {
                System.out.println("  [!] No ACTIVE resolvers found in department '" + dept.getName() + "'.");
                return;
            }

            System.out.println("  Available Resolvers in " + dept.getName() + ":");
            for (int i = 0; i < resolvers.size(); i++) {
                System.out.printf("    [%d] %s (%s) - %s%n", i + 1, resolvers.get(i).getName(), resolvers.get(i).getId(), resolvers.get(i).getDesignation());
            }
            System.out.print("  Select Resolver: ");
            int rChoice = safeInt(sc, 1, resolvers.size());
            Employee selected = resolvers.get(rChoice - 1);

            System.out.print("  Assignment Directives / Instructions: ");
            String notes = sc.nextLine().trim();

            if (ds.assignIssue(issue.getId(), selected.getId(), notes, admin.getName())) {
                System.out.println("  [+] Issue " + issue.getId() + " assigned to " + selected.getName() + " successfully!");
            }
        } catch (InvalidComplaintException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }

    private static void monitorHistory(Scanner sc, DataStore ds) {
        System.out.print("\n  Enter Issue ID: ");
        String id = sc.nextLine().trim();

        try {
            if (id.isEmpty()) {
                throw new InvalidComplaintException("Issue ID cannot be empty.");
            }

            Issue issue = ds.getIssueById(id);
            if (issue == null) {
                throw new InvalidComplaintException("Issue not found with ID: " + id);
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
        } catch (InvalidComplaintException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }

    private static void rejectIssue(Scanner sc, DataStore ds, AdminUser admin) {
        System.out.println("\n--- [ REJECT ISSUE REPORT ] ---");
        System.out.print("  Enter Issue ID to Reject: ");
        String id = sc.nextLine().trim();

        try {
            if (id.isEmpty()) {
                throw new InvalidComplaintException("Issue ID cannot be empty.");
            }

            Issue issue = ds.getIssueById(id);
            if (issue == null) {
                throw new InvalidComplaintException("Issue not found with ID: " + id);
            }

            if ("Resolved".equalsIgnoreCase(issue.getStatus())) {
                throw new InvalidComplaintException("Cannot reject an issue that is already marked RESOLVED.");
            }

            System.out.print("  Reason for rejection (e.g. Out of municipal scope, duplicate): ");
            String reason = sc.nextLine().trim();
            if (reason.isEmpty()) {
                System.out.println("  [!] Rejection reason cannot be empty.");
                return;
            }

            if (ds.updateIssueStatus(issue.getId(), "Rejected", reason, "Admin", admin.getName())) {
                System.out.println("  [+] Issue " + issue.getId() + " marked as REJECTED.");
            }
        } catch (InvalidComplaintException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }

    private static void viewFeedback(DataStore ds) {
        System.out.println("\n--- [ CITIZEN SATISFACTION FEEDBACK ] ---");
        List<Feedback> list = ds.getAllFeedbacks();
        if (list.isEmpty()) {
            System.out.println("  No feedback recorded yet.");
            return;
        }
        System.out.println("  ---------------------------------------------------------------------------------------------------");
        System.out.printf("  %-10s | %-10s | %-10s | %-8s | %-16s | %-30s%n", "ID", "Issue ID", "Citizen ID", "Rating", "Date", "Comments");
        System.out.println("  ---------------------------------------------------------------------------------------------------");
        for (Feedback f : list) {
            System.out.printf("  %-10s | %-10s | %-10s | %-8s | %-16s | %-30s%n",
                    f.getId(), f.getIssueId(), f.getCitizenId(), f.getRating() + "/5", f.getDate().format(DTF), truncate(f.getComments(), 30));
        }
        System.out.println("  ---------------------------------------------------------------------------------------------------");
    }

    private static void viewStats(DataStore ds) {
        System.out.println("\n==============================================================");
        System.out.println("  SYSTEM STATISTICS & PERFORMANCE");
        System.out.println("==============================================================");
        Map<String, Object> stats = ds.calculateSystemStatistics();
        System.out.println("  Total Citizens Registered : " + stats.get("totalCitizens"));
        System.out.println("  Total Resolvers Employed  : " + stats.get("totalResolvers") + " (" + stats.get("activeResolvers") + " Active)");
        System.out.println("  Municipal Departments     : " + stats.get("totalDepartments"));
        System.out.println("  Issue Categories          : " + stats.get("totalCategories"));
        System.out.println("  ------------------------------------------------------------");
        System.out.println("  TOTAL ISSUES REPORTED     : " + stats.get("totalIssues"));
        System.out.println("    - Reported (New)        : " + stats.get("statusReported"));
        System.out.println("    - Under Review          : " + stats.get("statusUnderReview"));
        System.out.println("    - Assigned to Resolver  : " + stats.get("statusAssigned"));
        System.out.println("    - In Progress (Field)   : " + stats.get("statusInProgress"));
        System.out.println("    - Resolved (Closed)     : " + stats.get("statusResolved"));
        System.out.println("    - Rejected (Out of Scope: " + stats.get("statusRejected"));
        System.out.println("  ------------------------------------------------------------");
        System.out.println("  Total Feedback Responses  : " + stats.get("totalFeedbacks"));
        System.out.printf("  Average Citizen Rating    : %.2f / 5.0%n", (double) stats.get("avgRating"));
        System.out.println("==============================================================");
    }

    private static void printIssueTable(List<Issue> list, DataStore ds) {
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
                System.out.print("  [!] Number out of range. Enter a number between " + min + " and " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("  [!] Invalid numeric format. Please enter a valid number [" + min + "-" + max + "]: ");
            }
        }
    }
}
