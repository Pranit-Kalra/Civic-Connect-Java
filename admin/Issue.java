package admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * CLASS: Issue & SUPPORTING CLASSES (StatusUpdate, InvalidComplaintException)
 * ============================================================================
 * Represents a citizen civic complaint ticket across its full lifecycle.
 * Lifecycle: Reported -> Under Review -> Assigned -> In Progress -> Resolved, plus Rejected.
 * 
 * Demonstrates:
 *   - Encapsulation (private fields with accessors/mutators)
 *   - Static Nested Classes (StatusUpdate for audit logging)
 *   - Custom Checked Exception (InvalidComplaintException for domain errors)
 */
public class Issue {
    private String id;
    private String citizenId;
    private String title;
    private String description;
    private String categoryId;
    private String area;
    private String city;
    private String state;
    private String landmark;
    private List<String> evidencePhotos;
    private String departmentId; // Initially null until assigned
    private String status;       // Reported, Under Review, Assigned, In Progress, Resolved, Rejected
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public Issue(String id, String citizenId, String title, String description, String categoryId,
                 String area, String city, String state, String landmark,
                 List<String> evidencePhotos, LocalDateTime createdAt) {
        this.id = id;
        this.citizenId = citizenId;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.area = area;
        this.city = city;
        this.state = state;
        this.landmark = landmark;
        this.evidencePhotos = (evidencePhotos != null) ? evidencePhotos : new ArrayList<>();
        this.departmentId = null;
        this.status = "Reported";
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
        this.resolvedAt = null;
    }

    public String getId() { return id; }
    public String getCitizenId() { return citizenId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getArea() { return area; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getLandmark() { return landmark; }
    public List<String> getEvidencePhotos() { return evidencePhotos; }
    public void addEvidencePhoto(String photo) { this.evidencePhotos.add(photo); }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getFormattedLocation() {
        return String.format("%s, %s, %s (Near: %s)", area, city, state, landmark);
    }

    public boolean isResolved() {
        return "Resolved".equalsIgnoreCase(status);
    }

    // ========================================================================
    // SUPPORTING CLASS: StatusUpdate (Audit Trail Entry)
    // ========================================================================
    public static class StatusUpdate {
        private String id;
        private String issueId;
        private String updatedByRole; // Citizen, Admin, Resolver
        private String updatedByName;
        private String fromStatus;
        private String toStatus;
        private String remarks;
        private LocalDateTime timestamp;

        public StatusUpdate(String id, String issueId, String updatedByRole, String updatedByName,
                            String fromStatus, String toStatus, String remarks, LocalDateTime timestamp) {
            this.id = id;
            this.issueId = issueId;
            this.updatedByRole = updatedByRole;
            this.updatedByName = updatedByName;
            this.fromStatus = fromStatus;
            this.toStatus = toStatus;
            this.remarks = remarks;
            this.timestamp = (timestamp != null) ? timestamp : LocalDateTime.now();
        }

        public String getId() { return id; }
        public String getIssueId() { return issueId; }
        public String getUpdatedByRole() { return updatedByRole; }
        public String getUpdatedByName() { return updatedByName; }
        public String getFromStatus() { return fromStatus; }
        public String getToStatus() { return toStatus; }
        public String getRemarks() { return remarks; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }

    // ========================================================================
    // CUSTOM EXCEPTION: InvalidComplaintException
    // ========================================================================
    public static class InvalidComplaintException extends Exception {
        public InvalidComplaintException(String message) {
            super(message);
        }
    }
}
