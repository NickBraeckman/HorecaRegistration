package security;

import data.CSVDataManager;
import data.PublicKeyRepository;
import service.InfectedCapsule;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class DoctorSecurityManager {
    private static DoctorSecurityManager instance;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private DoctorSecurityManager() {
        initKeyPair();
    }

    public static synchronized DoctorSecurityManager getInstance() {
        if (instance == null) {
            instance = new DoctorSecurityManager();
        }
        return instance;
    }

    private void initKeyPair() {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair dsaPair = keyGen.generateKeyPair();
            publicKey = dsaPair.getPublic();
            CSVDataManager.getInstance().writePublicKeyToCsv(CSVDataManager.CSVFile.PUBLIC_KEYS_DOCTOR,publicKey);
            privateKey = dsaPair.getPrivate();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    public List<InfectedCapsule> signInfectedCapsules(List<InfectedCapsule> infectedCapsules) throws InvalidKeyException, NoSuchAlgorithmException, IOException, SignatureException {

        // create and initialize signature object
        Signature signEngine = Signature.getInstance("SHA256withRSA");
        signEngine.initSign(privateKey);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        for (InfectedCapsule capsule : infectedCapsules){
            dos.writeBytes(capsule.getTokenSign());
            dos.writeBytes(capsule.getHash());
            dos.writeBytes(capsule.getRi());
            dos.flush();
            byte[] output = bos.toByteArray();

            // sign data
            signEngine.update(output);
            byte[] signature = signEngine.sign();
            capsule.setDoctorSign(Base64.getEncoder().encodeToString(signature));
            capsule.setData(Base64.getEncoder().encodeToString(output));
            bos.reset();
        }

        return infectedCapsules;
    }

}
