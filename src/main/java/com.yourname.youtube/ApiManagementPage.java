package com.yourname.youtube;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ApiManagementPage extends VBox {
    private final Main mainApp;
    private final Stage primaryStage;

    public ApiManagementPage(Stage primaryStage, Main mainApp) {
        this.mainApp = mainApp;
        this.primaryStage = primaryStage;

        setSpacing(15);
        setStyle("-fx-background-color: #2e2e2e; -fx-padding: 20;");

        Label titleLabel = new Label("API Management");
        titleLabel.setStyle("-fx-font-size: 24px;");

        PasswordField apiKeyField = new PasswordField();
        apiKeyField.setPromptText("Paste your API key here");


        if (mainApp.getApiKey() != null) {
            apiKeyField.setText(mainApp.getApiKey());
        }

        Label confirmationLabel = new Label();
        confirmationLabel.setStyle("-fx-text-fill: green;");

        Button saveButton = new Button("Validate & Save");
        saveButton.setOnAction(event -> {
            String apiKey = apiKeyField.getText();
            if (!apiKey.isEmpty()) {
                mainApp.saveApiKey(apiKey);
                confirmationLabel.setText("API Key saved successfully!");
            } else {
                confirmationLabel.setText("API Key cannot be empty.");
            }
        });

        Button followChannelsButton = new Button("Go to Followed Channels");
        followChannelsButton.setOnAction(event -> {
            FollowChannelsPage followChannelsPage = new FollowChannelsPage(mainApp, primaryStage);
            Scene followScene = new Scene(followChannelsPage, 1280, 800);
            followScene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
            primaryStage.setScene(followScene);
        });

        getChildren().addAll(titleLabel, apiKeyField, saveButton, confirmationLabel, followChannelsButton);
    }
}
