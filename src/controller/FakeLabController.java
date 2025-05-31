package controller;

import service.EnvelopeService;

public class FakeLabController {
    private final EnvelopeService service = new EnvelopeService();

    public void create(String receiver, String resultPath, String markPath, String zipPath) {
        service.generateEnvelope(resultPath, markPath, receiver, zipPath, true); // ✅ 위조이므로 true 전달
    }
}
