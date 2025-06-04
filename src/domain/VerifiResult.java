package domain;

public class VerifiResult {
    private final String status;
    private final String content;

    public VerifiResult(String status, String content) {
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
