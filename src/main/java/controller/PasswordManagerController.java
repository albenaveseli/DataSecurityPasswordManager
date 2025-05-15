package controller;

import database.DBConnector;
import encryption.AESCrypto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Credential;
import util.PasswordGenerator;

import java.util.List;

public class PasswordManagerController {
    @FXML private TextField websiteField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button generatePasswordButton;

    @FXML private TextField categoryField;

    @FXML private Button saveButton;
    @FXML private Button loadButton;

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button deleteButton;

    @FXML private ListView<String> credentialsList;
    @FXML private Button decryptButton;

    @FXML private TextArea outputArea;

    private final DBConnector db = new DBConnector();
    private List<Credential> loadedCredentials;

    @FXML
    private void initialize() {
        decryptButton.setDisable(true);
        outputArea.setText("Load or add credentials.");
    }

    @FXML
    private void generatePassword() {
        passwordField.setText(PasswordGenerator.generate());
        outputArea.setText("Strong password generated.");
    }

    @FXML
    private void saveCredential() {
        String website = websiteField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String category = categoryField.getText();

        if (website.isEmpty() || username.isEmpty() || password.isEmpty() || category.isEmpty()) {
            outputArea.setText("Please fill all fields.");
            return;
        }

        try {
            Credential credential = new Credential(
                    AESCrypto.encrypt(website),
                    AESCrypto.encrypt(username),
                    AESCrypto.encrypt(password),
                    AESCrypto.encrypt(category)
            );
            db.saveCredential(credential);
            clearFields();
            outputArea.setText("Credential saved successfully.");
            loadCredentials();
        } catch (Exception e) {
            outputArea.setText("Encryption error.");
            e.printStackTrace();
        }
    }

    @FXML
    private void loadCredentials() {
        loadedCredentials = db.loadAllCredentials();
        ObservableList<String> items = FXCollections.observableArrayList();

        for (Credential cred : loadedCredentials) {
            items.add("ID: " + cred.getId()
                    + " | Website(enc): " + cred.getWebsite()
                    + " | Username(enc): " + cred.getUsername()
                    + " | Password(enc): " + cred.getPassword()
                    + " | Category(enc): " + cred.getCategory());
        }

        credentialsList.setItems(items);
        outputArea.setText("Loaded " + loadedCredentials.size() + " encrypted credentials.");
        decryptButton.setDisable(true);
    }


    @FXML
    private void handleCredentialSelection() {
        decryptButton.setDisable(false);
    }

    @FXML
    private void decryptSelected() {
        int selectedIndex = credentialsList.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            outputArea.setText("Select a credential to decrypt.");
            return;
        }

        Credential cred = loadedCredentials.get(selectedIndex);

        try {
            String website = AESCrypto.decrypt(cred.getWebsite());
            String username = AESCrypto.decrypt(cred.getUsername());
            String password = AESCrypto.decrypt(cred.getPassword());
            String category = AESCrypto.decrypt(cred.getCategory());

            outputArea.setText("ID: " + cred.getId() +
                    "\nWebsite: " + website +
                    "\nUsername: " + username +
                    "\nPassword: " + password +
                    "\nCategory: " + category);
        } catch (Exception e) {
            outputArea.setText("Decryption error.");
            e.printStackTrace();
        }
    }

    @FXML
    private void searchByCategoryOrWebsite() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            outputArea.setText("Enter search keyword.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        try {
            for (Credential cred : db.loadAllCredentials()) {
                String website = AESCrypto.decrypt(cred.getWebsite());
                String category = AESCrypto.decrypt(cred.getCategory());

                if (website.toLowerCase().contains(keyword) || category.toLowerCase().contains(keyword)) {
                    sb.append("ID: ").append(cred.getId())
                            .append("\nWebsite: ").append(website)
                            .append("\nUsername: ").append(AESCrypto.decrypt(cred.getUsername()))
                            .append("\nPassword: ").append(AESCrypto.decrypt(cred.getPassword()))
                            .append("\nCategory: ").append(category)
                            .append("\n----------------------------\n");
                }
            }
            outputArea.setText(sb.length() == 0 ? "No results found." : sb.toString());
        } catch (Exception e) {
            outputArea.setText("Error during decryption/search.");
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteById() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Credential");
        dialog.setHeaderText("Enter ID of the credential to delete:");
        dialog.setContentText("ID:");
        dialog.showAndWait().ifPresent(input -> {
            try {
                int id = Integer.parseInt(input);
                db.deleteCredential(id);
                outputArea.setText("Credential with ID " + id + " deleted.");
                loadCredentials();
            } catch (NumberFormatException e) {
                outputArea.setText("Invalid ID format.");
            }
        });
    }

    private void clearFields() {
        websiteField.clear();
        usernameField.clear();
        passwordField.clear();
        categoryField.clear();
    }
}
