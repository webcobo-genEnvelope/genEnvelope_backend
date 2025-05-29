package domain;

public class GeneticEnvelope {
    final String resultFilePath;
    final String markFilePath;
    final String envelopeZipPath;

    public GeneticEnvelope(String resultFilePath, String markFilePath, String envelopeZipPath) {
        this.resultFilePath = resultFilePath;
        this.markFilePath = markFilePath;
        this.envelopeZipPath = envelopeZipPath;
    }

    public String getResultFilePath() { return resultFilePath; }
    public String getMarkFilePath() { return markFilePath; }
    public String getEnvelopeZipPath() { return envelopeZipPath; }
}
