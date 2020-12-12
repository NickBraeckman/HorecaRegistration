package main;

import com.sun.istack.internal.Nullable;
import lombok.SneakyThrows;
import service.CFService;
import service.RegistrarServiceCFInterface;
import util.Config;
import util.Constants;
import util.QRCodeGenerator;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CateringFacilityController {

    private Registry registry;
    private RegistrarServiceCFInterface registrarService;
    CFService CFService;
    private long businessNumber;

    private boolean isAuthenticated;

    private static final Logger logger = Logger.getLogger(CateringFacilityController.class.getName());

    public CateringFacilityController() {
    }

    public void start() {

        try {
            registry = LocateRegistry.getRegistry(Constants.REGISTRAR_SERVER_NAME, Constants.REGISTRAR_PORT_NUMBER);
            registrarService = (RegistrarServiceCFInterface) registry.lookup(RegistrarServiceCFInterface.SERVICE_NAME);
            CFService = new CFService(registrarService);
        } catch (RemoteException e) {
            error("CateringFacilityApplication could not connect");
            e.printStackTrace();
            System.exit(0);
        } catch (NotBoundException e) {
            error("CateringFacilityApplication could not bind");
            e.printStackTrace();
            System.exit(0);
        }

    }

    private void startTimer() {
        TimerTask repeatedTask = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                info("Server is running");
                String currentQRCode = QRCodeGenerator.generateDailyQRCode(businessNumber).getFormattedQRCode();
                info("Current QR Code: \n" + currentQRCode);
            }
        };

        Timer timer = new Timer("Daily Timer");
        timer.scheduleAtFixedRate(repeatedTask, Constants.TIMER_DELAY, Constants.TIMER_PERIOD_QR_CODE);
    }

    public boolean authenticate(long businessNumber, String location, String password) {

        // directory name is bound to the business number
        // easier separation of csv files for demo purposes
        Config.DIR_NAME = "dir_cf_" + businessNumber + "_" + location;

        if (CFService.authenticate(businessNumber, location, password)) {
            this.businessNumber = businessNumber;
            startTimer();
            return true;
        } else {
            return false;
        }

    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
}





