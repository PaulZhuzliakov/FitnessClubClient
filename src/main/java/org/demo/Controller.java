package org.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.demo.model.ClubClient;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    TextField testField;
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

    String server = "http://127.0.0.1:8080";
    String war = "/api/demo";
    String getByFIO = "/clients/searchByFIO";
    String getListByFIO = "/clients/getClientsByFIO";

    ObservableList<ClubClient> listOfClients;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        col_cardNumber.setCellValueFactory(new PropertyValueFactory<ClubClient, Integer>("clubCardNumber"));
        col_lastName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("lastName"));
        col_firstName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("firstName"));
        col_middleName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("middleName"));
        refreshTable();
    }

    public void refreshTable() {

        String url = "http://127.0.0.1:8080/api/demo/clients/getAllClients";
        Client client = ClientBuilder.newClient();
        String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<ClubClient> listOfMappedClients = null;
        try {
            listOfMappedClients = objectMapper.readValue(json, new TypeReference<List<ClubClient>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        listOfClients = FXCollections.observableArrayList(listOfMappedClients);
        table_clients.setItems(listOfClients);
    }

//    public void btnGET(ActionEvent actionEvent) throws IOException {
//        String filtering = "?lastname=" + lastname.getText() + "&firstname=" + firstname.getText() + "&middlename=" + middlename.getText();
//        String url = server+war+ getByFIO +filtering;
//
//        Client client = ClientBuilder.newClient();
//        String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);
//        result.setText(json);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        ClubClient clubClient = null;
//        try {
//            clubClient = objectMapper.readValue(new URL(url), ClubClient.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ArrayList<ClubClient> listOfMappedClients = new ArrayList<>();
//        listOfMappedClients.add(clubClient);
//        listOfClients = FXCollections.observableArrayList(listOfMappedClients);
//        table_clients.setItems(listOfClients);
//    }

    //возвращает список клиентов в JSON по ФИО, введёным в клиенте
    public void btnGETList(ActionEvent actionEvent) throws IOException {
        String filtering = "?lastname=" + lastname.getText() + "&firstname=" + firstname.getText() + "&middlename=" + middlename.getText();
        String url = server+war+ getListByFIO +filtering;

        Client client = ClientBuilder.newClient();
        String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);
        result.setText(json);

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<ClubClient> listOfMappedClients = null;
        try {
            listOfMappedClients = objectMapper.readValue(json, new TypeReference<List<ClubClient>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        listOfClients = FXCollections.observableArrayList(listOfMappedClients);
        table_clients.setItems(listOfClients);

    }

    public void btnPOST(ActionEvent actionEvent) {
        ClubClient clubClient = new ClubClient(lastname.getText(), firstname.getText(), middlename.getText());
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://127.0.0.1:8080/api/demo/clients/addByFIO");
        target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .post(Entity.json(clubClient), String.class);
        refreshTable();
    }

    public void btnPUT(ActionEvent actionEvent) {
        TableView.TableViewSelectionModel<ClubClient> selectionModel = table_clients.getSelectionModel();
        int id = selectionModel.getSelectedItem().getId();

        ClubClient clubClient = new ClubClient(lastname.getText(), firstname.getText(), middlename.getText());
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://127.0.0.1:8080/api/demo/clients/update/"+"?id="+id);
        target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .put(Entity.json(clubClient), String.class);
        refreshTable();
    }

    public void btnDELETE(ActionEvent actionEvent) {
        TableView.TableViewSelectionModel<ClubClient> selectionModel = table_clients.getSelectionModel();
        int id = selectionModel.getSelectedItem().getId();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://127.0.0.1:8080/api/demo/clients/deleteById/"+id);
        target.request().delete();
        refreshTable();
    }

}