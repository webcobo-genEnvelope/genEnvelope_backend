package domain;

public class VerificationResult {
    private final String status;
    private final String content;

    public VerificationResult(String status, String content) {
        this.status = status;
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }
}
