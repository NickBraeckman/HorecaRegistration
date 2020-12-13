package security;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import data.CSVDataManager;
import data.PublicKeyRepository;
import service.InfectedCapsule;

import java.io.DataOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;

public class MSSecurityManager {

    private static MSSecurityManager instance;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private MSSecurityManager() {
    }

    public static synchronized MSSecurityManager getInstance() {
        if (instance == null) {
            instance = new MSSecurityManager();
        }
        return instance;
    }

    public boolean verifySignInfectedCapsule(InfectedCapsule infectedCapsule, List<String> pseudonyms) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {

        // The signature algorithm with SHA-* and the RSA encryption algorithm as defined in the OSI Interoperability Workshop, using the padding conventions described in PKCS #1.
        Signature signEngine = Signature.getInstance("SHA256withRSA");
        PublicKey key = PublicKeyRepository.getInstance().getRegistrarPublicKey(CSVDataManager.CSVFile.PUBLIC_KEYS_DOCTOR);


        byte[] sign = Base64.getDecoder().decode((infectedCapsule.getDoctorSign()).getBytes());
        byte[] data = Base64.getDecoder().decode(infectedCapsule.getData().getBytes());

        // verify signature
        signEngine.initVerify(key);
        signEngine.update(data);

        // check the sign of the infected capsule
        if (!signEngine.verify(sign)) {
            return false;
        }


        if (!verifyHashInfectedCapsules(infectedCapsule, pseudonyms)) {
            return false;
        }

        return true;
    }

    private boolean verifyHashInfectedCapsules(InfectedCapsule infectedCapsule, List<String> pseudonyms) throws NoSuchAlgorithmException, IOException {

        MessageDigest digester = MessageDigest.getInstance("SHA-256");

        boolean isValid = false;
        for (String pseudonym : pseudonyms) {
            byte[] pseudonymBytes = pseudonym.getBytes();
            byte[] ri = Base64.getDecoder().decode(infectedCapsule.getRi().getBytes());
            byte[] hash;

            // H(Ri, nym_CF_day_i)
            ByteOutputStream bos = new ByteOutputStream();
            byte[] output;

            try (DataOutputStream dos = new DataOutputStream(bos)) {
                dos.write(ri);
                dos.write(pseudonymBytes);
                dos.flush();
                hash = digester.digest(bos.getBytes());
                bos.close();
            }
            String hashValue = Base64.getEncoder().encodeToString(hash);
            if (hashValue.equals(infectedCapsule.getHash())) {
                isValid = true;
            }

        }
        return isValid;
    }

}
