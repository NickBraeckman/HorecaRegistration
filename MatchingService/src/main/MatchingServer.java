package main;

import com.sun.istack.internal.Nullable;
import data.CapsuleRepository;
import lombok.SneakyThrows;
import service.*;
import util.Constants;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MatchingServer {
    private static final Logger logger = Logger.getLogger(MatchingServer.class.getName());

    private RegistrarServiceMSInterface registrarService;

    public void start() {
        try {
            Registry registry = LocateRegistry.createRegistry(Constants.MATCHING_PORT_NUMBER);
            registry.rebind(MatchingServiceMPInterface.SERVICE_NAME, new MatchingServiceMP(Constants.MATCHING_PORT_NUMBER));
            registry.rebind(MatchingServiceVisitorInterface.SERVICE_NAME, new MatchingServiceVisitor(Constants.MATCHING_PORT_NUMBER));
            info("MatchingServer is running on port: " + Constants.MATCHING_PORT_NUMBER);
            startTimer();

            Registry registrarRegistry = LocateRegistry.getRegistry(Constants.REGISTRAR_SERVER_NAME, Constants.REGISTRAR_PORT_NUMBER);
            registrarService = (RegistrarServiceMSInterface) registrarRegistry.lookup(RegistrarServiceMSInterface.SERVICE_NAME);

            registry.rebind(MatchingServiceInterface.SERVICE_NAME, new MatchingService(Constants.MATCHING_PORT_NUMBER, registrarService));

        } catch (RemoteException e) {
            error("MatchingServer could not connect");
            e.printStackTrace();
            System.exit(0);
        } catch (NotBoundException e) {
            error("MatchingServer could not bind");
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            error("Could not start the timer");
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void startTimer() throws IOException {
        TimerTask repeatedTask = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                info("Server is running");

                List<String> tokenSigns = CapsuleRepository.getInstance().getUninformedTokens(LocalDateTime.now().minusSeconds(Constants.UNINFORMED_TOKEN_FORWARD_DELAY).toLocalDate());

                if (!tokenSigns.isEmpty()) {
                    info("Send Uninformed Tokens to Registrar");
                    registrarService.sendUninformedTokens(tokenSigns);
                }

                CapsuleRepository.getInstance().flushCapsules();
            }
        };
        Timer timer = new Timer("daily timer");
        timer.scheduleAtFixedRate(repeatedTask, Constants.TIMER_DELAY, Constants.TIMER_PERIOD);
    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
}
