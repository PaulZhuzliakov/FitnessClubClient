package org.demo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.demo.Alerts;
import org.demo.model.ClubClient;
import org.demo.model.VisitDate;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.*;

public class Controller implements Initializable {
    @FXML
    TextField lastname, firstname, middlename, phonenumber, email, testField, testField2;
    @FXML
    TableView<ClubClient> table_clients;
    @FXML
    TableColumn<ClubClient, Integer> col_cardNumber;
    @FXML
    TableColumn<ClubClient, String> col_lastName, col_firstName, col_middleName, col_phoneNumber, col_eMail;
    @FXML
    TableView<VisitDate> table_attendance;
    @FXML
    TableColumn<VisitDate, Date> col_dates;
    @FXML
    Label lbl_visit;

    String server;
    String service;
    String clients;
    String searchClients;
    String visits;
    String getYearVisits;
    String tests;
    int membershipCardCost;

    ObservableList<VisitDate> listOfVisitDates;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getProperties();
        //инициалицация столбцов таблицы клиентов
        col_cardNumber.setCellValueFactory(new PropertyValueFactory<ClubClient, Integer>("clubCardNumber"));
        col_lastName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("lastName"));
        col_firstName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("firstName"));
        col_middleName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("middleName"));
        col_phoneNumber.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("phoneNumber"));
        col_eMail.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("mail"));
        //инициалицация столбцов таблицы посещаемости
        col_dates.setCellValueFactory(new PropertyValueFactory<VisitDate, Date>("date"));
        refreshTable();
    }

    public void getProperties() {
        try (FileReader reader = new FileReader("src/main/resources/config.properties")) {
            Properties properties = new Properties();
            properties.load(reader);
            server = properties.getProperty("server");
            service = properties.getProperty("service");
            clients = properties.getProperty("clients");
            searchClients = properties.getProperty("searchClients");
            visits = properties.getProperty("visits");
            getYearVisits = properties.getProperty("getYearVisits");
            tests = properties.getProperty("tests");
            membershipCardCost = Integer.valueOf(properties.getProperty("membershipCardCost"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //ниже следуют методы для работы с таблицой клиентов

    //вспомогательный метод для GET запросов
    void populateClientTable(String url) {
        Client client = ClientBuilder.newClient();
        String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<ClubClient> listOfMappedClients = null;
        try {
            listOfMappedClients = objectMapper.readValue(json, new TypeReference<List<ClubClient>>() {
            });
            ObservableList<ClubClient> listOfClients;
            listOfClients = FXCollections.observableArrayList(listOfMappedClients);
            table_clients.setItems(listOfClients);
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        testField.setText(url);
        testField2.setText(json);
    }

    //возвращает список всех клиентов и выводит в таблице
    public void refreshTable() {
        String url = server + service + clients;
        populateClientTable(url);
    }

    //возвращает список клиентов по ФИО, введёным в соответствующих полях и выводит в таблице
    public void btnGETList(ActionEvent actionEvent) throws IOException {
        String filtering = "?lastname=" + lastname.getText() + "&firstname=" + firstname.getText() +
                "&middlename=" + middlename.getText() + "&phonenumber=" + phonenumber.getText() +
                "&email=" + email.getText();
        String url = server + service + clients + searchClients + filtering;
        populateClientTable(url);
    }

    //отправляет данные о пользователе, которого надо создать по ФИО, введёным в соответствующих полях
    public void btnPOST(ActionEvent actionEvent) {
        ClubClient clubClient = new ClubClient(lastname.getText(), firstname.getText(), middlename.getText(),
                phonenumber.getText(), email.getText());
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
        ClubClient clubClient = new ClubClient(lastname.getText(), firstname.getText(), middlename.getText(),
                phonenumber.getText(), email.getText());

        Client client = ClientBuilder.newClient();
        String url = server + service + clients + id;
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
        int Clientid = table_clients.getSelectionModel().getSelectedItem().getId();
        String url = server + service + visits;
        VisitDate visitDate = new VisitDate(Clientid);
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
            listOfVisitDates = FXCollections.observableArrayList(listOfMappedDates);
            table_attendance.setItems(listOfVisitDates);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Отображать имя и фамилию над таблицей посещения
        StringBuilder builder = new StringBuilder();
        builder.append(table_clients.getSelectionModel().getSelectedItem().getLastName()).append(" ")
                .append(table_clients.getSelectionModel().getSelectedItem().getFirstName());
        String txt = builder.toString();
        lbl_visit.setText(txt);

    }

    //расчет стоимости нового абонемента, исходя из количества посещений за прошедший год начиная с сегодняшнего дня
    public void btnCalculateMembershipCard(ActionEvent actionEvent) {
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
        //String.format("%.0f", percentage) - норм?
        StringBuilder builder = new StringBuilder();
        builder.append("Количество посещений за прошедший год состовляет ").append(visitsInYear).append(" дней").append("\n")
                .append("Это ").append(String.format("%.0f", percentage)).append(" % дней за прошедший год").append("\n")
                .append("Ваша скидка составляет составляет: ").append(discount).append(" %").append("\n")
                .append("Стоимость нового абонемента составляет: ").append(cost).append(" рублей");
        String alertMsg = builder.toString();
        Alerts.showAlert("Рассчет стоимости абонемента", alertMsg);
    }

    //ниже функционал для тестирования. будет убран из релизной версии)

    //добавить в БД требуемое количество посещений для клиента по его id
    public void btnTestButton(ActionEvent actionEvent) {
        int id = table_clients.getSelectionModel().getSelectedItem().getId();
        int repeats = Integer.parseInt(testField.getText());
        String filtering = "?id=" + id + "&days=" + repeats;
        String url = server + service + tests + filtering;
        Client client = ClientBuilder.newClient();
        client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);
    }

    public void btnTestButton2(ActionEvent actionEvent) {
        testField2.setText(lastname.getText());
    }
}