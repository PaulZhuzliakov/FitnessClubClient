package org.demo.model;

import java.time.LocalDate;
import java.sql.Date;

public class VisitDate {
    int clientId;
    Date date;

    public VisitDate() {
    }

    //для метода отмечающего сегодняшнее посещение по id клиента
    public VisitDate(int clientId) {
        this.clientId = clientId;
        this.date = Date.valueOf(LocalDate.now());
//        this.date = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now());
    }

//    public VisitDate(int clientId, Date date) {
//        this.clientId = clientId;
//        this.date = date;
//    }

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
