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
public class Course {
    private int course_id;
    private String course_name;
    private int course_duration;
    private int course_credits;

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public int getCourse_duration() {
        return course_duration;
    }

    public void setCourse_duration(int course_duration) {
        this.course_duration = course_duration;
    }

    public int getCourse_credits() {
        return course_credits;
    }

    public void setCourse_credits(int course_credits) {
        this.course_credits = course_credits;
    }
}
