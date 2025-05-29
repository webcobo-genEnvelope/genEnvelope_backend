package util;

import javax.crypto.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EnvelopeUtils {
    public static void createEnvelopeWithCrypto(
            String resultPath,
            String markPath,
            String zipPath,
            PrivateKey senderPrivateKey,
            PublicKey receiverPublicKey
    ) {
        try {
            SecretKey aesKey = KeyGenerator.getInstance("AES").generateKey();

            byte[] resultBytes = Files.readAllBytes(Paths.get(resultPath));
            byte[] resultHash = CryptoUtils.hash(resultBytes);
            byte[] resultSig = CryptoUtils.sign(resultHash, senderPrivateKey);
            byte[] encryptedResult = CryptoUtils.encryptWithAES(resultBytes, aesKey);

            byte[] markBytes = Files.readAllBytes(Paths.get(markPath));
            byte[] markHash = CryptoUtils.hash(markBytes);
            byte[] markSig = CryptoUtils.sign(markHash, senderPrivateKey);

            byte[] encryptedAESKey = CryptoUtils.encryptAESKeyWithRSA(aesKey, receiverPublicKey);

            try (FileOutputStream fos = new FileOutputStream(zipPath);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                writeToZip(zos, "encrypted_result.txt", encryptedResult);
                writeToZip(zos, "certified_mark.png", markBytes);
                writeToZip(zos, "result.sig", resultSig);
                writeToZip(zos, "mark.sig", markSig);
                writeToZip(zos, "envelope.key", encryptedAESKey);
            }

            System.out.println("✅ 전자봉투 생성 완료: " + zipPath);
        } catch (Exception e) {
            System.err.println("❌ 전자봉투 생성 실패: " + e.getMessage());
        }
    }

    private static void writeToZip(ZipOutputStream zos, String name, byte[] data) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(data);
        zos.closeEntry();
    }
}