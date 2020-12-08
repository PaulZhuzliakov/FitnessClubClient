package org.demo.model;

import java.sql.Date;
import java.time.LocalDate;

public class VisitDate {
    int clientId;
    Date date;

    public VisitDate() {
    }

    //для метода отмечающего сегодняшнее посещение по id клиента
    public VisitDate(int clientId) {
        this.clientId = clientId;
        this.date = Date.valueOf(LocalDate.now());
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
