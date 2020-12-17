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
import org.demo.model.MembershipCardCost;
import org.demo.model.VisitDate;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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
    TableColumn<VisitDate, LocalDate> col_dates;

    @FXML
    Label lbl_visit;

    String server;
    String service;
    String clients;
    String visits;
    String cardCost;
    String tests;

    Client client = ClientBuilder.newClient();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initProperties();
        //инициалицация столбцов таблицы клиентов
        col_cardNumber.setCellValueFactory(new PropertyValueFactory<ClubClient, Integer>("clubCardNumber"));
        col_lastName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("lastName"));
        col_firstName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("firstName"));
        col_middleName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("middleName"));
        col_phoneNumber.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("phoneNumber"));
        col_eMail.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("mail"));
        //инициалицация столбцов таблицы посещаемости
        col_dates.setCellValueFactory(new PropertyValueFactory<VisitDate, LocalDate>("date"));
        refreshTable();
    }

    public void initProperties() {
        try (FileReader reader = new FileReader("src/main/resources/config.properties")) {
            Properties properties = new Properties();
            properties.load(reader);
            server = properties.getProperty("server");
            service = properties.getProperty("service");
            clients = properties.getProperty("clients");
            visits = properties.getProperty("visits");
            cardCost = properties.getProperty("cardCost");
            tests = properties.getProperty("tests");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int getIdOfSelectedClient() {
        return table_clients.getSelectionModel().getSelectedItem().getId();
    }

    //ниже следуют методы для работы с таблицой клиентов

    //возвращает список всех клиентов и выводит в таблице
    public void refreshTable() {
        String url = new StringBuilder(server).append(service).append(clients).toString();
        populateClientTable(url);
    }

    //возвращает список клиентов по параметрам, введёным в соответствующих полях и выводит в таблице
    public void btnGETList(ActionEvent actionEvent) throws IOException {
        if (lastname.getText().equals("") && firstname.getText().equals("") && middlename.getText().equals("")
                && phonenumber.getText().equals("") && email.getText().equals(""))
            Alerts.showAlert("Нет данных для поиска", "Заполните хотя бы одно поле для поиска");
        else {
            String filtering = new StringBuilder("?lastname=").append(lastname.getText()).append("&firstname=").append(firstname.getText())
                    .append("&middlename=").append(middlename.getText()).append("&phonenumber=").append(phonenumber.getText())
                    .append("&email=").append(email.getText()).toString();
            String url = new StringBuilder(server).append(service).append(clients).append(filtering).toString();
            populateClientTable(url);
        }
    }

//    //возвращает список клиентов по параметрам, введёным в соответствующих полях и выводит в таблице
//    public void btnGETList(ActionEvent actionEvent) throws IOException {
//        String filtering = new StringBuilder("?lastname=").append(lastname.getText()).append("&firstname=").append(firstname.getText())
//                .append("&middlename=").append(middlename.getText()).append("&phonenumber=").append(phonenumber.getText())
//                .append("&email=").append(email.getText()).toString();
//        String url = new StringBuilder(server).append(service).append(clients).append(filtering).toString();
//        populateClientTable(url);
//    }

    //не смог сделать общий параметризированный метод для заполнения таблий клиентов и посещений(
    void populateClientTable(String url) {
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
    }

    //отправляет данные о пользователе, которого надо создать по ФИО, введёным в соответствующих полях
    public void btnPOST(ActionEvent actionEvent) {
        ClubClient clubClient = new ClubClient(lastname.getText(), firstname.getText(), middlename.getText(),
                phonenumber.getText(), email.getText());
        String url = new StringBuilder(server).append(service).append(clients).toString();
        WebTarget target = client.target(url);
        target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .post(Entity.json(clubClient), String.class);
        refreshTable();
    }

    //отправляет данные о пользователе, которого надо редактировать
    //меняет параметры у уже имеющегося пользователя, введёные в соответствующих полях
    public void btnPUT(ActionEvent actionEvent) {
        int id = getIdOfSelectedClient();
        ClubClient clubClient = new ClubClient(lastname.getText(), firstname.getText(), middlename.getText(),
                phonenumber.getText(), email.getText());
        String url = new StringBuilder(server).append(service).append(clients).append(id).toString();
        WebTarget target = client.target(url);
        target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .put(Entity.json(clubClient), String.class);
        refreshTable();
    }

    //удаляет выделенного пользователя по id
    public void btnDELETE(ActionEvent actionEvent) {
        int id = getIdOfSelectedClient();
        String url = new StringBuilder(server).append(service).append(clients).append(id).toString();
        WebTarget target = client.target(url);
        target.request().delete();
        refreshTable();
    }


    //ниже следуют методы для работы с таблицой посещаемости

    //выбрать клиента в таблице клиентов и отобразить в таблице посещаемости данные о датах посещения выбранного клиента
    public void btnConfirmVisit(ActionEvent actionEvent) {
        int id = getIdOfSelectedClient();
        String url = new StringBuilder(server).append(service).append(visits).toString();
        LocalDate today = LocalDate.now();
        VisitDate visitDate = new VisitDate(id, today);
        WebTarget target = client.target(url);
        target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .post(Entity.json(visitDate), String.class);
    }

    //отобразить в таблице посещений, даты посещения выбранного клиента
    public void btnViewVisits(ActionEvent actionEvent) {
        int id = getIdOfSelectedClient();
        String url = new StringBuilder(server).append(service).append(visits).append(id).toString();
        populateVisitTable(url);
        //Отображать имя и фамилию над таблицей посещения
        String txt = new StringBuilder(table_clients.getSelectionModel().getSelectedItem().getLastName()).append(" ")
                .append(table_clients.getSelectionModel().getSelectedItem().getFirstName()).toString();
        lbl_visit.setText(txt);
    }

    void populateVisitTable(String url) {
        String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);
        //
        testField.setText(json);
        testField2.setText(url);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<VisitDate> listOfMappedDates = null;
        try {
            listOfMappedDates = objectMapper.readValue(json, new TypeReference<List<VisitDate>>() {
            });
            ObservableList<VisitDate> listOfVisitDates;
            listOfVisitDates = FXCollections.observableArrayList(listOfMappedDates);
            table_attendance.setItems(listOfVisitDates);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //расчет стоимости нового абонемента, исходя из количества посещений за прошедший год, начиная с сегодняшнего дня
    public void btnCalculateMembershipCardCost(ActionEvent actionEvent) {
        int id = getIdOfSelectedClient();
        String url = new StringBuilder(server).append(service).append(cardCost).append(id).toString();
        String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        MembershipCardCost card = null;
        try {
            card = objectMapper.readValue(json, new TypeReference<MembershipCardCost>() {
            });
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Количество посещений за прошедший год состовляет ")
                .append(card.getDaysVisitedInPeriodOfTime()).append(" дней").append("\n")
                .append("Это ").append(String.format("%.0f", card.getPercentageOfVisitedDaysInPeriodOfTime()))
                .append(" % дней за прошедший год").append("\n")
                .append("Ваша скидка составляет составляет: ").append(card.getDiscount()).append(" %").append("\n")
                .append("Стоимость нового абонемента с учётом скидки составляет: ")
                .append(card.getMembershipCardCostWithDiscount()).append(" рублей");
        String msg = sb.toString();
        Alerts.showAlert("Рассчет стоимости абонемента", msg);
    }

    //ниже функционал для тестирования. будет убран из релизной версии)

    //добавить в БД требуемое количество посещений для клиента по его id
    public void btnTestButton(ActionEvent actionEvent) {
        int id = getIdOfSelectedClient();
        int days = Integer.parseInt(testField.getText());
        String filtering = new StringBuilder("?id=").append(id).append("&days=").append(days).toString();
        String url = new StringBuilder(server).append(service).append(tests).append(filtering).toString();
        client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);
    }

    public void btnTestButton2(ActionEvent actionEvent) {
    }
}