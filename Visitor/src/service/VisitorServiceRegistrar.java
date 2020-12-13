package service;

import data.TokenRepository;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class VisitorServiceRegistrar extends UnicastRemoteObject implements VisitorServiceRegistrarInterface {

    private RegistrarServiceVisitorInterface registrarService;
    private TokenRepository tokenRepository;
    private boolean isAuthenticated;

    public VisitorServiceRegistrar(RegistrarServiceVisitorInterface registrarService, TokenRepository tokenRepository) throws RemoteException {
        this.registrarService = registrarService;
        this.tokenRepository = tokenRepository;
    }

    /**
     * 1.3 retrieve tokens
     * receive the daily tokens from the registrar in a batch
     * @param tokenBatch send by registrar
     * @throws RemoteException
     */
    @Override
    public void saveDailyTokens(TokenBatch tokenBatch) throws RemoteException {
        try {
            tokenRepository.initFiles();
            tokenRepository.putAllTokens(tokenBatch);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1.2 user enrollment
     * @param phoneNumber
     * @return
     */
    public boolean authenticate(String phoneNumber){
        isAuthenticated = false;
        try {
            isAuthenticated = registrarService.authenticate(phoneNumber, this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return isAuthenticated;
    }
}
