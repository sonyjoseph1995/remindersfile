package com.example.ashir.reminder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import com.example.ashir.reminder.NotifyMessage;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    private static final int REPEAT_TIME_IN_SECONDS = 60;
    PendingIntent pendingIntent;
    EditText editName,editSurname,editMarks ,editTextId;
    Button btnAddReminder;
    Button btnviewAll;
    Button btnDelete;
    Button btnviewUpdate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      /* AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
                REPEAT_TIME_IN_SECONDS * 1000, pendingIntent);*/


        myDb = new DatabaseHelper(this);
        final Handler handler=new Handler();
        Runnable run=new Runnable() {    // code to be run every seconds
            @Override
            public void run() {
                check();
                Log.i("test","everyyyyyyyyyyy seconds");
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(run);




        editName = (EditText)findViewById(R.id.editText_date);
        editSurname = (EditText)findViewById(R.id.editText_time);
        editMarks = (EditText)findViewById(R.id.editText_reminder);
        editTextId = (EditText)findViewById(R.id.editText_id);
        btnAddReminder = (Button)findViewById(R.id.button_add);
        btnviewAll = (Button)findViewById(R.id.button_viewAll);
        btnviewUpdate= (Button)findViewById(R.id.button_update);
        btnDelete= (Button)findViewById(R.id.button_delete);


        AddData();
        viewAll();
        UpdateData();
        DeleteData();


    }





    public void DeleteData() {       //Delete the Reminder
        btnDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer deletedRows = myDb.deleteData(editTextId.getText().toString());
                        if(deletedRows > 0)
                            Toast.makeText(MainActivity.this,"Reminder Deleted",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this,"Reminder not Deleted",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }



    //Update Reminder
    public void UpdateData() {
        btnviewUpdate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isUpdate = myDb.updateData(editTextId.getText().toString(),
                                editName.getText().toString(),
                                editSurname.getText().toString(),editMarks.getText().toString());
                        if(isUpdate == true)
                            Toast.makeText(MainActivity.this,"Reminder Updated",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this,"Reminder not Updated",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }


    //create new Reminder
    public  void AddData() {
        btnAddReminder.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = myDb.insertData(editName.getText().toString(),
                                editSurname.getText().toString(),
                                editMarks.getText().toString() );
                        if(isInserted == true)
                            Toast.makeText(MainActivity.this,"Reminder Added",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this,"Reminder not Added",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }




    //view all Reminder
    public void viewAll() {
        btnviewAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = myDb.getAllData();
                        if(res.getCount() == 0) {
                            // show message
                            showMessage("Error","Nothing found");
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();
                        while (res.moveToNext()) {
                            buffer.append("Id :"+ res.getString(0)+"\n");
                            buffer.append("DATE :"+ res.getString(1)+"\n");
                            buffer.append("TIME :"+ res.getString(2)+"\n");
                            buffer.append("REMINDER :"+ res.getString(3)+"\n\n");
                        }

                        // Show all data
                        showMessage("Data",buffer.toString());
                    }
                }
        );
    }



    //checking the Reminder's Date, Time with the System current date and time
    public void check(){
        Cursor res = myDb.getAllData();
        if(res.getCount() == 0) {
            // show message
            showMessage("Error","Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            // buffer.append("Id :"+ res.getString(0)+"\n");
            // buffer.append("REMINDER :"+ res.getString(1)+"\n");
            buffer.append("DATE :" + res.getString(1) + "\n");
            String d = buffer.toString();
            Log.i("date", d);
            buffer.setLength(0);
            buffer.append("TIME :" + res.getString(2) + "\n\n");
            String t = buffer.toString();
            Log.i("time", t);
            buffer.setLength(0);                 // clear buffer data
            Date currentTime = Calendar.getInstance().getTime();

            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy/mm/dd");
            String dateObj = curFormater.format(currentTime);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String s = sdf.format(new Date());
            Log.i("current date", dateObj);
            Log.i("current time", s);
            if (d == dateObj && t == s) {
                notifyThis();
            }
        }
    }



    //Set notification
    public void notifyThis(){
        Notification.Builder builder = new Notification.Builder(MainActivity.this);
        Intent notificationIntent = new Intent(this,NotifyMessage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("REMINDER..")
                .setContentText("text")
                .setContentIntent(pendingIntent);
        builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = builder.getNotification();
        notificationManager.notify(R.mipmap.ic_launcher, notification);
    }





    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }



}

