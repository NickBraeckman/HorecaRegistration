package service;

import main.Visitor;
import main.VisitorApplication;
import main.VisitorController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.*;

public class VisitorServiceMP extends UnicastRemoteObject implements VisitorServiceMPInterface {
    private byte[] ACK = null;
    private MPServiceVisitorInterface mixingProxyService;
    BufferedImage bi;

    public VisitorServiceMP(MPServiceVisitorInterface mixingProxyService) throws RemoteException {
        this.mixingProxyService = mixingProxyService;
    }

    /**
     * receive an acknowledgment from the mixing proxy
     * the validity of the QR-code is approved
     * generate a cf and day specific visual representation
     * @param acknowledgement send by Mixing Proxy
     */
    @Override
    public void receiveAcknowledgment(Acknowledgement acknowledgement) {
        ACK = acknowledgement.sign.getBytes();
        bi = getDailyAck(ACK);
        ACK = null;
    }

    public BufferedImage getBufferedImage(boolean b) {
        return bi;
    }

    public static BufferedImage getDailyAck(byte[] bytes) {
        long seed2 = -1;
        for (byte b : bytes) seed2 += b;
        return getMatrixSquare(200, seed2, false);
    }

    public void removeProof() {
        bi = getMatrixSquare(200, -1, true);
    }

    private static BufferedImage getMatrixSquare(int pixelSize, long seed, boolean black) {
        int width = pixelSize * 3;
        int height = pixelSize * 3;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Random random = (seed < 0) ? new Random() : new Random(seed);
        List<Integer> pixels = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) pixels.add(getPseudoRandomPixel(random, black));

        for (int y = 0; y < pixelSize; y++) {
            for (int x = 0; x < pixelSize; x++) img.setRGB(x, y, pixels.get(0));
            for (int x = pixelSize; x < pixelSize * 2; x++) img.setRGB(x, y, pixels.get(1));
            for (int x = pixelSize * 2; x < pixelSize * 3; x++) img.setRGB(x, y, pixels.get(2));
        }
        for (int y = pixelSize; y < pixelSize * 2; y++) {
            for (int x = 0; x < pixelSize; x++) img.setRGB(x, y, pixels.get(3));
            for (int x = pixelSize; x < pixelSize * 2; x++) img.setRGB(x, y, pixels.get(4));
            for (int x = pixelSize * 2; x < pixelSize * 3; x++) img.setRGB(x, y, pixels.get(5));
        }
        for (int y = pixelSize * 2; y < pixelSize * 3; y++) {
            for (int x = 0; x < pixelSize; x++) img.setRGB(x, y, pixels.get(6));
            for (int x = pixelSize; x < pixelSize * 2; x++) img.setRGB(x, y, pixels.get(7));
            for (int x = pixelSize * 2; x < pixelSize * 3; x++) img.setRGB(x, y, pixels.get(8));
        }
        return img;
    }

    private static int getPseudoRandomPixel(Random random, boolean black) {
        int alpha = random.nextInt(256);
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        if (black) alpha = red = green = blue;

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * 2.1 register a visit
     * send a capsule to the Mixing Proxy after scanning the QR-code
     * @param arriveTime
     * @param stopTime
     * @param hash
     * @param token
     * @return
     */
    public boolean sendCapsule(LocalDateTime arriveTime, LocalDateTime stopTime, String hash, Token token) {

        Capsule capsule = initCapsule(arriveTime, stopTime, hash, token);

        if (capsule.getToken() == null) {
            return false;
        }

        try {
            if (!mixingProxyService.sendCapsule(capsule, this)) {
                return false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 2.1 register a visit
     * update the exit time of the last sent capsule
     * @param arriveTime
     * @param stopTime
     * @param hash
     * @param token
     * @return
     */
    public boolean updateCapsule(LocalDateTime arriveTime, LocalDateTime stopTime, String hash, Token token) {

        Capsule capsule = initCapsule(arriveTime, stopTime, hash, token);

        if (capsule.getToken() == null) {
            return false;
        }

        try {
            if (!mixingProxyService.updateCapsule(capsule, this)) {
                return false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Capsule initCapsule(LocalDateTime arriveTime, LocalDateTime stopTime, String hash, Token token) {
        Capsule capsule = new Capsule();
        capsule.setHash(hash);
        capsule.setStartTime(arriveTime);
        capsule.setStopTime(stopTime);
        capsule.setToken(token);
        return capsule;
    }
}
