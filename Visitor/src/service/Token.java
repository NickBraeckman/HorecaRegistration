package service;

import java.io.Serializable;
import java.time.LocalDate;

public class Token implements Serializable {
    public String sign;
    public String data;

    public Token() {
    }

    public Token(String sign, String data) {
        this.sign = sign;
        this.data = data;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "token[" + sign + ", " + data +"]";
    }
}
