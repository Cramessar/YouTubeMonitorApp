package com.yourname.youtube;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Main extends Application {
    private String apiKey = "";
    protected final List<Channel> monitoredChannels = new ArrayList<>();
    private final Map<String, String> lastVideoIds = new HashMap<>();
    private int apiUsage = 0;
    private final int DAILY_API_LIMIT = 10000;
    private String lastUsageDate = "";

    @Override
    public void start(Stage primaryStage) {
        apiKey = loadApiKey();
        loadApiUsage(); // Load API usage from file and reset if needed

        ApiManagementPage apiManagementPage = new ApiManagementPage(primaryStage, this);

        Scene scene = new Scene(apiManagementPage, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
        primaryStage.setScene(scene);

        primaryStage.setTitle("YouTube Monitor App");
        primaryStage.show();

        scheduleVideoChecks(5);
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getApiUsage() {
        return apiUsage;
    }

    public int getDailyApiLimit() {
        return DAILY_API_LIMIT;
    }

    public void incrementApiUsage(int amount) {
        apiUsage += amount;
        saveApiUsage(); // Save updated usage count
    }

    public List<Channel> searchChannels(String query) {
        incrementApiUsage(100);
        List<Channel> channels = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String urlStr = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=channel&q="
                    + encodedQuery + "&key=" + apiKey;

            URL url = new URI(urlStr).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray items = response.getAsJsonArray("items");

                for (int i = 0; i < items.size(); i++) {
                    JsonObject item = items.get(i).getAsJsonObject();
                    JsonObject snippet = item.getAsJsonObject("snippet");
                    String channelId = item.getAsJsonObject("id").get("channelId").getAsString();
                    String title = snippet.get("title").getAsString();
                    String description = snippet.get("description").getAsString();
                    String thumbnailUrl = snippet.getAsJsonObject("thumbnails")
                            .getAsJsonObject("default")
                            .get("url").getAsString();

                    channels.add(new Channel(channelId, title, description, thumbnailUrl));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channels;
    }

    public void displayMonitoredChannels(VBox monitoredBox) {
        monitoredBox.getChildren().clear();
        for (Channel channel : monitoredChannels) {
            HBox channelBox = new HBox(10);

            ImageView thumbnail = new ImageView(new Image(channel.getThumbnailUrl()));
            thumbnail.setFitWidth(50);
            thumbnail.setFitHeight(50);

            Video latestVideo = fetchLatestVideoDetails(channel.getId());

            VBox details = new VBox(
                    new Label(channel.getTitle()),
                    latestVideo != null ? new Label("Latest video: " + latestVideo.getTitle()) : new Label("No recent videos"),
                    latestVideo != null ? new Label("Uploaded on: " + latestVideo.getUploadTime()) : new Label("")
            );

            Button watchButton = new Button("Watch Video");
            watchButton.setOnAction(event -> {
                if (latestVideo != null) {
                    openVideoInBrowser(latestVideo.getUrl());
                }
            });

            Button removeButton = new Button("Remove");
            removeButton.setOnAction(event -> {
                monitoredChannels.remove(channel);
                displayMonitoredChannels(monitoredBox);
            });

            channelBox.getChildren().addAll(thumbnail, details, watchButton, removeButton);
            monitoredBox.getChildren().add(channelBox);
        }
    }

    private void scheduleVideoChecks(int intervalMinutes) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::checkForNewVideos, 0, intervalMinutes, TimeUnit.MINUTES);
    }

    private void checkForNewVideos() {
        for (Channel channel : monitoredChannels) {
            Video latestVideo = fetchLatestVideoDetails(channel.getId());

            if (latestVideo == null) {
                System.out.println("No video found for " + channel.getTitle());
                continue;
            }

            String latestVideoId = latestVideo.getId();

            if (!latestVideoId.equals(lastVideoIds.get(channel.getId()))) {
                lastVideoIds.put(channel.getId(), latestVideoId);
                notifyUser(channel.getTitle(), latestVideo.getUrl());
            }
        }
    }

    public Video fetchLatestVideoDetails(String channelId) {
        incrementApiUsage(1);
        try {
            String urlStr = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" +
                    channelId + "&order=date&type=video&maxResults=1&key=" + apiKey;

            URL url = new URI(urlStr).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray items = response.getAsJsonArray("items");

                if (items.size() > 0) {
                    JsonObject video = items.get(0).getAsJsonObject();
                    JsonObject snippet = video.getAsJsonObject("snippet");

                    String videoId = video.getAsJsonObject("id").get("videoId").getAsString();
                    String videoTitle = snippet.get("title").getAsString();
                    String uploadTime = snippet.get("publishTime").getAsString();

                    Instant instant = Instant.parse(uploadTime);
                    LocalDateTime dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm a");
                    String formattedTime = dateTime.format(formatter);

                    String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

                    return new Video(videoId, videoTitle, formattedTime, videoUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadApiUsage() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("api_usage.properties")) {
            properties.load(fis);
            lastUsageDate = properties.getProperty("lastUsageDate", "");
            apiUsage = Integer.parseInt(properties.getProperty("apiUsage", "0"));

            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (!lastUsageDate.equals(currentDate)) {
                apiUsage = 0; // Reset API usage at midnight
                lastUsageDate = currentDate;
                saveApiUsage();
            }
        } catch (IOException e) {
            System.out.println("No previous API usage record found, starting fresh.");
            resetApiUsage();
        }
    }

    public void saveApiUsage() {
        Properties properties = new Properties();
        try (FileOutputStream fos = new FileOutputStream("api_usage.properties")) {
            properties.setProperty("lastUsageDate", lastUsageDate);
            properties.setProperty("apiUsage", String.valueOf(apiUsage));
            properties.store(fos, "YouTube API Usage");
        } catch (IOException e) {
            System.out.println("Error saving API usage.");
        }
    }

    public void resetApiUsage() {
        lastUsageDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        apiUsage = 0;
        saveApiUsage();
    }


    public void openVideoInBrowser(String videoUrl) {
        try {
            URI videoUri = new URI(videoUrl);
            java.awt.Desktop.getDesktop().browse(videoUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyUser(String channelName, String videoUrl) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "New video posted on " + channelName + "!", ButtonType.OK);
            alert.setHeaderText("New Video Alert");
            alert.setContentText("Click OK to open in browser.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                openVideoInBrowser(videoUrl);
            }
        });
    }

    public String loadApiKey() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
            return properties.getProperty("apiKey", "");
        } catch (IOException e) {
            System.out.println("No API key found.");
        }
        return "";
    }

    public void saveApiKey(String apiKey) {
        Properties properties = new Properties();
        try (FileOutputStream fos = new FileOutputStream("config.properties")) {
            properties.setProperty("apiKey", apiKey);
            properties.store(fos, "YouTube API Key");
        } catch (IOException e) {
            System.out.println("Error saving API key.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class Video {
        private final String id;
        private final String title;
        private final String uploadTime;
        private final String url;

        public Video(String id, String title, String uploadTime, String url) {
            this.id = id;
            this.title = title;
            this.uploadTime = uploadTime;
            this.url = url;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getUploadTime() { return uploadTime; }
        public String getUrl() { return url; }
    }

    static class Channel {
        private final String id;
        private final String title;
        private final String description;
        private final String thumbnailUrl;

        public Channel(String id, String title, String description, String thumbnailUrl) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getThumbnailUrl() { return thumbnailUrl; }
    }
}