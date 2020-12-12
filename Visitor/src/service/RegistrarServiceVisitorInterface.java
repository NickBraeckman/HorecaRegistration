package service;


import java.rmi.Remote;
import java.rmi.RemoteException;

// LOCAL
public interface RegistrarServiceVisitorInterface extends Remote {
    public static String SERVICE_NAME = "RegistrarVisitorServiceInterface";

    public boolean authenticate(String phoneNumber, VisitorServiceRegistrarInterface visitorService) throws RemoteException;
    public boolean login(String phoneNumber, String passwd, VisitorServiceRegistrarInterface visitorService) throws RemoteException;
    public boolean signup(String phoneNumber, String passwd, VisitorServiceRegistrarInterface visitorService) throws RemoteException;
    TokenBatch receiveDailyTokens() throws RemoteException;
}
