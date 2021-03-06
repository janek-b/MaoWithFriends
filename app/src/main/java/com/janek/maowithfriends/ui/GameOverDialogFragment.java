package com.janek.maowithfriends.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;


public class GameOverDialogFragment extends DialogFragment {
    private boolean won;

    public void setOutcome(boolean won) {
        this.won = won;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (this.won) {
            builder.setTitle("Round Over. You Won!");
        } else {
            builder.setTitle("Round Over.");
        }
        builder.setMessage("Play another Round?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                ((GameActivity)getActivity()).newRound();
//                dismiss();
                // TODO: add new game method;
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                ((GameActivity)getActivity()).endGame();
            }
        });
        return builder.create();
    }
}
