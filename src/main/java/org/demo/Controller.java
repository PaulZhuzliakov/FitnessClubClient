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
import javax.ws.rs.core.Response;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshTable();
    }

    public void refreshTable() {
        col_cardNumber.setCellValueFactory(new PropertyValueFactory<ClubClient, Integer>("clubCardNumber"));
        col_lastName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("lastName"));
        col_firstName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("firstName"));
        col_middleName.setCellValueFactory(new PropertyValueFactory<ClubClient, String>("middleName"));

        String url = "http://127.0.0.1:8080/api/demo/clients/getAllClients";
        Client client = ClientBuilder.newClient();
        String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<ClubClient> listOfMappedClients = null;
        try {
            listOfMappedClients = objectMapper.readValue(json, new TypeReference<List<ClubClient>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        listOfClients = FXCollections.observableArrayList(listOfMappedClients);
        table_clients.setItems(listOfClients);
    }

    String server = "http://127.0.0.1:8080";
    String war = "/api/demo";
    String getByFIOMethod = "/clients/searchByFIO";
    String postByFIOMethod = "/clients//addByFIO";

    //возвращает одного клиента по ФИО, введёным в клиенте
    public void btnGET(ActionEvent actionEvent) {
        String filtering = "?lastname=" + lastname.getText() + "&firstname=" + firstname.getText() + "&middlename=" + middlename.getText();
        String url = "http://127.0.0.1:8080/api/demo/clients/addByFIO";

        String url2 = server + war + getByFIOMethod + filtering;

        Client client = ClientBuilder.newClient();
        String json = client.target(url).request(MediaType.APPLICATION_JSON).get(String.class);
        result.setText(json);

        ObjectMapper objectMapper = new ObjectMapper();
        ClubClient clubClient = null;
        try {
            clubClient = objectMapper.readValue(new URL(url), ClubClient.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String id = String.valueOf(clubClient.getId());
        clientId.setText(id);
    }


    public void btnPOST(ActionEvent actionEvent) {

        ClubClient clubClient = new ClubClient(lastname.getText(), firstname.getText(), middlename.getText());
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://127.0.0.1:8080/api/demo/clients/addByFIO");
        String response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .post(Entity.json(clubClient), String.class);

//        String filtering = "?lastname=" + lastname.getText() + "&firstname=" + firstname.getText() + "&middlename=" + middlename.getText();
//        String url = server + war + postByFIOMethod + filtering;
//
//        Client client = ClientBuilder.newClient();
//        WebTarget webTargetURI = client.target(server + war);
//        WebTarget webTargetAddClient = webTargetURI.path(postByFIOMethod + filtering);



//        Invocation.Builder invocationBuilder = webTargetAddClient.request(MediaType.APPLICATION_JSON);
//        ClubClient clubClient = new ClubClient(lastname.getText(), firstname.getText(), middlename.getText());
//        Response response = invocationBuilder.post(Entity.entity(clubClient, MediaType.APPLICATION_JSON));


//        client.target(url).request(MediaType.APPLICATION_JSON);
//        result.setText(json);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        ClubClient clubClient = null;
//        try {
//            clubClient = objectMapper.readValue(new URL(url), ClubClient.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String id = String.valueOf(clubClient.getId());
//        clientId.setText(id);
    }

    public void btnPUT(ActionEvent actionEvent) {
    }

    public void btnDELETE(ActionEvent actionEvent) {
    }
}