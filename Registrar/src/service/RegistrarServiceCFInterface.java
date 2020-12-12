package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

//LOCAL
public interface RegistrarServiceCFInterface extends Remote {

    public static String SERVICE_NAME = "RegistrarServiceCFInterface";

    boolean authenticateCateringFacility(long businessNumber, String location, String password, CFServiceInterface cfService) throws RemoteException;

}
