package util;

import javax.crypto.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnvelopeUtils {

    private static final Logger LOGGER = Logger.getLogger(EnvelopeUtils.class.getName());

    public static void createSecureEnvelope(String resultPath, String markPath, String zipPath,
                                            String receiverPublicKeyPath, KeyPair labKeyPair,
                                            String certificatePath, boolean isFake) {

        try {
            SecretKey aesKey = KeyGenerator.getInstance("AES").generateKey();
            PublicKey receiverKey = KeyManager.loadPublicKey(receiverPublicKeyPath);

            byte[] resultBytes = Files.readAllBytes(Paths.get(resultPath));
            byte[] resultHash = CryptoUtils.hash(resultBytes);
            byte[] resultSig = CryptoUtils.sign(resultHash, labKeyPair.getPrivate());

            byte[] markBytes = Files.readAllBytes(Paths.get(markPath));
            byte[] markSig;
            if (isFake) {
                markSig = CryptoUtils.sign("wrong_fake_data".getBytes(), labKeyPair.getPrivate());
            } else {
                byte[] markHash = CryptoUtils.hash(markBytes);
                markSig = CryptoUtils.sign(markHash, labKeyPair.getPrivate());
            }

            byte[] certBytes = Files.readAllBytes(Paths.get(certificatePath));

            File tempDir = new File("data/temp_content");
            if (!tempDir.exists() && !tempDir.mkdirs()) {
                throw new EnvelopeException("임시 폴더 생성 실패");
            }

            Files.write(Paths.get("data/temp_content/result.txt"), resultBytes);
            Files.write(Paths.get("data/temp_content/result.sig"), resultSig);
            Files.write(Paths.get("data/temp_content/certified_mark.png"), markBytes);
            Files.write(Paths.get("data/temp_content/mark.sig"), markSig);
            Files.write(Paths.get("data/temp_content/certificate.txt"), certBytes);

            byte[] encryptedPayload;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ZipOutputStream zos = new ZipOutputStream(baos)) {

                for (File f : tempDir.listFiles()) {
                    zos.putNextEntry(new ZipEntry(f.getName()));
                    zos.write(Files.readAllBytes(f.toPath()));
                    zos.closeEntry();
                }

                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                encryptedPayload = cipher.doFinal(baos.toByteArray());

                try (FileOutputStream cipherOut = new FileOutputStream("data/cipher_payload.dat")) {
                    cipherOut.write(encryptedPayload);
                }
            }

            byte[] encryptedAesKey = CryptoUtils.encryptKeyWithRSA(aesKey, receiverKey);

            try (FileOutputStream fos = new FileOutputStream(zipPath);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {
                writeToZip(zos, "encrypted_data.dat", encryptedPayload);
                writeToZip(zos, "envelope.key", encryptedAesKey);
            }

            for (File f : tempDir.listFiles()) f.delete();
            tempDir.delete();

        } catch (IOException | GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "전자봉투 생성 중 오류 발생", e);
            throw new EnvelopeException("전자봉투 생성 실패", e);
        }
    }

    private static void writeToZip(ZipOutputStream zos, String name, byte[] data) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(data);
        zos.closeEntry();
    }
}
