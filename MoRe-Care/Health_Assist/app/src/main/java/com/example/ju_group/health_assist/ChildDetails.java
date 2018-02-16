package com.example.ju_group.health_assist;


import java.io.Serializable;


/*Child details to store information*/
class ChildDetails implements Serializable{

    private static final String TAG = "ChildDetails";
    private static final long serialVersionID = 1L;
    private Long patient_id;
    private String patientName;
    private String mothersName;
    private String bloodGroup;
    private String sex;
    private String dateOfBirth;
    private String address;
    private String phone;
    private String emailId="";
    private String questionAnswer;

    public ChildDetails(){

    }

    public ChildDetails(String patientName, String mothersName, String bloodGroup, String sex, String dateOfBirth, String address, String phone, String emailId, String questionAnswer) {
        this.patientName = patientName;
        this.mothersName = mothersName;
        this.bloodGroup = bloodGroup;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phone = phone;
        this.emailId = emailId;
        this.questionAnswer = questionAnswer;
    }
    /*Getter and setter*/

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getMothersName() {
        return mothersName;
    }

    public void setMothersName(String mothersName) {
        this.mothersName = mothersName;
    }

    public Long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Long patient_id) {
        this.patient_id = patient_id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    @Override
    public String toString() {

        return("toString: Patient details: patientname= "+ patientName+ "phone= "+ phone);

    }

}
