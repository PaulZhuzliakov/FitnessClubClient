package org.demo;

import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

public class Alerts {

    public static void showAlert(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }


    static void showEmptyFieldsAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText("Нет данных для поиска");
        alert.setContentText("Заполните хотя бы один параметр для поиска");
        alert.showAndWait();
    }

    static void showNoElementSelectedAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText("Не выбран клиент");
        alert.setContentText("а");
        alert.showAndWait();
    }

}
