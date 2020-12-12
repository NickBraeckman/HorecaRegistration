package service;

import data.CapsuleRepository;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

// LOCAL
public class MatchingServiceMP extends UnicastRemoteObject implements MatchingServiceMPInterface {

    public MatchingServiceMP(int port) throws RemoteException {
        super(port);
    }

    @Override
    public void sendCapsules(List<Capsule> capsules) throws RemoteException {
        for (Capsule capsule : capsules) {
            try {
                CapsuleRepository.getInstance().addCapsule(capsule);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendUninformedTokens(List<String> uninformedTokenSigns) throws RemoteException {
        for (String tokenSign : uninformedTokenSigns) {
            try {
                CapsuleRepository.getInstance().markCapsulesInformed(tokenSign);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
