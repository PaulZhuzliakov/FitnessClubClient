package org.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.demo.model.ClubClient;
import org.demo.DB.DBConnect;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextField lastname;
    @FXML
    TextField firstname;
    @FXML
    TextField middlename;
    @FXML
    TextField result;
    @FXML
    TextField clientId;

    @FXML
    TableView<ClubClient> table_clients;
    @FXML
    TableColumn<ClubClient, Integer> col_cardNumber;
    @FXML
    TableColumn<ClubClient, String> col_lastName;
    @FXML
    TableColumn<ClubClient, String> col_firstName;
    @FXML
    TableColumn<ClubClient, String> col_middleName;

    ObservableList<ClubClient> listOfClients;
    int index = -1;
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshTable();
    }

    public void refreshTable() {
        col_cardNumber.setCellValueFactory(new PropertyValueFactory<ClubClient, Integer>("clubCardNumber"));
        col_lastName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("lastName"));
        col_firstName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("firstName"));
        col_middleName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("middleName"));
        listOfClients = DBConnect.getDataUsers();
        table_clients.setItems(listOfClients);
    }

    String server = "http://127.0.0.1:8080";
    String war = "/api/demo";
    String GetBuFIOMethod = "/clients/searchByFIO";

    public void btnGET(ActionEvent actionEvent) {
        String filtering = "?lastname=" + lastname.getText() + "&firstname=" + firstname.getText() + "&middlename=" + middlename.getText();
        String url = server + war + GetBuFIOMethod + filtering;

        Client client = ClientBuilder.newClient();
        String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);
        result.setText(json);

        ObjectMapper mapper = new ObjectMapper();
        ClubClient clubClient = null;
        try {
            clubClient = mapper.readValue(new URL(url), ClubClient.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String id = String.valueOf(clubClient.getId());
        clientId.setText(url);
//        clientId.setText(id);
    }


}