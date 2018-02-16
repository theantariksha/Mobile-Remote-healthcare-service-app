package com.example.ju_group.health_assist;

import java.util.ArrayList;



class Symptoms {

    private String symptomName;
    private String has;
    private String no;
    private String dangerous;
    private String backtrace;

    public Symptoms(String has, String no, String symptomName, String dangerous, String backtrace) {
        this.has = has;
        this.no = no;
        this.symptomName = symptomName;
        if(dangerous==null)
            this.dangerous="false";
        else
            this.dangerous = dangerous;
        if(backtrace==null)
            this.backtrace="";
        else
            this.backtrace=backtrace;
    }

    public Symptoms() {
    }

    public String getHas() {
        return has;
    }

    public void setHas(String has) {
        this.has = has;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getSymptomName() {
        return symptomName;
    }

    public String getDangerous() {
        return dangerous;
    }

    public void setSymptomName(String symptomName) {
        this.symptomName = symptomName;
    }

    public void setDangerous(String dangerous) {
        this.dangerous = dangerous;
    }

    public String getBacktrace() {
        return backtrace;
    }

    public void setBacktrace(String backtrace) {
        this.backtrace = backtrace;
    }

    @Override
    public String toString() {

        return ("symName= "+symptomName+"\nhas = "+has+"\nno = "+no);
    }
}
