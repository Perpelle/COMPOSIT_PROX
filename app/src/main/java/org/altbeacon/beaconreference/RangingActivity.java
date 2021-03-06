package org.altbeacon.beaconreference;

import java.util.Collection;

import android.app.Activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import Proxemic.Dilmo;
import Proxemic.ProxZone;

public class RangingActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    //Permet de définir les zones  proxémiques
    ProxZone proxzone;
    String zoneProxemique;
    MediaPlayer alarme;

    @Override
    // permet de demarrer comme la methode main dans java
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        //Permet de définir les zones proxémiques
        proxzone = new ProxZone(0.5D, 1.0D, 1.5D, 2.0D);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.unbind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {

        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  "+beacons.size());
                    Beacon firstBeacon = beacons.iterator().next();
                    logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
                    //Log.d("m1","distance "+firstBeacon.getDistance());
                    Dilmo d= new Dilmo (proxzone);
                    d.setProxemicDistance(firstBeacon.getDistance());
                    String a=d.getProxemicZoneByDistance();
                    Log.d("m1",a +firstBeacon.getDistance());
                    // mettre void afficher informations
                    String info = "projet confidentiel";
                    alarme = MediaPlayer.create(getApplicationContext(), R.raw.alarme1);
                    alarme.setLooping(true);
                    alarme.seekTo(0);
                    if (a.equals("intimiZone") && !MonitoringActivity.spinnerValue.equals("Partenaire")) {
                        alarme.setVolume(3f,3f);
                        alarme.start();

                    } else if (a.equals("personalZone") && !MonitoringActivity.spinnerValue.equals("Partenaire")) {
                        //Alarme sur le telephone
                        alarme.setVolume(0.5f,0.5f);
                        alarme.start();

                    } else if (a.equals("socialZone") && !MonitoringActivity.spinnerValue.equals("Partenaire")) {
                        //Afficher certaines informations
                        if(alarme.isPlaying()) {
                            alarme.pause();
                        }
                        Log.d("DIST", info);

                    } else if (a.equals("publicZone") && !MonitoringActivity.spinnerValue.equals("Partenaire")) {
                        //Afficher certaines informations
                        if(alarme.isPlaying()) {
                            alarme.pause();
                        }
                        Log.d("DIST", info);
                    } else if (MonitoringActivity.spinnerValue.equals("Partenaire")){
                        //Afficher certaines informations

                    }
                }
            }

        };


        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {   }
    }

    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                editText.append(line+"\n");
            }
        });
    }
}
