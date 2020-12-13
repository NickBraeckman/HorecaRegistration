package service;

import data.CapsuleRepository;
import security.MSSecurityManager;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// LOCAL
public class MatchingService extends UnicastRemoteObject implements MatchingServiceInterface {

    private RegistrarServiceMSInterface registrarServiceMSInterface;

    public MatchingService(int port, RegistrarServiceMSInterface serviceMSInterface) throws RemoteException {
        super(port);
        this.registrarServiceMSInterface = serviceMSInterface;
    }

    /**
     * 3.2 submit logs
     * the doctor sends the capsules of an infected user
     * the matching service checks the validity of the capsules
     * ask the pseudonyms of the days that the capsules are created for
     * compare the hashes: H(Ri, nym) = H'(Ri,nym)
     * check the validity of the doctor sign
     * mark the capsules in local storage critical
     *
     * @param infectedCapsules : send and signed by Doctor
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean sendInfectedCapsule(List<InfectedCapsule> infectedCapsules) throws RemoteException {

        Set<LocalDate> dates = new HashSet<>();
        List<String> pseudonyms = new ArrayList<>();

        for (InfectedCapsule capsule : infectedCapsules) {
            dates.add(capsule.getArriveTime().toLocalDate());
            dates.add(capsule.getExitTime().toLocalDate());
        }

        // request pseudonyms
        for (LocalDate date : dates) {
            pseudonyms = registrarServiceMSInterface.requestPseudonyms(date);
        }

        try {
            for (InfectedCapsule infectedCapsule : infectedCapsules) {
                // check the validity of the capsule's hash and the doctor sign
                if (MSSecurityManager.getInstance().verifySignInfectedCapsule(infectedCapsule, pseudonyms)) {
                    CapsuleRepository.getInstance().markCapsulesCritical(infectedCapsule.getTokenSign(), infectedCapsule.getHash(), infectedCapsule.getArriveTime(), infectedCapsule.getExitTime());
                }
            }

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
