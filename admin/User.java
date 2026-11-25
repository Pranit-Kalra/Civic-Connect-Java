package admin;

/**
 * ============================================================================
 * INTERFACE: UserInfo
 * ============================================================================
 * Defines essential behavior that every user type in CivicConnect must fulfill.
 */
interface UserInfo {
    String getRole();
    void displayProfile();
}

/**
 * ============================================================================
 * ABSTRACT SUPERCLASS: User
 * ============================================================================
 * Common superclass for all system users (Citizen, AdminUser, Employee).
 * 
 * Demonstrates:
 *   - Abstraction (cannot be instantiated directly)
 *   - Interface implementation (implements UserInfo)
 *   - Encapsulation (private fields with public getters and setters)
 *   - Polymorphism (abstract methods getRole and displayProfile)
 */
public abstract class User implements UserInfo {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String password;

    public User(String id, String name, String email, String phone, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // --- Encapsulation: Getters and Setters ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // --- Abstraction: Abstract methods implemented uniquely by subclasses ---
    @Override
    public abstract String getRole();

    @Override
    public abstract void displayProfile();

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s", id, name, email, getRole());
    }
}
