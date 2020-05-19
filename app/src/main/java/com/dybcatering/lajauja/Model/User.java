package com.dybcatering.lajauja.Model;

public class User {
    private String Name;
    private String LastName;
    private String Date;
    private String Document;

    private String MyDirection;
    private String Password;
    private String Phone;
    private String IsStaff;

    public User() {
    }

    /*
    public User(String name, String password) {
        Name = name;
        Password = password;
        IsStaff = "false";

    }

     */
    public User(String name, String lastName, String date, String document, String myDirection, String password) {
        Name = name;
        LastName = lastName;
        Date = date;
        Document = document;
        MyDirection = myDirection;
        Password = password;
        IsStaff = "false";
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDocument() {
        return Document;
    }

    public void setDocument(String document) {
        Document = document;
    }

    public String getMyDirection() {
        return MyDirection;
    }

    public void setMyDirection(String myDirection) {
        MyDirection = myDirection;
    }
}
