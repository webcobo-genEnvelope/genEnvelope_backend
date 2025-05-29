package util;

import domain.VerificationResult;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EnvelopeVerifier {
    public static VerificationResult verify(String zipPath, String privateKeyPath) throws Exception {
        PrivateKey privateKey = KeyManager.loadPrivateKey(privateKeyPath);
        byte[] encryptedAesKey = null;
        byte[] encryptedPayload = null;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("envelope.key")) {
                    encryptedAesKey = zis.readAllBytes();
                } else if (entry.getName().equals("암호문.dat")) {
                    encryptedPayload = zis.readAllBytes();
                }
                zis.closeEntry();
            }
        }

        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decrypted = aesCipher.doFinal(encryptedPayload);

        File tempZip = new File("data/decrypted_payload.zip");
        Files.write(tempZip.toPath(), decrypted);

        String resultText = "";
        String status = "진본 확인됨";

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(tempZip))) {
            ZipEntry entry;
            byte[] resultSig = null, markSig = null, resultBytes = null, markBytes = null, certBytes = null;

            while ((entry = zis.getNextEntry()) != null) {
                byte[] data = zis.readAllBytes();
                switch (entry.getName()) {
                    case "result.txt" -> resultBytes = data;
                    case "result.sig" -> resultSig = data;
                    case "certified_mark.png" -> markBytes = data;
                    case "mark.sig" -> markSig = data;
                    case "certificate.txt" -> certBytes = data;
                }
            }

            PublicKey labPublicKey = KeyFactory.getInstance("RSA")
                    .generatePublic(new java.security.spec.X509EncodedKeySpec(certBytes));

            byte[] resultHash = CryptoUtils.hash(resultBytes);
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(labPublicKey);
            sig.update(resultHash);
            if (!sig.verify(resultSig)) status = "위조 감지 (결과 파일)";

            byte[] markHash = CryptoUtils.hash(markBytes);
            sig.initVerify(labPublicKey);
            sig.update(markHash);
            if (!sig.verify(markSig)) status = "위조 감지 (마크 파일)";

            resultText = new String(resultBytes);
        }

        return new VerificationResult(status, resultText);
    }
}
