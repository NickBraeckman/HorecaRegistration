package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

// LOCAL
public interface MatchingServiceInterface extends Remote {
    public static String SERVICE_NAME = "MatchingServiceInterface";

    boolean sendInfectedCapsule(List<InfectedCapsule> infectedCapsules) throws RemoteException;

}
