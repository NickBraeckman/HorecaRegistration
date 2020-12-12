package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class VisitorApplication extends Application {
    private final String title = "Horeca Registration System";
    private String phoneNumber;
    private VisitorController controller = new VisitorController();
    public Stage primaryStage;
    private Button btn;
    private Button exitBtn;
    private TextField qr_code;
    private final String enterPhoneNumber = "Phone number ...";

    private Label proof = new Label("NO PROOF YET");
    private Stage stage2;
    private StackPane root;
    private FlowPane fp;
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.controller = new VisitorController();
        this.primaryStage = primaryStage;
        controller.start();
        buildLoginScene();
    }

    private void buildLoginScene() {
        primaryStage.setTitle("Login");
        HBox l_hb = new HBox();

        //phonenumber
        TextField l_tf = new TextField();
        l_tf.setPromptText(enterPhoneNumber);
        l_hb.getChildren().add(l_tf);

        //button
        Button l_btn = new Button("Login");
        l_btn.setOnAction(event -> authenticate(l_tf));
        l_hb.getChildren().add(l_btn);

        StackPane l_root = new StackPane();
        l_root.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) authenticate(l_tf);
        });
        l_root.getChildren().add(l_hb);
        primaryStage.setScene(new Scene(l_root));


        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(1);
        });

        primaryStage.show();
    }

    // still need to authenticate with phone number
    private void authenticate(TextField tf) {
        boolean status = controller.authenticate(tf.getText());

        // boolean isPhoneNumber = Pattern.matches("(^\\+[0-9]{2}|^\\+[0-9]{2}\\(0\\)|^\\(\\+[0-9]{2}\\)\\(0\\)|^00[0-9]{2}|^0)([0-9]{9}$|[0-9\\-\\s]{10}$)",tf.toString());
        if (status) {
            phoneNumber = tf.getText();
            buildMainScene();
        } else {
            tf.clear();
        }
    }


    private void buildMainScene() {
        primaryStage.setTitle("Visitor: " +phoneNumber);
        fp = new FlowPane();
        //qr code
        qr_code = new TextField();
        qr_code.setPromptText("enter qr_code");
        fp.getChildren().add(qr_code);

        //button
        btn = new Button("send_qr_code");
        btn.setOnAction(event -> sendQR(qr_code));
        fp.getChildren().add(btn);

        // exit button only visible when qr-code is approved
        exitBtn = new Button("exit");
        exitBtn.setOnAction(event -> exit());
        fp.getChildren().add(exitBtn);
        exitBtn.setVisible(false);

        //label with proof of registration
        fp.getChildren().add(proof);

        root = new StackPane();
        root.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) sendQR(qr_code);
        });
        root.getChildren().add(fp);

        scene = new Scene(root, 300, 300);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            exit();
            Platform.exit();
            System.exit(1);
        });
        primaryStage.show();

        // observer when periodical sending of capsule fails/ not valid
        controller.stopLogging().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    exit();
                }
            }
        });
    }

    private void sendQR(TextField tf) {
        if (stage2 != null) {
            stage2.close();
        }
        boolean isValid = controller.sendQR(LocalDateTime.now(), tf.getText());
        if (isValid) {
            // set qr-code textfield and send btn not visible
            // visitor can't send another qr-code entry whitout pushing the exit button
            this.proof.setText("Successful checked QR-Code");

            btn.setVisible(false);
            exitBtn.setVisible(true);
            qr_code.setVisible(false);
        }

        // TODO random niet echte qr code intypen -> geeft wit scherm ipv 9 blokken met grijswaarden
        newStage2(controller.getImage(isValid));

    }

    private void newStage2(Image image) {
        ImageView proofImageView = new ImageView();
        proofImageView.setImage(image);
        stage2 = new Stage();
        stage2.setTitle("Visitor: " +phoneNumber);
        AnchorPane ap = new AnchorPane();
        ap.getChildren().add(proofImageView);
        stage2.setScene(new Scene(ap));
        stage2.show();
    }


    public void exit() {
        if (stage2 != null) {
            stage2.close();
        }
        controller.exit();
        qr_code.setVisible(true);
        qr_code.setText("");
        btn.setVisible(true);
        exitBtn.setVisible(false);
        this.proof.setText("");
    }
}
