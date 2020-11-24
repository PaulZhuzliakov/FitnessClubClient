package org.demo.model;

public class ClubClient {
    private int id;
    private int clubCardNumber;
    private String lastName;
    private String firstName;
    private String middleName;

    public ClubClient() {
    }

    public ClubClient(int id, int clubCardNumber, String lastName, String firstName, String middleName) {
        this.id = id;
        this.clubCardNumber = clubCardNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
    }

    public ClubClient(int clubCardNumber, String lastName, String firstName, String middleName) {
        this.clubCardNumber = clubCardNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getClubCardNumber() {
        return clubCardNumber;
    }
    public void setClubCardNumber(int clubCardNumber) {
        this.clubCardNumber = clubCardNumber;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getMiddleName() {
        return middleName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
}
