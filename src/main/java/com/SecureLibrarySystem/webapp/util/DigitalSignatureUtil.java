package com.SecureLibrarySystem.webapp.util;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

public class DigitalSignatureUtil {

    private static final Path KEY_FILE = Path.of(System.getProperty("user.dir"), "keys", "digital-signature.key");
    private static final KeyPair KEY_PAIR = loadOrCreateKeyPair();

    private static KeyPair loadOrCreateKeyPair() {
        try {
            if (Files.exists(KEY_FILE)) {
                return loadKeyPair();
            }
            KeyPair keyPair = generateKeyPair();
            saveKeyPair(keyPair);
            return keyPair;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private static KeyPair loadKeyPair() throws Exception {
        List<String> lines = Files.readAllLines(KEY_FILE);
        if (lines.size() < 2) {
            throw new IllegalStateException("Key file is missing data.");
        }
        byte[] privateKeyBytes = Base64.getDecoder().decode(lines.get(0));
        byte[] publicKeyBytes = Base64.getDecoder().decode(lines.get(1));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return new KeyPair(
                keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes)),
                keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes))
        );
    }

    private static void saveKeyPair(KeyPair keyPair) throws Exception {
        Files.createDirectories(KEY_FILE.getParent());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        Files.write(KEY_FILE, List.of(privateKey, publicKey));
    }

    // Create digital signature
    public static String sign(String data) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(KEY_PAIR.getPrivate());
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Verify signature
    public static boolean verify(String data, String signatureStr) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(KEY_PAIR.getPublic());
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(signatureStr));
        } catch (Exception e) {
            return false;
        }
    }
}
