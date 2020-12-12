package main;

import com.sun.istack.internal.Nullable;
import data.CSVDataManager;
import data.CapsuleRepository;
import data.UninformedTokensRepository;
import lombok.SneakyThrows;
import service.Capsule;
import service.MPServiceVisitor;
import service.MatchingServiceMPInterface;
import util.Constants;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MixingProxyServer {

    private static final Logger logger = Logger.getLogger(MixingProxyServer.class.getName());

    private MatchingServiceMPInterface matchingService;

    public MixingProxyServer() {
    }

    public void start() {
        try {
            Registry registry = LocateRegistry.createRegistry(Constants.MIXING_PROXY_PORT_NUMBER);
            registry.rebind(MPServiceVisitor.SERVICE_NAME, new MPServiceVisitor(Constants.MIXING_PROXY_PORT_NUMBER));

            Registry matchingRegistry = LocateRegistry.getRegistry(Constants.MATCHING_SERVER_NAME, Constants.MATCHING_PORT_NUMBER);
            matchingService = (MatchingServiceMPInterface) matchingRegistry.lookup(MatchingServiceMPInterface.SERVICE_NAME);

            info("MixingProxy is running on port: " + Constants.MIXING_PROXY_PORT_NUMBER);

            startTimer();
        } catch (RemoteException e) {
            error("MixingProxy could not connect");
            e.printStackTrace();
            System.exit(0);
        } catch (NotBoundException e) {
            error("MixingProxy could not bind");
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            error("Could not start the timer");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void sendCapsules() {
        try {
            List<Capsule> capsules = CapsuleRepository.getInstance().flushCapsules();
            matchingService.sendCapsules(capsules);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startTimer() throws IOException {
        TimerTask repeatedTask = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                info("Server is running: ");

                // demo purposes -> flush button
                // uncomment these in production

                /*List<Capsule> capsules = CapsuleRepository.getInstance().flushCapsules();
                if (!capsules.isEmpty()) {
                    matchingService.sendCapsules(capsules);
                }*/

                List<String> uninformedTokens = UninformedTokensRepository.getInstance().flushUninformedTokens();
                if (!uninformedTokens.isEmpty()) {
                    info("Send uninformed tokens");
                    matchingService.sendUninformedTokens(uninformedTokens);
                }
            }
        };
        Timer timer = new Timer("daily timer");
        timer.scheduleAtFixedRate(repeatedTask, Constants.TIMER_DELAY, Constants.TIMER_PERIOD_FLUSH_UNINFORMED);
    }

    public void printDatabase() {
        try {
            String capsules = CSVDataManager.getInstance().getCSVToString(CSVDataManager.CSVFile.CAPSULES);
            System.out.println(capsules);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
}
