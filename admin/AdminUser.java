package admin;

/**
 * ============================================================================
 * CLASS: AdminUser (Subclass of User)
 * ============================================================================
 * Represents a municipal administrator with management privileges.
 * 
 * Demonstrates:
 *   - Inheritance (extends User superclass)
 *   - Polymorphism (overrides getRole and displayProfile)
 *   - Encapsulation
 */
public class AdminUser extends User {

    public AdminUser(String id, String name, String email, String phone, String password) {
        super(id, name, email, phone, password);
    }

    @Override
    public String getRole() {
        return "Administrator";
    }

    @Override
    public void displayProfile() {
        System.out.println("\n  --- [ ADMINISTRATOR PROFILE ] ---");
        System.out.println("  Admin ID   : " + getId());
        System.out.println("  Name       : " + getName());
        System.out.println("  Email      : " + getEmail());
        System.out.println("  Phone      : " + getPhone());
        System.out.println("  Role       : " + getRole());
        System.out.println("  Privileges : Municipal SuperAdmin");
        System.out.println("  ---------------------------------");
    }
}
