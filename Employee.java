package resolver;

import admin.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================================
 * CLASS: Employee (Subclass of User)
 * ============================================================================
 * Represents a municipal resolver / field officer assigned to resolve complaints.
 * 
 * Demonstrates:
 *   - Inheritance (extends User superclass)
 *   - Polymorphism (overrides getRole and displayProfile)
 *   - Method Overloading (displayProfile() vs displayProfile(String))
 *   - Encapsulation (private fields with getters and setters)
 */
public class Employee extends User {
    private String designation;
    private String departmentId;
    private LocalDate joiningDate;
    private boolean active;

    public Employee(String id, String name, String email, String phone, String password,
                    String designation, String departmentId, LocalDate joiningDate, boolean active) {
        super(id, name, email, phone, password);
        this.designation = designation;
        this.departmentId = departmentId;
        this.joiningDate = (joiningDate != null) ? joiningDate : LocalDate.now();
        this.active = active;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getRole() {
        return "Resolver";
    }

    @Override
    public void displayProfile() {
        displayProfile("Assigned Department");
    }

    /**
     * Overloaded displayProfile method showing department details.
     */
    public void displayProfile(String departmentName) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println("\n  --- [ RESOLVER PROFILE ] ---");
        System.out.println("  Employee ID : " + getId());
        System.out.println("  Name        : " + getName());
        System.out.println("  Email       : " + getEmail());
        System.out.println("  Phone       : " + getPhone());
        System.out.println("  Role        : " + getRole());
        System.out.println("  Designation : " + designation);
        System.out.println("  Department  : " + departmentName + " (" + departmentId + ")");
        System.out.println("  Joined On   : " + joiningDate.format(dtf));
        System.out.println("  Status      : " + (active ? "ACTIVE" : "INACTIVE"));
        System.out.println("  ----------------------------");
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s", getId(), getName(), designation);
    }
}
