package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

// REMOTE
public interface MPServiceVisitorInterface extends Remote {

    public static String SERVICE_NAME = "MPServiceVisitorInterface";

    boolean sendCapsule(Capsule capsule, VisitorServiceMPInterface vsService) throws RemoteException;

    boolean updateCapsule(Capsule capsule, VisitorServiceMPInterface vsService) throws RemoteException;

    boolean sendUninformedTokenSigns(List<String> uninformedTokenSigns) throws RemoteException;
}
