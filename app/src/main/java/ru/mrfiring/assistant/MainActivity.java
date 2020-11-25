package ru.mrfiring.assistant;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.*;
import android.app.DialogFragment;


import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.*;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.*;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    public static ListView DictList;
    DialogFragment settingsDialog;
    DialogFragment licenseDialog;

    public static Map<Integer,String> dict = new HashMap<Integer,String>(); //Словари (Номер,Имя словаря)
    public static Map<Integer,String[]> dictElements = new HashMap<Integer,String[]>(); //Элементы словарей( Номер словаря, <Номер элемента , Значение элемента>)
    public static Map<String,String> elementToSend = new HashMap<String,String>();//Элементы для отправки (Значение элемента из dictElements, Значение для отправки)
    public static ArrayList<Bitmap> images = new ArrayList<Bitmap>();


    public static int CurrentDictionaryNumber = 0;
    public static String ipAddr = "0.0.0.0";
    public static String userKey = "null";
    public static boolean isValidate = false;
    public static String content12 = "null";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DictList = (ListView)findViewById(R.id.DictList);
        DictList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        settingsDialog = new SettingsDialog();
        licenseDialog = new LicenseDialog();
		
		

		
		

        try {

			LoadData("Settings.bin");

            //До добавления элементов нужно добавить иконки.В том порядке, в котором они будут даны элементам.
            //Нужно вызвать поток в него передать адреса иконок.(Полный URL адрес)

            //Сюда помещаешь адрес иконки.
            String imgs[] = {"https://site.ru/image1.png"};

            TaskLoadImgs task =  new TaskLoadImgs();
                    task.execute(imgs);
            task.get(); //Ждём завершения потока.
			///Здесь добавляешь элементы
            //С помощью функции addDictionary(Имя словаря, Элементы словаря,Элементы для отправки)


            String[] strs1 = {"ELEMENT1","ELEMTN2"};

            addDictionary("Names", strs1, new String[]{"Vasya","Alex"});

            Log.d("DD","DictElementCount " + dictElements.size());


            if(dict.size() > 0) {


                String strs[] = new String[dict.values().size()];//Выделяем память под массив строк размером с кол-во  элементов dict.

                for (int i = 0; i < dict.values().size(); i++)//Получаем названия словарей.
                {
                    strs[i] = dict.get(i);
                }


                Bitmap images[] = new Bitmap[MainActivity.images.size()];
                for(int i = 0; i < MainActivity.images.size();i++)
                {
                    images[i] = MainActivity.images.get(i);

                }

                ArrayWImgAdapter adapt = new ArrayWImgAdapter(this,strs,images);

                DictList.setAdapter(adapt);
            }
            DictList.setOnItemClickListener(this); //Устанавливаем слушатель события onItemClick.

        }
        catch(Exception ex){
            Toast.makeText(this,ex.toString(),Toast.LENGTH_LONG).show();
        }





    }

    @Override
    protected void onDestroy() {
        SaveData("Settings.bin");
        super.onDestroy();
    }



    DialogInterface.OnClickListener DlgListener = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog,int which){
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    finish();
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Загружаем меню из menu_main.xml
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                settingsDialog.show(getFragmentManager(),"dlg_settings");
                break;
            case R.id.action_feedbackm:
                Intent feed = new Intent(this,FeedbackActivity.class);
                startActivity(feed);
                break;
            case R.id.action_supportm:
                Intent support = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.support_url)));
                startActivity(support);
                break;
            case R.id.action_licensem:
                licenseDialog.show(getFragmentManager(),"dlg_licensem");
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    
    private boolean LoadData(String fileName)  {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(fileName)));


                ipAddr = br.readLine(); //Читаем ip адрес
                userKey = br.readLine();//Читаем ключ

                String buf = br.readLine();
            if(buf != null)
            if(buf.equals("1"))
                isValidate = true;
            else isValidate = false;

                Toast.makeText(this, "IP loaded!", Toast.LENGTH_SHORT).show();
            if(ipAddr == null)
                ipAddr ="0.0.0.0";
            if(userKey == null)
                userKey = "null";


            br.close();
            return true;
        }
        catch(FileNotFoundException ex) {
            Toast.makeText(this,"Dictionary file not found!",Toast.LENGTH_LONG).show();
            return false;
        }
        catch(IOException ex) {
            Toast.makeText(this,ex.toString(),Toast.LENGTH_LONG).show();
            return false;
        }
    }
    
    private void SaveData(String fileName) {

        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput(fileName,MODE_PRIVATE)));

            bw.write(ipAddr + "\n"); //Пишем ip адрес
            bw.write(userKey + "\n");

            if(isValidate)
                bw.write("1");
            else bw.write("0");

            bw.close();
        }
        catch(IOException ex) {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        }
        catch(NullPointerException ex) {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        }


    }
    void addDictionary(String dictionaryName, String[] dictionaryElements,String[] elementsToSend) {
        MainActivity.dict.put(MainActivity.dict.size(),dictionaryName);
        MainActivity.dictElements.put(MainActivity.dict.size() - 1, dictionaryElements);


        for(int i = 0; i < dictionaryElements.length;i++)
        {
            MainActivity.elementToSend.put(MainActivity.dictElements.get(MainActivity.dict.size()-1)[i],elementsToSend[i]);
        }


    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CurrentDictionaryNumber = DictList.getCheckedItemPosition();

            Intent inten = new Intent(this, DictElementsActivity.class);
            startActivity(inten);



    }
}

class ArrayWImgAdapter extends  ArrayAdapter<String>
{
    private final Context context;
    private final String titles[];
    private final Bitmap images[];

    ArrayWImgAdapter(Context c,String[] tlts,Bitmap[] imgs) {
        super(c,R.layout.image_view,tlts);
        this.titles = tlts;
        this.context = c;
        this.images = imgs;
    }

    @Override
    public View getView(int Position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater =(LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.image_view, parent, false);
        TextView tv = (TextView)view.findViewById(R.id.textView);
        ImageView iv = (ImageView)view.findViewById(R.id.imgView);

        tv.setText(titles[Position]);

        if(images.length > 0 && images[Position] != null)
        iv.setImageBitmap(images[Position]);
        else
        iv.setImageResource(R.mipmap.ic_launcher1);

        return view;
    }




}

class TaskLoadImgs extends AsyncTask<String[], Void, Void> {
    @Override
    protected Void doInBackground(String[] ... params) {
        try{
            for(int i = 0; i < params[0].length; i++){
                URL url = new URL(params[0][i]);
                URLConnection connection = url.openConnection();

                Bitmap img = BitmapFactory.decodeStream(connection.getInputStream());

                MainActivity.images.add(img);


            }

        }
        catch(FileNotFoundException ex){
            Log.d("DD","TaskLoadImgs FILE NOT FOUND: " + ex.toString());

        }
        catch(Exception ex){
            Log.d("DD","TaskLoadImgs Error in doInBackground " + ex.toString());
        }
        return null;
    }

}

class TaskLicense extends AsyncTask<String,Void,Void> {
    BufferedReader br;

    @Override
    protected Void doInBackground(String... params) {
        try {
            URL url;
            if(params[0].endsWith("/"))
                url = new URL("http://api.microsofttranslator.com/V2/Http.svc/Translate?appId=FE345E5DE648285BC278902E265503D18C311BBF&text=test&from=en&to=ru");
            else    url = new URL(params[0]+ "/" + params[1] + "/" + params[2] + "/" );

            URLConnection connection = url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String content = "";

            String buf = "";
            while((buf = br.readLine()) != null)
                content += buf;
            if(content.contains("true"))
                MainActivity.isValidate = true;
            else MainActivity.isValidate = false;
            MainActivity.content12 = content;
        }
        catch(FileNotFoundException ex){
            MainActivity.isValidate = false;
            Log.d("DD","PostExecuteErr: " +ex.toString());
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
            if(br != null)
            br.close();
        }
        catch(Exception ex){
            Log.d("DD","PostExecuteErr: " +ex.toString());
        }
        super.onPostExecute(aVoid);
    }
}