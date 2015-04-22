package com.shockn745.workoutmotivationaltool.recyclerview.cards;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.R;

/**
 * Created by Shock on 21.04.15.
 */
public class CardContact implements CardInterface{

    private String nom;
    private String prenom;
    private String telephone;

    public static class ContactVH extends RecyclerView.ViewHolder {
        public TextView mPrenomTextView;
        public TextView mNomTextView;
        public TextView mTelTextView;

        public ContactVH(View itemView) {
            super(itemView);
            this.mPrenomTextView = (TextView) itemView.findViewById(R.id.prenom_textView);
            this.mNomTextView = (TextView) itemView.findViewById(R.id.nom_text_view);
            this.mTelTextView = (TextView) itemView.findViewById(R.id.telephone_textView);
        }
    }

    public CardContact(String mNom, String mPrenom, String mTelephone) {
        this.nom = mNom;
        this.prenom = mPrenom;
        this.telephone = mTelephone;
    }

    @Override
    public int getViewType() {
        return CardInterface.CONTACT_VIEW_TYPE;
    }

    @Override
    public boolean canDismiss() {
        return true;
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
