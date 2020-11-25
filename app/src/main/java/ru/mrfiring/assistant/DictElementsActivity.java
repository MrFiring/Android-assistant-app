package ru.mrfiring.assistant;

import ru.mrfiring.assistant.MainActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;

public class DictElementsActivity extends Activity implements AdapterView.OnItemClickListener {

    static public ListView ElementsList;
    DialogFragment settingsDialog;
    DialogFragment licenseDialog;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict_elements);

        settingsDialog = new SettingsDialog();
        licenseDialog = new LicenseDialog();
        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);//Включаем стрелку рядом с иконкой в экшн баре.

        ElementsList = (ListView)findViewById(R.id.ElementsList); //Находим наш listview
        try {
            //Если количество элементов словаря больше 0 и содержится ключ выбранного словаря,тогда заполняем listView
                if(MainActivity.dictElements.size() > 0 && MainActivity.dictElements.containsKey(MainActivity.CurrentDictionaryNumber) ) {

                    if (MainActivity.dictElements.get(MainActivity.CurrentDictionaryNumber).length > 0) {
                        ArrayList<String> list = new ArrayList<String>();
                        for(int i = 0 ; i < MainActivity.dictElements.get(MainActivity.CurrentDictionaryNumber).length;i++)
                        {
                            if(MainActivity.dictElements.get(MainActivity.CurrentDictionaryNumber)[i] != null)
                                list.add(MainActivity.dictElements.get(MainActivity.CurrentDictionaryNumber)[i]);
                            Log.d("DD","ADD ELEMENT: #"+ i + " " + MainActivity.dictElements.get(MainActivity.CurrentDictionaryNumber)[i]);
                        }

                        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
                        ElementsList.setAdapter(adapt);
                    }

                }

        }
        catch(Exception ex)
        {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
            Log.d("DD","INIT "  );
        }
        ElementsList.setOnItemClickListener(this); //Задаем слушатель на нажатие

    }

    @Override
    protected void onDestroy() {



        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Загружаем меню из menu_dict_elements.xml
        getMenuInflater().inflate(R.menu.menu_dict_elements, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //Обрабатываем нажатия на элементы меню.
        switch(id)
        {
            case R.id.action_settings2:
                settingsDialog.show(getFragmentManager(),"dlg_settings");
                break;

            case android.R.id.home:
                    finish();//Возвращаемся к списку словарей.
                break;
            case R.id.action_feedback:
                Intent feed = new Intent(this,FeedbackActivity.class);
                startActivity(feed);
                break;
            case R.id.action_support:
                Intent support = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.support_url)));
                startActivity(support);
                break;
            case R.id.action_licensede:
                licenseDialog.show(getFragmentManager(), "dlg_licensede");
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int checkedPos = ElementsList.getCheckedItemPosition(); //Получаем позицию выбранного элемента списка
        try {

                Log.d("DD", MainActivity.ipAddr);
                new Task().execute(String.valueOf(checkedPos));//Подключаемся к серверу и отправляем строку.

        }
        catch(Exception ex){
            Log.d("DD",ex.toString() + " DICT DELETETING ON ITEM SELEECTED");
        }
    }
}


 class Task extends AsyncTask<String,Void,Void>{
     Socket client;
     PrintWriter out;

     @Override
     protected Void doInBackground(String... params) {
         try {
             client = new Socket(MainActivity.ipAddr, 45879);
             Log.d("DD",String.valueOf(client.isConnected()));
             out = new PrintWriter(client.getOutputStream(),true);//Синхронизируем поток вывода.
             int checkedPos = Integer.parseInt(params[0]);

             out.println(MainActivity.elementToSend.get(MainActivity.dictElements.get(MainActivity.CurrentDictionaryNumber)[checkedPos]));//Отправляем текст элемента на сервер.
             Log.d("DD","SENDED: " + MainActivity.elementToSend.get(MainActivity.dictElements.get(MainActivity.CurrentDictionaryNumber)[checkedPos]));
         }
         catch (Exception ex)
         {
             Log.d("DD",ex.toString());
         }

         return null;
     }

     @Override
     protected void onPostExecute(Void aVoid) {
         try {
             if (client != null)
                 client.close();
         }
         catch(Exception ex){
             Log.d("DD","PostExecuteErr: " +ex.toString());
         }
         super.onPostExecute(aVoid);
     }
 }