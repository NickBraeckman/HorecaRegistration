package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

// LOCAL
public interface MatchingServiceMPInterface extends Remote {
    public static String SERVICE_NAME = "MatchingServiceMPInterface";

    void sendCapsules(List<Capsule> capsules) throws RemoteException;

    void sendUninformedTokens(List<String> uninformedTokenSigns) throws RemoteException;
}
