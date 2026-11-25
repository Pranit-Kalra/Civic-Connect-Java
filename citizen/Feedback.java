package citizen;

import java.time.LocalDateTime;

/**
 * Feedback Entity: Citizen rating and remarks for a resolved issue.
 */
public class Feedback {
    private String id;
    private String issueId;
    private String citizenId;
    private int rating; // 1 to 5
    private String comments;
    private LocalDateTime date;

    public Feedback(String id, String issueId, String citizenId, int rating, String comments, LocalDateTime date) {
        this.id = id;
        this.issueId = issueId;
        this.citizenId = citizenId;
        this.rating = rating;
        this.comments = comments;
        this.date = (date != null) ? date : LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getIssueId() { return issueId; }
    public String getCitizenId() { return citizenId; }
    public int getRating() { return rating; }
    public String getComments() { return comments; }
    public LocalDateTime getDate() { return date; }
}
