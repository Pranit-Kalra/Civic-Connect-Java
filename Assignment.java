package resolver;

import java.time.LocalDateTime;

/**
 * Assignment Entity: Records allocation of an issue to a municipal resolver.
 */
public class Assignment {
    private String id;
    private String issueId;
    private String employeeId;
    private LocalDateTime assignedAt;
    private String notes;
    private LocalDateTime completionDate;

    public Assignment(String id, String issueId, String employeeId, LocalDateTime assignedAt, String notes) {
        this.id = id;
        this.issueId = issueId;
        this.employeeId = employeeId;
        this.assignedAt = (assignedAt != null) ? assignedAt : LocalDateTime.now();
        this.notes = notes;
        this.completionDate = null;
    }

    public String getId() { return id; }
    public String getIssueId() { return issueId; }
    public String getEmployeeId() { return employeeId; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCompletionDate() { return completionDate; }

    public boolean setCompletionDate(LocalDateTime completionDate) {
        if (completionDate != null && completionDate.isBefore(assignedAt)) {
            return false; // Chronological consistency check
        }
        this.completionDate = completionDate;
        return true;
    }
}
