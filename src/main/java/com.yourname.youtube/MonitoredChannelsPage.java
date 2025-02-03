package com.yourname.youtube;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MonitoredChannelsPage extends VBox {
    private final Main mainApp;
    private final Label apiUsageLabel;

    public MonitoredChannelsPage(Main mainApp, Stage primaryStage) {
        this.mainApp = mainApp;
        setSpacing(15);
        setStyle("-fx-background-color: #2e2e2e; -fx-padding: 20;");

        Label titleLabel = new Label("Monitored Channels");
        titleLabel.setStyle("-fx-font-size: 24px;");

        VBox monitoredBox = new VBox(10);
        displayMonitoredChannels(monitoredBox);

        // API Usage Counter
        apiUsageLabel = new Label("API Usage: " + mainApp.getApiUsage() + " / 10000");
        Button refreshUsageButton = new Button("Refresh Usage");
        refreshUsageButton.setOnAction(event -> updateApiUsage());

        VBox usageBox = new VBox(apiUsageLabel, refreshUsageButton);
        usageBox.setStyle("-fx-padding: 10px; -fx-background-color: #3e3e3e; -fx-border-radius: 5;");

        Button backButton = new Button("Back to Follow Channels");
        backButton.setOnAction(event -> {
            FollowChannelsPage followChannelsPage = new FollowChannelsPage(mainApp, primaryStage);
            Scene followScene = new Scene(followChannelsPage, 1280, 800);
            followScene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
            primaryStage.setScene(followScene);
        });

        getChildren().addAll(titleLabel, monitoredBox, usageBox, backButton);
    }

    private void displayMonitoredChannels(VBox monitoredBox) {
        monitoredBox.getChildren().clear();
        for (Main.Channel channel : mainApp.monitoredChannels) {
            Main.Video latestVideo = mainApp.fetchLatestVideoDetails(channel.getId());

            HBox channelBox = new HBox(10);
            channelBox.setStyle("-fx-background-color: #3e3e3e; -fx-padding: 10; -fx-border-radius: 10;");

            ImageView thumbnail = new ImageView(new Image(channel.getThumbnailUrl()));
            thumbnail.setFitWidth(50);
            thumbnail.setFitHeight(50);

            VBox details = new VBox(
                    new Label("Channel: " + channel.getTitle()),
                    latestVideo != null ? new Label("Latest Video: " + latestVideo.getTitle()) : new Label("No recent videos"),
                    latestVideo != null ? new Label("Uploaded on: " + latestVideo.getUploadTime()) : new Label("")
            );

            Button watchButton = new Button("Watch Video");
            watchButton.setOnAction(event -> {
                if (latestVideo != null) {
                    mainApp.openVideoInBrowser(latestVideo.getUrl());
                }
            });

            channelBox.getChildren().addAll(thumbnail, details, watchButton);
            monitoredBox.getChildren().add(channelBox);
        }
    }

    private void updateApiUsage() {
        apiUsageLabel.setText("API Usage: " + mainApp.getApiUsage() + " / 10000");
    }
}
