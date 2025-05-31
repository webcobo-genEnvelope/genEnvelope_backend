package controller;

import service.EnvelopeService;

public class RealLabController {
    private final EnvelopeService service = new EnvelopeService();

    // ✅ isFake까지 포함한 최신 시그니처
    public void create(String receiverName, String resultPath, String markPath, String zipPath, boolean isFake) {
        service.generateEnvelope(resultPath, markPath, receiverName, zipPath, isFake);
    }
}