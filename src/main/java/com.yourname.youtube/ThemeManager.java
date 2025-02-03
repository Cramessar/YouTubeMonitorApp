package com.yourname.youtube;

import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class ThemeManager {
    public HBox createThemeSelector() {
        // Dropdown for theme selection
        ComboBox<String> themeDropdown = new ComboBox<>();
        themeDropdown.getItems().addAll("Dark Theme", "Light Theme", "System Theme");
        themeDropdown.setValue("Dark Theme");

        // Handle theme selection
        themeDropdown.setOnAction(e -> {
            String selectedTheme = themeDropdown.getValue();
            System.out.println("Theme selected: " + selectedTheme);
            // This is where theme switching logic will go
        });

        // Horizontal layout for the dropdown
        return new HBox(10, themeDropdown);
    }
}

