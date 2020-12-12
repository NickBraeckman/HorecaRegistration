package main;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Visitor {
    //UNIQUE IDENTIFIER
    private long user_id;
    // PERSON INFO
    private String surname, lastname;
    private long phoneNumber;
    //LOGIN STUFF
    private String passwd;
    private String salt;
    private List<byte[]> dailyTokens;
}
