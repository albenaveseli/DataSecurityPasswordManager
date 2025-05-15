package controller;

import encryption.AESCrypto;
import encryption.MasterPasswordManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class MasterPasswordController {
    @FXML private PasswordField masterPasswordField;
    @FXML private Button unlockButton;
    @FXML private Label messageLabel;

    @FXML
    private void unlock() {
        String masterPass = masterPasswordField.getText();
        if (masterPass == null || masterPass.isEmpty()) {
            messageLabel.setText("Master password cannot be empty!");
            return;
        }

        try {
            AESCrypto.initKeyFromPassword(masterPass);

            if (!MasterPasswordManager.tokenExists()) {
                System.out.println("Creating token...");
                String token = AESCrypto.encrypt("Test123");
                MasterPasswordManager.saveToken(token);
            } else {
                String encryptedToken = MasterPasswordManager.loadToken();
                System.out.println("Encrypted token read: " + encryptedToken);

                if (encryptedToken == null || encryptedToken.trim().isEmpty()) {
                    // Token bosh, krijo token te ri
                    String token = AESCrypto.encrypt("Test123");
                    MasterPasswordManager.saveToken(token);
                } else {
                    String decryptedToken = AESCrypto.decrypt(encryptedToken);
                    System.out.println("Decrypted token: " + decryptedToken);

                    if (!"Test123".equals(decryptedToken)) {
                        messageLabel.setText("Incorrect master password!");
                        return;
                    }
                }
            }

            Stage stage = (Stage) unlockButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PasswordManagerView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Password Manager");
            stage.show();

        } catch (javax.crypto.BadPaddingException e) {
            messageLabel.setText("Incorrect master password!");
        } catch (Exception e) {
            messageLabel.setText("Failed to initialize encryption key or verify token.");
            e.printStackTrace();
        }
    }
}
