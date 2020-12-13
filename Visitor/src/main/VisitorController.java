package main;

import com.sun.istack.internal.Nullable;
import data.CSVDataManager;
import data.QRCode;
import data.TokenRepository;
import data.VisitorLogRepository;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import lombok.SneakyThrows;
import service.*;
import util.Config;
import util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VisitorController {

    private static final Logger logger = Logger.getLogger(VisitorController.class.getName());

    private RegistrarServiceVisitorInterface registrarService;
    private MPServiceVisitorInterface mixingProxyService;
    private VisitorServiceRegistrar visitorServiceRegistrar;
    private MatchingServiceVisitorInterface matchingService;
    private VisitorServiceMP visitorServiceMP;
    private TokenRepository tokenRepository;
    private VisitorLogRepository visitorLogRepository;
    private CSVDataManager dataManager;

    private LocalDateTime temp;
    private LocalDateTime arriveTime;
    private QRCode qrCode;
    private boolean stopLogging = false;
    private Token token;
    private BooleanProperty stopLoggingProperty = new SimpleBooleanProperty();
    private StringProperty timeInterval = new SimpleStringProperty();

    public VisitorController() {
        dataManager = new CSVDataManager();
        tokenRepository = new TokenRepository(dataManager);
        visitorLogRepository = new VisitorLogRepository(dataManager);
    }

    // observable
    public final boolean getStopLoggingProperty() {
        return stopLoggingProperty.get();
    }

    public final void setStopLoggingProperty(boolean stopLoggingProperty) {
        this.stopLoggingProperty.set(stopLoggingProperty);
    }

    public StringProperty timeInterval() {return  timeInterval;}

    public BooleanProperty stopLogging() {
        return stopLoggingProperty;
    }

    public void start() {

        try {
            Registry registryRegistrar = LocateRegistry.getRegistry(Constants.REGISTRAR_SERVER_NAME, Constants.REGISTRAR_PORT_NUMBER);
            registrarService = (RegistrarServiceVisitorInterface) registryRegistrar.lookup(RegistrarServiceVisitorInterface.SERVICE_NAME);
            Registry registryMixingProxy = LocateRegistry.getRegistry(Constants.MIXING_PROXY_SERVER_NAME, Constants.MIXING_PROXY_PORT_NAME);
            mixingProxyService = (MPServiceVisitorInterface) registryMixingProxy.lookup(MPServiceVisitorInterface.SERVICE_NAME);
            Registry registryMatchingServer = LocateRegistry.getRegistry(Constants.MATCHING_SERVER_NAME, Constants.MATCHING_PORT_NUMBER);
            matchingService = (MatchingServiceVisitorInterface) registryMatchingServer.lookup(MatchingServiceVisitorInterface.SERVICE_NAME);

            visitorServiceRegistrar = new VisitorServiceRegistrar(registrarService, tokenRepository);
            visitorServiceMP = new VisitorServiceMP(mixingProxyService);

        } catch (RemoteException e) {
            error("VisitorApplication could not connect");
            e.printStackTrace();
            System.exit(0);
        } catch (NotBoundException e) {
            error("VisitorApplication could not bind");
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void startTokenTimer(String hash) throws IOException {
        temp = arriveTime;
        TimerTask repeatedTask = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                info("Visitor is using the tokens");

                // stop the timer
                if (stopLoggingProperty.get() || stopLogging) {
                    this.cancel();
                }

                // only do this when the temp time + flush delay < now
                if (temp.plusSeconds(Constants.CAPSULE_VISITOR_FLUSH_DELAY).isBefore(LocalDateTime.now())) {

                    // get a token
                    token = tokenRepository.getToken();

                    // update the temp time with the flush delay
                    temp = temp.plusSeconds(Constants.CAPSULE_VISITOR_FLUSH_DELAY);

                    // try to send the capsule to the mixing proxy (validation)
                    if (token == null || !visitorServiceMP.sendCapsule(temp, temp.plusSeconds(Constants.CAPSULE_VISITOR_FLUSH_DELAY), hash, token)) {
                        stopLoggingProperty.setValue(true);
                        stopLogging = true;
                    } else {

                        Platform.runLater(() -> timeInterval.set(temp.toString() + " - " + temp.plusSeconds(Constants.CAPSULE_VISITOR_FLUSH_DELAY).toString()));
                        // add the qr-code entries, time interval and token to the visitor log
                        visitorLogRepository.logQrCode(temp, qrCode, token.getSign());
                    }

                    if (tokenRepository.getTokenCount() == 0) {
                        stopLoggingProperty.setValue(true);
                        stopLogging = true;
                    }
                }
            }
        };
        // schedule the task
        Timer timer = new Timer("Token Timer");
        timer.scheduleAtFixedRate(repeatedTask, Constants.TOKEN_TIMER_DELAY, Constants.TOKEN_TIMER_PERIOD);
    }

    public void startCriticalTupleTimer() {
        TimerTask repeatedTask = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                List<CriticalTuple> tupleList = matchingService.requestCriticalTuples();
                if (!tupleList.isEmpty()) {
                    info("Received critical tuples");
                    Set<String> uninformedTokenSigns = new HashSet<>();
                    for (CriticalTuple tuple : tupleList) {
                        uninformedTokenSigns.addAll(visitorLogRepository.getTokenSigns(tuple.getHash(), tuple.getArriveTime(), tuple.getExitTime()));
                    }
                    if (!uninformedTokenSigns.isEmpty()) {
                        info("Found uninformed tokens");
                        mixingProxyService.sendUninformedTokenSigns(new ArrayList<>(uninformedTokenSigns));
                    }
                }
            }
        };
        // schedule the task
        Timer timer = new Timer("Token Timer");
        timer.scheduleAtFixedRate(repeatedTask, Constants.CRITICAL_TUPLE_TIMER_DELAY, Constants.CRITICAL_TUPLE_TIMER_PERIOD);
    }

    public boolean sendQR(LocalDateTime arriveTime, String qr_code) {

        if (qr_code.equals("")) return false;

        if (!qr_code.contains("=")) return false;

        this.arriveTime = arriveTime;
        boolean isValid = false;
        String ri = null;
        String cf = null;
        String hash = null;

        try {

            // format the qr-code by it's parameters
            String[] strings = qr_code.split("=");
            ri = strings[0] + "=";
            cf = strings[1];
            hash = strings[2] + "=";

            // log the first entry in visitor log
            qrCode = new QRCode(ri, cf, hash);

            // get a token
            token = tokenRepository.getToken();

            if (token == null) {
                return false;
            }

            // log the qr-code entries, the visitor token and the time interval
            visitorLogRepository.logQrCode(this.arriveTime, qrCode, token.getSign());

            // send the capsule to mixing proxy for validation
            isValid = visitorServiceMP.sendCapsule(this.arriveTime, this.arriveTime.plusSeconds(Constants.CAPSULE_VISITOR_FLUSH_DELAY), hash, token);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // capsule is valid -> start the timer
        // timer sends every 30 min a capsule to MP
        if (isValid) {
            stopLoggingProperty.set(false);
            stopLogging = false;
            // update the time interval GUI
            timeInterval.set(arriveTime.toString() + " - " + arriveTime.plusSeconds(Constants.CAPSULE_VISITOR_FLUSH_DELAY).toString());
            try {
                startTokenTimer(hash);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return isValid;
    }

    public Image getImage(boolean b) {
        BufferedImage bf = visitorServiceMP.getBufferedImage(b);


        WritableImage wr = null;
        if (bf != null) {
            wr = new WritableImage(bf.getWidth(), bf.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < bf.getWidth(); x++) {
                for (int y = 0; y < bf.getHeight(); y++) {
                    pw.setArgb(x, y, bf.getRGB(x, y));
                }
            }
        }

        ImageView imView = new ImageView(wr);


        return imView.getImage();
    }

    public boolean authenticate(String phoneNumber) {

        // normally the phone number is stored in the systems telephony service
        // directory name is bound to the phone number
        // easier separation of csv files for demo purposes
        Config.DIR_NAME = "dir_visitor_" + phoneNumber;
        dataManager.initFiles();

        // authenticate user with registrar
        if (visitorServiceRegistrar.authenticate(phoneNumber)) {
            startCriticalTupleTimer();
            return true;
        }
        return false;
    }

    public void exit() {
        if (temp != null) {
            LocalDateTime exitTime = LocalDateTime.now();

            // 10 min of safety margin when a visitor pushes the leave button// 10 min of safety margin when a visitor pushes the leave button
            exitTime = exitTime.plusMinutes(Constants.SAFETY_MARGIN);
            try {
                if (token != null) {
                    visitorLogRepository.logQrCodeExitTime(temp, exitTime, qrCode, token.getSign());
                    visitorServiceMP.updateCapsule(temp, exitTime, qrCode.getHash(), token);
                    visitorServiceMP.removeProof();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            stopLogging = true;
        }
    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
}
