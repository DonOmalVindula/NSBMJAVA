/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

/**
 *
 * @author Omal
 */
public class Postgraduate extends Student {
    private String institute;
    private int year_completed;
    private String qualification;

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public int getYear_completed() {
        return year_completed;
    }

    public void setYear_completed(int year_completed) {
        this.year_completed = year_completed;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
}
