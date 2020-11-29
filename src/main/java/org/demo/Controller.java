package org.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.StageStyle;
import org.demo.model.ClubClient;
import org.demo.model.VisitDate;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Controller implements Initializable {
    @FXML
    TextField lastname, firstname, middlename, testField;
    @FXML
    TableView<ClubClient> table_clients;
    @FXML
    TableColumn<ClubClient, Integer> col_cardNumber;
    @FXML
    TableColumn<ClubClient, String> col_lastName, col_firstName, col_middleName;
    @FXML
    TableView<VisitDate> table_attendance;
    @FXML
    TableColumn<VisitDate, Date> col_dates;
    @FXML
    Label lbl_visit;

    String server;
    String service;
    String clients;
    String getByFio;
    String visits;
    String getYearVisits;
    String tests;
    int membershipCardCost;

    ObservableList<ClubClient> listOfClients;
    ObservableList<VisitDate> listOfVisitDates;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setProperties();
        //инициалицация столбцов таблицы клиентов
        col_cardNumber.setCellValueFactory(new PropertyValueFactory<ClubClient, Integer>("clubCardNumber"));
        col_lastName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("lastName"));
        col_firstName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("firstName"));
        col_middleName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("middleName"));
        //инициалицация столбцов таблицы посещаемости
        col_dates.setCellValueFactory(new PropertyValueFactory<VisitDate, Date>("date"));
        refreshTable();
    }

    public void setProperties() {
        try (FileReader reader = new FileReader("src/main/resources/config.properties")) {
            Properties properties = new Properties();
            properties.load(reader);
            server = properties.getProperty("server");
            service = properties.getProperty("service");
            clients = properties.getProperty("clients");
            getByFio = properties.getProperty("getByFio");
            visits = properties.getProperty("visits");
            getYearVisits = properties.getProperty("getYearVisits");
            tests = properties.getProperty("tests");
            membershipCardCost = Integer.valueOf(properties.getProperty("membershipCardCost"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //ниже следуют методы для работы с таблицой клиентов

    //возвращает список всех клиентов и выводит в таблице
    public void refreshTable() {
        Client client = ClientBuilder.newClient();
        String url = server + service + clients;
        String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<ClubClient> listOfMappedClients = null;
        try {
            listOfMappedClients = objectMapper.readValue(json, new TypeReference<List<ClubClient>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        listOfClients = FXCollections.observableArrayList(listOfMappedClients);
        table_clients.setItems(listOfClients);
        //добавить логику отсутствия соединения с сервером. а то клиент не запускается, если сервер не ответил
    }

    //возвращает список клиентов по ФИО, введёным в соответствующих полях и выводит в таблице
    public void btnGETList(ActionEvent actionEvent) throws IOException {

        String filtering = "?lastname=" + lastname.getText() + "&firstname=" + firstname.getText() + "&middlename=" + middlename.getText();
        if (new String(lastname.getText() + firstname.getText() + middlename.getText()).equals("")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UTILITY);
            alert.setHeaderText("Клиент не найден1");
            alert.setContentText("Проверьте корректность введеных данных");
            alert.showAndWait();
        } else {
            String url = server + service + clients + getByFio + filtering;

            Client client = ClientBuilder.newClient();
            String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<ClubClient> listOfMappedClients = null;
            try {
                listOfMappedClients = objectMapper.readValue(json, new TypeReference<List<ClubClient>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (listOfMappedClients.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initStyle(StageStyle.UTILITY);
                alert.setHeaderText("Нет данных для поиска");
                alert.setContentText("Заполните хотя бы оин параметр для поиска");
                alert.showAndWait();
            } else {
                //вывод в таблице
                listOfClients = FXCollections.observableArrayList(listOfMappedClients);
                table_clients.setItems(listOfClients);
            }
        }
    }

    //отправляет данные о пользователе, которого надо создать по ФИО, введёным в соответствующих полях
    public void btnPOST(ActionEvent actionEvent) {
        ClubClient clubClient = new ClubClient(lastname.getText(), firstname.getText(), middlename.getText());
        Client client = ClientBuilder.newClient();
        String url = server + service + clients;
        WebTarget target = client.target(url);
        target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .post(Entity.json(clubClient), String.class);
        refreshTable();
    }

    //отправляет данные о пользователе, которого надо редактировать
    //меняет у уже имеющегося пользователя ФИО, введёные в соответствующих полях
    public void btnPUT(ActionEvent actionEvent) {
        TableView.TableViewSelectionModel<ClubClient> selectionModel = table_clients.getSelectionModel();
        int id = selectionModel.getSelectedItem().getId();

        ClubClient clubClient = new ClubClient(lastname.getText(), firstname.getText(), middlename.getText());
        Client client = ClientBuilder.newClient();
        String url = server + service + clients+id;
        WebTarget target = client.target(url);
        target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .put(Entity.json(clubClient), String.class);
        refreshTable();
    }

    //удаляет выделенного пользователя
    public void btnDELETE(ActionEvent actionEvent) {
        TableView.TableViewSelectionModel<ClubClient> selectionModel = table_clients.getSelectionModel();
        int id = selectionModel.getSelectedItem().getId();
        String url = server + service + clients + id;
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        target.request().delete();
        refreshTable();
    }


    //ниже следуют методы для работы с таблицой посещаемости

    //выбрать клиента в таблице клиентов и отобразить в таблице посещаемости данные о датах посещения выбранного клиента
    public void btnConfirmVisit(ActionEvent actionEvent) {
        int id = table_clients.getSelectionModel().getSelectedItem().getId();
        java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        String url = server + service + visits;
        VisitDate visitDate = new VisitDate(id, currentDate);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .post(Entity.json(visitDate), String.class);
    }

    //отобразить в таблице посещений, даты посещения выбранного клиента
    public void btnViewVisits(ActionEvent actionEvent) {
        int id = table_clients.getSelectionModel().getSelectedItem().getId();
        String url = server + service + visits + id;

        Client client = ClientBuilder.newClient();
        String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<VisitDate> listOfMappedDates = null;
        try {
            listOfMappedDates = objectMapper.readValue(json, new TypeReference<List<VisitDate>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        listOfVisitDates = FXCollections.observableArrayList(listOfMappedDates);
        table_attendance.setItems(listOfVisitDates);

        String txt = table_clients.getSelectionModel().getSelectedItem().getLastName() + " "
                + table_clients.getSelectionModel().getSelectedItem().getFirstName();
        lbl_visit.setText(txt);

    }

    //расчет стоимости нового абонемента, исходя из количества посещений за прошедший год начиная с сегодняшнего дня
    public void btnCalculateMembershipCard(ActionEvent actionEvent) {
        if (table_clients.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Клиент не выбран");
            alert.setContentText("Выделите клиента в таблице и попробуёте ещё раз");
            alert.showAndWait();
        } else {
            int id = table_clients.getSelectionModel().getSelectedItem().getId();
            String url = server + service + visits + getYearVisits + id;

            Client client = ClientBuilder.newClient();
            String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            int visitsInYear = 0;
            try {
                visitsInYear = objectMapper.readValue(json, Integer.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            float percentage = (float) visitsInYear / 365 * 100;
            int cost;
            int discount;
            if (percentage < 35) {
                discount = 0;
                cost = membershipCardCost;
            } else if (percentage >= 35 && percentage < 60) {
                discount = 10;
                cost = (int) (membershipCardCost * 0.9);
            } else {
                discount = 15;
                cost = (int) (membershipCardCost * 0.85);
            }

            String msg = "Количество посещений за прошедший год состовляет " + visitsInYear + " дней"
                    + "\n" + "Это " + String.format("%.0f", percentage) + " % дней за прошедший год"
                    + "\n" + "Ваша скидка составляет составляет: " + discount + " %"
                    + "\n" + "Стоимость нового абонемента составляет: " + cost + " рублей";
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Рассчет стоимости абонемента");
//        alert.setHeaderText("HeaderText");
            alert.setContentText(msg);
            alert.showAndWait();
        }
    }

    //добавить в БД требуемое количество посещений для клиента по его id
    public void btnTestButton(ActionEvent actionEvent) {
        int id = table_clients.getSelectionModel().getSelectedItem().getId();
        int repeats = Integer.parseInt(testField.getText());
        String filtering = "?id=" + id + "&days=" + repeats;
        String url = server + service + tests + filtering;
        Client client = ClientBuilder.newClient();
        client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);

    }
}