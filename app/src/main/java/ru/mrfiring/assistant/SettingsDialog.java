package ru.mrfiring.assistant;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.*;
import android.os.*;
import android.content.*;





public class SettingsDialog extends DialogFragment implements OnClickListener {

    EditText tbIp;

    public View onCreateView(LayoutInflater inflater, ViewGroup contatiner, Bundle savedInstanceState) {
        getDialog().setTitle("Settings");
        View v = inflater.inflate(R.layout.settings_dialog,null);
        v.findViewById(R.id.dlg_btn_Ok).setOnClickListener(this);
        v.findViewById(R.id.dlg_btn_cancel).setOnClickListener(this);
        if(MainActivity.ipAddr.length() != 0)
       (( EditText)v.findViewById(R.id.editText)).setText(MainActivity.ipAddr);
        tbIp = (EditText)v.findViewById(R.id.editText);
        return v;


    }

    public void onClick(View v){

        if(v.getId() == R.id.dlg_btn_Ok){
             MainActivity.ipAddr = tbIp.getText().toString();
            onDismiss(this.getDialog());
        }
        else if(v.getId() == R.id.dlg_btn_cancel){

            onDismiss(this.getDialog());
        }

    }

    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog){
        super.onCancel(dialog);
    }

}
