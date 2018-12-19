package com.example.mocko.someprojectandroid;


import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class MySmsReceiver extends BroadcastReceiver {
    //PDU kljuc
    public static final String pdu_type = "pdus";

    private String latitude;
    private String longitude;

    /*
        The SMS broadcast Intent includes the incoming SMS details. To extract the array of SmsMessage objects
        packaged within the SMS broadcast Intent bundle, use the pdu extras key to extract an array of
        SMS PDUs (protocol description units — used to encapsulate an SMS message and its metadata),
        each of which represents an SMS message. To convert each PDU byte array into an SMS Message object,

        Extracting SMS messages from Incoming SMS Intent broadcasts
        Available for Bundle bundle = intent.getExtras(); download on , _ _ . .
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++)
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        Each SmsMessage contains the SMS message details, including the originating address (phone number),
        time stamp, and the message body.
    */

    /*
        Metoda OnReceive se poziva automatski od strane BroadcastReceiver klase
        svaki put kada dodje neka poruka na sistemsku aplikaciju

        @param context :Sadrzaj u kojem se trenutno receiver nalazi
        @param intent :sadrzi podatke koji su pristigli na broadcast
    */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        //string generisan za sadrzaj poruke
        String strMessage = "";
        //string koji sadrzi dolazeci broj
        String strReciveAddress="";
        String format = bundle.getString("format");

    /*
        Posto SMS broadcast Intent sadrzi detalje pristigle poruke, potrebno je izvuci SMS sadrzaj
        koji je upakovani u SMS broadcast Intent. Da bi se to uradilo koristi se pdu kljuc.
        PDU(Protocol Description Units - koristi se za enkapsulaciju SMS poruke i njenih podataka)
        ovde je taj pdu kljuc generisan u stringu  pdu_type


        Ovde se kreira niz objekata u slucaju da jedna poruka stigne iz vise delova odnosno da granica poruke
        npr. bude 100 karaktera a pristigne poruka koja ima 300 karaktera i ona dodje iz 3 dela odnosno 3*100
        Da bi se pokupio sav sadrzaj poruka se smesta u Object[] pdus
    */
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            //proverava se build vezija da li je veca od Marshmallow
            boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);

            msgs = new SmsMessage[pdus.length];
            //ako je stigla poruka iz vise delova mora se proci vise puta kroz petlju u suprotnom ako je poslata jedna prouka
            //for petalja ce imati jednu iteraciju
            for (int i = 0; i < msgs.length; i++) {

                if (isVersionM) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {

                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                //vadi broj posiljaoca pristigle poruke
                strReciveAddress= msgs[i].getOriginatingAddress();

                //provera da li je broj odgovarajuci
                //if(strReciveAddress.equals("0621561305")){

                    //vadjenje sadrzaja poruke
                    strMessage = msgs[i].getMessageBody();
                    Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();

                    //Poziv funkcije za izdvajanje koordinata iz sadrzaja poruke
                    splitCordinate(strMessage);

                    /************************  I resenje  ***************************

                        //Poziv Google Maps aplikacije
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

                    *****************************************************************/

                    /****************************************************************

                     II Resenje - koriscenje custom pravljene google map

                     ****************************************************************/

                    //poziv main activty-ja
                    Intent mapIntent = new Intent(context, MapsActivity.class);
                    //prosledjivanje geografske sirine i duzine kroz intent
                    mapIntent.putExtra("latitude",getLatitude());
                    mapIntent.putExtra("longitude",getLongitude());
                    mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //poziv mape
                    context.startActivity(mapIntent);
               //}
            }
        }
    }
    //funkcija koja se koristi za vadjenje koordinata iz sadrzaja poruke i
    // deli koordinate na dva dela po geografoskoj sirini i duzini
    public void splitCordinate(String strMessage){
        char [] reciveMessage= strMessage.toCharArray();

        String [] coordiante = new String[]{"",""};
        int i=0;
        while(i!=1) {
            for (int j = 0; j < reciveMessage.length; j++) {
                if (reciveMessage[j] == ',') {
                    i++;
                }
                else if ((reciveMessage[j] >= '0' && reciveMessage[j] <= '9')||reciveMessage[j]=='-'||reciveMessage[j]=='.') {
                    coordiante[i] += String.valueOf(reciveMessage[j]);
                }
            }

        }
        //setovanje geografske sirine i duzine
        setLatitude(coordiante[0]);
        setLongitude(coordiante[1]);
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}