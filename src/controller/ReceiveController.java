package controller;

import service.EnvelopeVerificationService;

public class ReceiveController {
    private final EnvelopeVerificationService service = new EnvelopeVerificationService();

    public String[] verifyEnvelope(String username) {
        return service.decryptAndVerify(username);
    }
}
