package com.example.visas.genielogiciel2.Model.Principal;



public class Contact {

    private int id;
    private String contactName;
    private String contactNumber;
    private String operateur;

    public String getOperateur() {
        return operateur;
    }

    public void setOperateur(String operateur) {
        this.operateur = operateur;
    }

    public Contact(String contactName, String contactNumber,String operateur) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.operateur=operateur;
    }

    public Contact(int id, String contactName,String contactNumber,String operateur) {
        this.id = id;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.operateur=operateur;
    }

    public Contact() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String contactNumberToString(){
        return contactNumber+"";
    }
}
