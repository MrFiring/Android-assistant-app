package ru.mrfiring.assistant;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class FeedbackActivity extends Activity implements View.OnClickListener {


    Button btnSend;
    Button btnCancel;
    EditText etSubject;
    EditText etMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        btnSend = (Button)findViewById(R.id.btnSend);
        btnCancel = (Button)findViewById(R.id.btnCancel);

        btnSend.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        etSubject = (EditText)findViewById(R.id.etSubject);
        etMessage = (EditText)findViewById(R.id.etMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnSend:
                if(etSubject.getText().toString().equals("") || etMessage.getText().toString()
                        .equals("")) {
                    Toast.makeText(this,"An field is empty",Toast.LENGTH_SHORT).show();
                    break;
                }
                String rec[] = {getResources().getString(R.string.feedback_email)};
                Intent email = new Intent(Intent.ACTION_SEND,Uri.parse("mailto:"));
                email.putExtra(Intent.EXTRA_EMAIL,rec);
                email.putExtra(Intent.EXTRA_SUBJECT,etSubject.getText().toString());
                email.putExtra(Intent.EXTRA_TEXT,etMessage.getText().toString());
                email.setType("email/rfc822");

                startActivity(Intent.createChooser(email,"Choose an Email client :"));
                break;
            case R.id.btnCancel:
                finish();
                break;

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        switch (id){


        case android.R.id.home:
                finish();
               break;
            case R.id.action_fsupport:
                Intent support = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.support_url)));
                startActivity(support);
                break;


        }


        return super.onOptionsItemSelected(item);
    }
}
