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
public class Undergraduate extends Student 
{
    private int alvl_rank;
    private char gk_result;
    private char eng_result;
    private char subject1_result;
    private char subject2_result;
    private char subject3_result;   

    public int getAlvl_rank() {
        return alvl_rank;
    }

    public void setAlvl_rank(int alvl_rank) {
        this.alvl_rank = alvl_rank;
    }

    public char getGk_result() {
        return gk_result;
    }

    public void setGk_result(char gk_result) {
        this.gk_result = gk_result;
    }

    public char getEng_result() {
        return eng_result;
    }

    public void setEng_result(char eng_result) {
        this.eng_result = eng_result;
    }

    public char getSubject1_result() {
        return subject1_result;
    }

    public void setSubject1_result(char subject1_result) {
        this.subject1_result = subject1_result;
    }

    public char getSubject2_result() {
        return subject2_result;
    }

    public void setSubject2_result(char subject2_result) {
        this.subject2_result = subject2_result;
    }

    public char getSubject3_result() {
        return subject3_result;
    }

    public void setSubject3_result(char subject3_result) {
        this.subject3_result = subject3_result;
    }
}

