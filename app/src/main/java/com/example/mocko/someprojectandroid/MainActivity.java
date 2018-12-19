package com.example.mocko.someprojectandroid;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    // Kreira activity, postavlja view, poziva metodu checkForSmsPermission() za SMS permisiju
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForSmsPermission();
    }

    //Metoda koja proverava da li je prihvacena perimisja(dozvola) za SMS
    private void checkForSmsPermission() {
        //Proverava da li je permisija odobrena- ako nije onda se ulazi u IF selekciju
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Poziva dijalog koji se pojavljuje na uredjaju preko kojeg korisnik prihvata permisiju
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    //Metoda koja se poziva kada u activity_main klikne na ikonicu za slanje poruke
    public void smsSendMessage(View view) {
        //Kreira se referenca na editText_main u activity_main za unos telefonskog broja
        EditText phoneNumber = (EditText) findViewById(R.id.editText_main);
        //U string destinationAddress se pokupi uneti broj iz phoneNumber
        String destinationAddress = phoneNumber.getText().toString();
        //Kreira se smsBody koji refencira sms_message za unos koordinata
        EditText smsBody = (EditText) findViewById(R.id.sms_message);
        //U string smsMessage se sadrzaj iz smsBody
        String smsMessage = smsBody.getText().toString();

        String scAddress = null;

        PendingIntent sentIntent = null, deliveryIntent = null;
        //Poziv metoda za proveru permisije
        checkForSmsPermission();

        //Kreiranje smsManager objekta za slanje poruke na drugi broj(u ovom slucaju na broj koji je u GPS lokatoru)
        SmsManager smsManager = SmsManager.getDefault();
        /*
        Poziv metode za slanje SMS poruke na GPS lokator
        @param destinationAddress :Boj telefona koji je pokupljen iz phoneNumber
        @param scAddress :Prosledjuje se null vrednost da bi se koristila default vrednost SMSC
                    SMSC vrednost je default vrednost u sistemskoj aplikaciji SMS-a
        @param smsMesssage :Za slanje sadrzaja poruke
        @param sentIntent :Objekat PendingIntent koji se koristi za proveru da li je poruka uspesno poslata
        @param deliveryIntent :Objekat PendingIntent koji se koristi za proveru da li je poruka uspesno isporucena
        */
        smsManager.sendTextMessage(destinationAddress, scAddress, smsMessage, sentIntent, deliveryIntent);
    }
}
