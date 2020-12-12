package main;

import com.sun.istack.internal.Nullable;
import data.CSVDataManager;
import data.CateringFacilityRepository;
import model.CateringFacility;
import security.RegistrarSecurityManager;
import service.*;
import util.Constants;
import util.PseudonymGenerator;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistrarServer {

    private static final Logger logger = Logger.getLogger(RegistrarServerApplication.class.getName());

    public RegistrarServer() {
    }

    public void start() {
        try {
            Registry registry = LocateRegistry.createRegistry(Constants.REGISTRAR_PORT_NUMBER);

            registry.rebind(RegistrarServiceCFInterface.SERVICE_NAME, new RegistrarServiceCF(Constants.REGISTRAR_PORT_NUMBER));
            registry.rebind(RegistrarServiceVisitorInterface.SERVICE_NAME, new RegistrarServiceVisitor(Constants.REGISTRAR_PORT_NUMBER));
            registry.rebind(RegistrarServiceMSInterface.SERVICE_NAME, new RegistrarServiceMS(Constants.REGISTRAR_PORT_NUMBER));

            info("RegistrarServer is running on port: " + Constants.REGISTRAR_PORT_NUMBER);
            startTimer();
        } catch (RemoteException e) {
            error("RegistrarServer could not run on port: " + Constants.REGISTRAR_PORT_NUMBER);
            e.printStackTrace();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {
        RegistrarSecurityManager.getInstance();
        TimerTask repeatedTask = new TimerTask() {
            @Override
            public void run() {
                info("Server is running: " + "\nBroadcast monthly pseudonyms...");
                broadcastMonthlyPseudonyms();

            }
        };

        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(repeatedTask, Constants.TIMER_DELAY, Constants.TIMER_PERIOD_MONTHLY_PSEUDONYMS);
    }

    public void broadcastMonthlyPseudonyms() {
        for (CateringFacility cf : CateringFacilityRepository.getInstance().getCateringFacilityList()) {

            // if the first of the month
            if (LocalDate.now().getDayOfMonth() == 1) {

                // amount of the days in current month
                int amountOfDaysInMonth = LocalDate.now().lengthOfMonth();

                LocalDate temp = LocalDate.now();

                // generate pseudonym for each day in of the month
                for (int i = 0; i < amountOfDaysInMonth; i++) {
                    Map<String, String> pseudonyms = new HashMap<>();
                    try {
                        pseudonyms = PseudonymGenerator.generatePseudonyms(cf, temp.plusDays(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cf.sendMonthlyPseudonyms(pseudonyms);
                }
            }
        }
    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }

    public void printDatabase() {
        try {
            String pseudonyms = CSVDataManager.getInstance().getCSVToString(CSVDataManager.CSVFile.PSEUDONYMS);
            String tokens = CSVDataManager.getInstance().getCSVToString(CSVDataManager.CSVFile.TOKENS);
            System.out.println(pseudonyms);
            System.out.println(tokens);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
