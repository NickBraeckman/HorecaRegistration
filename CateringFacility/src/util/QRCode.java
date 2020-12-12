package util;

import java.io.Serializable;
import java.util.Base64;

public class QRCode implements Serializable {
    private byte[] Ri;
    private long CF;
    private byte[] hash;

    public QRCode(byte[] ri, long CF, byte[] hash) {
        this.Ri = ri;
        this.CF = CF;
        this.hash = hash;
    }

    public byte[] getRi() {
        return Ri;
    }

    public long getCF() {
        return CF;
    }

    public byte[] getHash() {
        return hash;
    }

    public String getFormattedQRCode(){
        StringBuilder builder = new StringBuilder();

        String random = Base64.getEncoder().encodeToString(Ri);
        builder.append(random);

        String businessNumber = Long.toString(CF);
        builder.append(businessNumber);
        builder.append("=");

        String hashValue = Base64.getEncoder().encodeToString(hash);
        builder.append(hashValue);

        return builder.toString();
    }


}
