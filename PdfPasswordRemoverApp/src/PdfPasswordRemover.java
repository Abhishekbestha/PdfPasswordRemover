import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.File;
import java.io.FileOutputStream;

public class PdfPasswordRemover extends Application {

    private File selectedFile;
    private PasswordField passwordField;
    private TextField passwordTextField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("PDF Password Remover");

        // Button to choose file
        Button chooseFileButton = new Button("Choose PDF File");
        Label fileLabel = new Label("No file selected");

        // Password field for input (initially hidden)
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter PDF password");

        // Text field for displaying the password (for "show" mode)
        passwordTextField = new TextField();
        passwordTextField.setPromptText("Enter PDF password");

        // Initially, the passwordTextField is invisible
        passwordTextField.setVisible(false);

        // StackPane to stack both password fields on top of each other
        StackPane passwordPane = new StackPane(passwordField, passwordTextField);

        // Checkbox to toggle between showing and hiding the password
        CheckBox showPasswordCheckBox = new CheckBox("Show Password");

        // Button to remove the password
        Button removePasswordButton = new Button("Remove Password");

        // Set up file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        // Handle file selection
        chooseFileButton.setOnAction(e -> {
            selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                fileLabel.setText("Selected file: " + selectedFile.getName());
            } else {
                fileLabel.setText("No file selected");
            }
        });

        // Toggle password visibility
        showPasswordCheckBox.setOnAction(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordTextField.setText(passwordField.getText()); // Sync the password
                passwordField.setVisible(false);
                passwordTextField.setVisible(true);
            } else {
                passwordField.setText(passwordTextField.getText()); // Sync the password
                passwordField.setVisible(true);
                passwordTextField.setVisible(false);
            }
        });

        // Handle password removal
        removePasswordButton.setOnAction(e -> {
            String password = showPasswordCheckBox.isSelected() ? passwordTextField.getText() : passwordField.getText();
            if (selectedFile != null && !password.isEmpty()) {
                removePdfPasswordWithIText(selectedFile, password);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a file and enter the password.");
            }
        });

        // Layout setup
        VBox root = new VBox(10, chooseFileButton, fileLabel, passwordPane, showPasswordCheckBox, removePasswordButton);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 400, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to remove PDF password using iText
    private void removePdfPasswordWithIText(File file, String password) {
        try {
            // Create PdfReader object with the password
            PdfReader reader = new PdfReader(file.getAbsolutePath(), password.getBytes());

            // Choose where to save the unlocked PDF
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("unlocked_" + file.getName());
            File saveFile = fileChooser.showSaveDialog(null);

            if (saveFile != null) {
                // Use PdfStamper to save the unlocked PDF
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(saveFile));
                stamper.close();
                reader.close();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Password removed successfully.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove password: " + e.getMessage());
        }
    }

    // Helper method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
