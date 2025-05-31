package service;

import domain.VerificationResult;
import repository.EnvelopeVerifyRepository;
import util.EnvelopeVerifier;

public class EnvelopeVerificationService {
    private final EnvelopeVerifyRepository repository = new EnvelopeVerifyRepository();

    public String[] decryptAndVerify(String username) {
        return decryptAndVerify(username, "data/envelope.zip");
    }

    public String[] decryptAndVerifyFake(String username) {
        return decryptAndVerify(username, "data/fake_envelope.zip");
    }

    public String[] decryptAndVerify(String username, String zipPath) {
        try {
            String privateKeyPath = "data/" + username + "private";
            VerificationResult result = EnvelopeVerifier.verify(zipPath, privateKeyPath);
            repository.save(result);
            return new String[]{ result.getStatus(), result.getContent() };
        } catch (Exception e) {
            return new String[]{ "오류 발생", e.getMessage() };
        }
    }
}