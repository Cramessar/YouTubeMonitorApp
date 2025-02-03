package com.yourname.youtube;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class FollowChannelsPage extends VBox {
    private final Main mainApp;
    private final VBox searchResultsBox;

    public FollowChannelsPage(Main mainApp, Stage primaryStage) {
        this.mainApp = mainApp;
        setSpacing(15);
        setStyle("-fx-background-color: #2e2e2e; -fx-padding: 20;");

        Label titleLabel = new Label("Follow Channels");
        titleLabel.setStyle("-fx-font-size: 24px;");


        TextField searchField = new TextField();
        searchField.setPromptText("Enter channel name...");

        Button searchButton = new Button("Search");
        searchResultsBox = new VBox(10);

        searchButton.setOnAction(event -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                List<Main.Channel> searchResults = mainApp.searchChannels(query);
                displaySearchResults(searchResults);
            } else {
                searchResultsBox.getChildren().clear();
                searchResultsBox.getChildren().add(new Label("Please enter a search query."));
            }
        });

        HBox searchBox = new HBox(10, searchField, searchButton);
        searchBox.setStyle("-fx-padding: 10px;");


        Button backButton = new Button("Back to API Management");
        backButton.setOnAction(event -> {
            ApiManagementPage apiManagementPage = new ApiManagementPage(primaryStage, mainApp);
            Scene apiScene = new Scene(apiManagementPage, 1280, 800);
            apiScene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
            primaryStage.setScene(apiScene);
        });


        Button viewMonitoredChannelsButton = new Button("View Monitored Channels");
        viewMonitoredChannelsButton.setOnAction(event -> {
            MonitoredChannelsPage monitoredChannelsPage = new MonitoredChannelsPage(mainApp, primaryStage);
            Scene monitoredScene = new Scene(monitoredChannelsPage, 1280, 800);
            monitoredScene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
            primaryStage.setScene(monitoredScene);
        });

        getChildren().addAll(titleLabel, searchBox, searchResultsBox, viewMonitoredChannelsButton, backButton);
    }


    public void displaySearchResults(List<Main.Channel> results) {
        searchResultsBox.getChildren().clear();

        if (results.isEmpty()) {
            searchResultsBox.getChildren().add(new Label("No results found."));
            return;
        }

        for (Main.Channel channel : results) {
            HBox channelBox = new HBox(10);
            channelBox.setStyle("-fx-padding: 10px; -fx-background-color: #2e2e2e; -fx-border-radius: 5;");

            ImageView thumbnail = new ImageView(new Image(channel.getThumbnailUrl()));
            thumbnail.setFitWidth(50);
            thumbnail.setFitHeight(50);

            VBox details = new VBox(
                    new Label(channel.getTitle()),
                    new Label(channel.getDescription())
            );

            Button addButton = new Button("Add");
            addButton.setOnAction(event -> {
                mainApp.monitoredChannels.add(channel);
                addButton.setText("Added");
                addButton.setDisable(true);
            });

            channelBox.getChildren().addAll(thumbnail, details, addButton);
            searchResultsBox.getChildren().add(channelBox);
        }
    }
}
