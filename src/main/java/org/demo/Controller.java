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
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    TextField lastname, firstname, middlename;
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
    String server = "http://127.0.0.1:8080";
    String war = "/api/demo";
    String getByFIO = "/clients/searchByFIO";
    String getListByFIO = "/clients/getClientsByFIO";

    ObservableList<ClubClient> listOfClients;
    ObservableList<VisitDate> listOfVisitDates;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //инициалицация столбцов таблицы клиентов
        col_cardNumber.setCellValueFactory(new PropertyValueFactory<ClubClient, Integer>("clubCardNumber"));
        col_lastName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("lastName"));
        col_firstName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("firstName"));
        col_middleName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("middleName"));
        //инициалицация столбцов таблицы посещаемости
        col_dates.setCellValueFactory(new PropertyValueFactory<VisitDate, Date>("date"));
        refreshTable();
    }

    //ниже следуют методы для работы с таблицой посещаемости

    //возвращает список всех клиентов и выводит в таблице
    public void refreshTable() {
        String url = "http://127.0.0.1:8080/api/demo/clients/getAllClients";
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
        listOfClients = FXCollections.observableArrayList(listOfMappedClients);
        table_clients.setItems(listOfClients);
        //добавить логику отсутствия соединения с сервером. а то клиент не запускается, если сервер не ответил
    }

    //возвращает список клиентов по ФИО, введёным в соответствующих полях и выводит в таблице
    public void btnGETList(ActionEvent actionEvent) throws IOException {
        String filtering = "?lastname=" + lastname.getText() + "&firstname=" + firstname.getText() + "&middlename=" + middlename.getText();
        String url = "http://127.0.0.1:8080/api/demo/clients/getClientsByFIO" + filtering;

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
        //вывод в таблице
        listOfClients = FXCollections.observableArrayList(listOfMappedClients);
        table_clients.setItems(listOfClients);
    }

    //отправляет данные о пользователе, которого надо создать по ФИО, введёным в соответствующих полях
    public void btnPOST(ActionEvent actionEvent) {
        ClubClient clubClient = new ClubClient(lastname.getText(), firstname.getText(), middlename.getText());
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://127.0.0.1:8080/api/demo/clients/addByFIO");
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
        WebTarget target = client.target("http://127.0.0.1:8080/api/demo/clients/update/" + "?id=" + id);
        target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .put(Entity.json(clubClient), String.class);
        refreshTable();
    }

    //удаляет выделенного пользователя
    public void btnDELETE(ActionEvent actionEvent) {
        TableView.TableViewSelectionModel<ClubClient> selectionModel = table_clients.getSelectionModel();
        int id = selectionModel.getSelectedItem().getId();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://127.0.0.1:8080/api/demo/clients/deleteById/" + id);
        target.request().delete();
        refreshTable();
    }


    //ниже следуют методы для работы с таблицой посещаемости

    //выбрать клиента в таблице клиентов и отобразить в таблице посещаемости данные о датах посещения выбранного клиента
    public void btnConfirmVisit(ActionEvent actionEvent) {
        int id = table_clients.getSelectionModel().getSelectedItem().getId();
        java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        String url = "http://127.0.0.1:8080/api/demo/clients/confirmClientVisit";
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
        String filtering = "?id=" + id;
        String url = "http://127.0.0.1:8080/api/demo/clients/viewVisits" + filtering;

        Client client = ClientBuilder.newClient();
        String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<VisitDate> listOfMappedDates = null;
        try {
            listOfMappedDates = objectMapper.readValue(json, new TypeReference<List<VisitDate>>() {});
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
    //ужасно написано. переписать!
    public void btnCalculateMembershipCard(ActionEvent actionEvent) {
        int id = table_clients.getSelectionModel().getSelectedItem().getId();
        String filtering = "?id=" + id;
        String url = "http://127.0.0.1:8080/api/demo/clients/calcMembershipCard" + filtering;

        Client client = ClientBuilder.newClient();
        String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        int number = 0;
        try {
            number = objectMapper.readValue(json, Integer.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int percentage = (number/365)*100;

        int discount;
        if (percentage<35) {
            discount = 0;
        } else if (percentage>=35&&percentage<60){
            discount = 10;
        } else {
            discount = 15;
        }
        //Цена абонемента без скидки
        int cost = 20_000;
        String msg = "Количество посещений за прошедший год состовляет " + number + " дней"
                + "\n" + "Стоимость нового абонемента составляет: " + (cost * 1-(1-discount)) + " рублей";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Рассчет стоимости абонемента");
//        alert.setHeaderText("HeaderText");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}