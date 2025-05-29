package service;

import domain.VerificationResult;
import repository.EnvelopeVerifyRepository;
import util.EnvelopeVerifier;

public class EnvelopeVerificationService {
    private final EnvelopeVerifyRepository repository = new EnvelopeVerifyRepository();

    public String[] decryptAndVerify(String username) {
        try {
            String privateKeyPath = "data/" + username + "private";
            String zipPath = "data/envelope.zip";
            VerificationResult result = EnvelopeVerifier.verify(zipPath, privateKeyPath);
            repository.save(result);
            return new String[]{ result.getStatus(), result.getContent() };
        } catch (Exception e) {
            return new String[]{ "오류 발생", e.getMessage() };
        }
    }
}
