package util;

import javax.crypto.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EnvelopeUtils {
    public static void createSecureEnvelope(String resultPath, String markPath, String zipPath,
                                            String receiverPublicKeyPath, KeyPair labKeyPair, String certificatePath) {
        try {
            SecretKey aesKey = KeyGenerator.getInstance("AES").generateKey();
            PublicKey receiverKey = KeyManager.loadPublicKey(receiverPublicKeyPath);

            // 전자서명
            byte[] resultBytes = Files.readAllBytes(Paths.get(resultPath));
            byte[] resultHash = CryptoUtils.hash(resultBytes);
            byte[] resultSig = CryptoUtils.sign(resultHash, labKeyPair.getPrivate());

            byte[] markBytes = Files.readAllBytes(Paths.get(markPath));
            byte[] markHash = CryptoUtils.hash(markBytes);
            byte[] markSig = CryptoUtils.sign(markHash, labKeyPair.getPrivate());

            byte[] certBytes = Files.readAllBytes(Paths.get(certificatePath));

            // 임시 폴더에 파일 저장 후 ZIP으로 압축
            File tempDir = new File("data/temp_content");
            tempDir.mkdir();
            Files.write(Paths.get("data/temp_content/result.txt"), resultBytes);
            Files.write(Paths.get("data/temp_content/result.sig"), resultSig);
            Files.write(Paths.get("data/temp_content/certified_mark.png"), markBytes);
            Files.write(Paths.get("data/temp_content/mark.sig"), markSig);
            Files.write(Paths.get("data/temp_content/certificate.txt"), certBytes);

            FileOutputStream cipherOut = new FileOutputStream("data/cipher_payload.dat");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (File f : tempDir.listFiles()) {
                    ZipEntry entry = new ZipEntry(f.getName());
                    zos.putNextEntry(entry);
                    zos.write(Files.readAllBytes(f.toPath()));
                    zos.closeEntry();
                }
            }
            byte[] encryptedPayload = cipher.doFinal(baos.toByteArray());
            cipherOut.write(encryptedPayload);
            cipherOut.close();

            // AES 키를 사용자 공개키로 암호화
            byte[] encryptedAesKey = CryptoUtils.encryptKeyWithRSA(aesKey, receiverKey);

            // 최종 zip 생성
            try (FileOutputStream fos = new FileOutputStream(zipPath);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {
                writeToZip(zos, "암호문.dat", encryptedPayload);
                writeToZip(zos, "envelope.key", encryptedAesKey);
            }

            // cleanup
            for (File f : tempDir.listFiles()) f.delete();
            tempDir.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeToZip(ZipOutputStream zos, String name, byte[] data) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(data);
        zos.closeEntry();
    }
}