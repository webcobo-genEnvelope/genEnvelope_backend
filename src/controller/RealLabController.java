package controller;

import service.EnvelopeService;

public class RealLabController {
    private final EnvelopeService service = new EnvelopeService();

    public void create(String resultPath, String markPath) {
        service.generateEnvelope(resultPath, markPath);
    }
}