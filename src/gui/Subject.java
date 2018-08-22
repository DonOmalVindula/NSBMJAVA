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
public class Subject {
    private int subject_code;
    private String subject_name;
    private int duration; //in hours
    private double subject_fee;
    private int subject_credits;
    private int subject_lecturer;
    private int subject_course;

    public int getSubject_course() {
        return subject_course;
    }

    public void setSubject_course(int subject_course) {
        this.subject_course = subject_course;
    }

    public int getSubject_code() {
        return subject_code;
    }

    public void setSubject_code(int subject_code) {
        this.subject_code = subject_code;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getSubject_fee() {
        return subject_fee;
    }

    public void setSubject_fee(double subject_fee) {
        this.subject_fee = subject_fee;
    }

    public int getSubject_credits() {
        return subject_credits;
    }

    public void setSubject_credits(int subject_credits) {
        this.subject_credits = subject_credits;
    }

    public int getSubject_lecturer() {
        return subject_lecturer;
    }

    public void setSubject_lecturer(int subject_lecturer) {
        this.subject_lecturer = subject_lecturer;
    }
    
    
}
