package com.shockn745.workoutmotivationaltool.recyclerview.cards;

/**
 * Created by Shock on 21.04.15.
 */
public class CardContact implements CardInterface{

    private String nom;
    private String prenom;
    private String telephone;

    public CardContact(String mNom, String mPrenom, String mTelephone) {
        this.nom = mNom;
        this.prenom = mPrenom;
        this.telephone = mTelephone;
    }

    @Override
    public int getViewType() {
        return CardInterface.CONTACT_VIEW_TYPE;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
