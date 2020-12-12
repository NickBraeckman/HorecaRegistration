package service;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

// LOCAL
public interface MatchingServiceVisitorInterface extends Remote {
    public static String SERVICE_NAME = "MatchingServiceVisitorInterface";

    List<CriticalTuple> requestCriticalTuples() throws RemoteException;
}
