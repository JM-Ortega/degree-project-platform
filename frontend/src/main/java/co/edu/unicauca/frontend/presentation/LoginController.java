package co.edu.unicauca.frontend.presentation;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }

    @FXML
    private void onLogin() {
        String email = emailField.getText();
        String pass = passwordField.getText();

        if (email == null || email.isBlank() || pass == null || pass.isBlank()) {
            showError("Por favor completa correo y contraseña.");
            return;
        }

        // Stub: solo para ver que funciona la UI
        hideError();
        Alert ok = new Alert(Alert.AlertType.INFORMATION, "✅ Login stub OK (sin API).");
        ok.setHeaderText(null);
        ok.setTitle("Login");
        ok.showAndWait();
    }

    @FXML
    private void onGoToRegister() {
        Alert a = new Alert(Alert.AlertType.INFORMATION, "Navegación a registro (stub).");
        a.setHeaderText(null);
        a.setTitle("Registro");
        a.showAndWait();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }

    private void hideError() {
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);
        errorLabel.setText(null);
    }
}
