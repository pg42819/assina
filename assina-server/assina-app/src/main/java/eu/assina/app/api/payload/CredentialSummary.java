package eu.assina.app.api.payload;

import java.time.Instant;

public class CredentialSummary {
    private String id;
    private String ownerUsername;
    private String ownerName;
    private Instant createdAt;
    private String type;
    private String description;

    public CredentialSummary(String id, String ownerUsername, String ownerName, Instant createdAt,
                             String type, String description) {
        this.id = id;
        this.ownerUsername = ownerUsername;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
        this.type = type;
        this.description = description;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
