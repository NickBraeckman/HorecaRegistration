package service;

import data.CapsuleRepository;
import data.UninformedTokensRepository;
import security.MPSecurityManager;
import util.Constants;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MPServiceVisitor extends UnicastRemoteObject implements MPServiceVisitorInterface {


    public MPServiceVisitor(int port) throws RemoteException {
        super(port);

    }

    /**
     * 2.1 register visit
     * happens after scanning of qr code
     * sign H(Ri, nym) or hash
     * store capsule with a time interval
     * send signed H(Ri, nym) back
     * @param capsule
     * @param vsService
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean sendCapsule(Capsule capsule, VisitorServiceMPInterface vsService) throws RemoteException {
        // check the validity and date of the token
        try {
            if (!MPSecurityManager.verifySignToken(capsule.getToken(), LocalDate.now())) {
                return false;
            }
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }

        // check if the token is already spent before
        try {
            if (!CapsuleRepository.getInstance().isTokenAlreadySpent(capsule.getToken())) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // sign the hash and temporary store the capsule in a csv folder
        try {
            Acknowledgement ack = MPSecurityManager.getInstance().generateAcknowledgement(capsule);
            vsService.receiveAcknowledgment(ack);

            // update time interval @ mixing proxy side
            capsule.setStopTime(LocalDateTime.now().plusSeconds(Constants.CAPSULE_VISITOR_FLUSH_DELAY));
            CapsuleRepository.getInstance().addCapsule(capsule);
        } catch (IOException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 2.1 register visit
     * update the capsule exit time when visitor leaves the catering facility -> exit
     * @param capsule
     * @param vsService
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean updateCapsule(Capsule capsule, VisitorServiceMPInterface vsService) throws RemoteException {
        // check the validity and date of the token
        try {
            if (!MPSecurityManager.verifySignToken(capsule.getToken(), LocalDate.now())) {
                return false;
            }
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }

        // check if the capsule can be updated
        try {
            capsule.setStopTime(LocalDateTime.now().plusSeconds(Constants.SAFETY_MARGIN));
            if (!CapsuleRepository.getInstance().updateCapsule(capsule)) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // sign the hash and temporary store the capsule in a csv folder
        try {
            Acknowledgement ack = MPSecurityManager.getInstance().generateAcknowledgement(capsule);
            vsService.receiveAcknowledgment(ack);
        } catch (IOException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * user sends tuples that appear in his/her local storage
     * mixing proxy forwards these to the matching service
     * @param uninformedTokenSigns
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean sendUninformedTokenSigns(List<String> uninformedTokenSigns) throws RemoteException {

        try {
            UninformedTokensRepository.getInstance().addUninformedTokenSigns(uninformedTokenSigns);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
