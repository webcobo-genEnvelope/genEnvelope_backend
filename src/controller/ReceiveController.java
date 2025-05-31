package controller;

import service.EnvelopeVerificationService;

public class ReceiveController {
    private final EnvelopeVerificationService service = new EnvelopeVerificationService();

    public String[] verifyEnvelope(String username) {
        return service.decryptAndVerify(username);
    }

    public String[] verifyFakeEnvelope(String username) {
        return service.decryptAndVerifyFake(username);
    }

    public String[] verify(String username) {
        return service.decryptAndVerify(username, "data/court_envelope.zip");
    }
}
