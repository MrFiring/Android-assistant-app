package ru.mrfiring.assistant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LicenseDialog extends DialogFragment implements OnClickListener {

    EditText edKey;


    public View onCreateView(LayoutInflater inflate, ViewGroup group, Bundle savedInstanceState) {
        getDialog().setTitle(getResources().getString(R.string.license_dlg));
        View v = inflate.inflate(R.layout.license_dialog,null);
        edKey = (EditText)v.findViewById(R.id.edKey);

        if(MainActivity.userKey != null && MainActivity.userKey.length() > 0)
            edKey.setText(MainActivity.userKey);

        v.findViewById(R.id.btn_send).setOnClickListener(this);
        v.findViewById(R.id.btn_lcancel).setOnClickListener(this);


        return v;

    }




    DialogInterface.OnClickListener DlgListener = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog,int which){
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    dialog.cancel();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btn_send:
                MainActivity.userKey = edKey.getText().toString();
                TelephonyManager mngr = (TelephonyManager)getDialog().getContext().getSystemService(Context.TELEPHONY_SERVICE);
                new TaskLicense().execute(getResources().getString(R.string.validation_url), MainActivity.userKey , mngr.getDeviceId());

                if(MainActivity.isValidate)
                {
                    Toast.makeText(getDialog().getContext(),"Validating success",Toast.LENGTH_SHORT).show();
                }
                else if(!MainActivity.isValidate) {
                    Toast.makeText(getDialog().getContext(),"Validating failed",Toast.LENGTH_SHORT).show();
                }
                onDismiss(getDialog());
                break;
            case R.id.btn_lcancel:
                onDismiss(getDialog());
                break;
        }
    }
}
