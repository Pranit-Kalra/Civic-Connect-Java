package citizen;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Citizen Entity: Represents a registered municipal resident.
 */
public class Citizen {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String password;
    private LocalDateTime registrationDate;

    public Citizen(String id, String name, String email, String phone, String address, String password, LocalDateTime regDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.registrationDate = (regDate != null) ? regDate : LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public String getRole() { return "Citizen"; }

    public void displayProfile() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.println("\n  --- [ CITIZEN PROFILE ] ---");
        System.out.println("  Citizen ID    : " + id);
        System.out.println("  Full Name     : " + name);
        System.out.println("  Email         : " + email);
        System.out.println("  Phone         : " + phone);
        System.out.println("  Address       : " + address);
        System.out.println("  Registered On : " + registrationDate.format(dtf));
        System.out.println("  ---------------------------");
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", id, name, email);
    }
}
