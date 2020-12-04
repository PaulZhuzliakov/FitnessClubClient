package org.demo.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Date;
import java.util.Calendar;

@XmlRootElement
public class VisitDate {
    int clientId;
    Date date;

    public VisitDate() {
    }

    //задаёт текущую дату
    public VisitDate(int clientId) {
        this.clientId = clientId;

        //this.date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        this.date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
    }

    //для метода отмечающего сегодняшнее посещение по id клиента
    public VisitDate(int clientId, Date date) {
        this.clientId = clientId;
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
