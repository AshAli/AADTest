package com.example.android.aadtest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /*
    LOGGING
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /*
    NOTIFICATION
     */
    // Used in API26 (Android 8.0) and above
    // Allow to group notifications so that can configure behavior
    // Also allows users to turn certain channels on/off
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotifyManager;
    // To associate the notification with a notification ID
    // So that code can update or cancel the notification in the future
    private static final int NOTIFICATION_ID = 0;
    // Unique constant member variable to represent the update notification action for broadcast receiver
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.example.android.notifyme.ACTION_UPDATE_NOTIFICATION";
    private NotificationReceiver mReceiver = new NotificationReceiver();

    /*
    JOB SCHEDULER
     */
    private static final int JOB_ID = 0;
    private JobScheduler mScheduler;
    // Values from Fragment
    private int mOverride;
    private int mNetworkType;
    private boolean mIdleReq;
    private boolean mChargingReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        LOGGING
        SNACKBAR
        ACTIVITIES & INTENTS
        */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG,"fab Pressed");
                Toast.makeText(MainActivity.this, "fab pressed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,RecyclerViewActivity.class);
                startActivity(intent);
            }
        });

        /*
        LOGGING
        TOAST
        */
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG,"button Pressed");
                Snackbar.make(view, "Button is pressed", Snackbar.LENGTH_LONG).show();
            }
        });

        /*
        NOTIFICATION
        */
        createNotificationChannel();
        // To register broadcast receiver for notifications
        registerReceiver(mReceiver,new IntentFilter(ACTION_UPDATE_NOTIFICATION));

        /*
        LOGGING
        NOTIFICATION
        */
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG,"button2 Pressed");
                sendNotification();
            }
        });

        /*
        LOGGING
        NOTIFICATION
        */
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG,"button3 Pressed");
                updateNotification();
            }
        });

        /*
        LOGGING
        NOTIFICATION
        */
        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG,"button4 Pressed");
                cancelNotification();
            }
        });

        /*
        LOGGING
        JOB SCHEDULER
        */
        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG,"button5 Pressed");
                scheduleJob();
            }
        });

        /*
        LOGGING
        JOB SCHEDULER
        */
        Button button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG,"button6 Pressed");
                cancelJobs();
            }
        });

        /*
        LOGGING
        MENUS & PICKERS
        */
        Button button7 = (Button) findViewById(R.id.button7);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG,"button7 Pressed");
                onClickShowAlert();
            }
        });

        /*
        NAVIGATION DRAWER
        */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
        JOB SCHEDULER ++
        */
        mOverride = getIntent().getIntExtra("OVERRIDE_DEADLINE_ID", 0);
        mNetworkType = getIntent().getIntExtra("NETWORK_TYPE_ID", 1);
        mIdleReq = !(getIntent().getIntExtra("REQUIRE_IDLE_ID", 0) == 0);
        mChargingReq = !(getIntent().getIntExtra("REQUIRE_CHARGING_ID", 1) == 0);

        /*
        APP SETTINGS
        */
        android.support.v7.preference.PreferenceManager
                .setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences sharedPref =
                android.support.v7.preference.PreferenceManager
                        .getDefaultSharedPreferences(this);
        Boolean switchPref = sharedPref.getBoolean
                (SettingsActivity.KEY_PREF_EXAMPLE_SWITCH, false);
        Toast.makeText(this, switchPref.toString(),
                Toast.LENGTH_SHORT).show();
    }

    /*
    NOTIFICATION
     */
    @Override
    protected void onDestroy() {
        // To deregister broadcast receiver for notifications
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    /*
    NOTIFICATION
    ACTIVITIES & INTENTS
     */
    public void sendNotification() {
        // Intent to update notification
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        // Add Action to update notification
        notifyBuilder.addAction(R.drawable.ic_android, "Update Notification", updatePendingIntent);
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    /*
    NOTIFICATION
    */
    public void updateNotification() {
        // Load the drawable resource into the a bitmap image.
        Bitmap androidImage = BitmapFactory
                .decodeResource(getResources(), R.drawable.mascot_1);

        // Build the notification with all of the parameters using helper
        // method.
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();

        // Update the notification style to BigPictureStyle.
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated!"));

        // Deliver the notification will overwrite since same ID
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    /*
    NOTIFICATION
     */
    public void cancelNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID);
    }

    /*
    NOTIFICATION
    ACTIVITIES & INTENTS
     */
    private NotificationCompat.Builder getNotificationBuilder() {
        // Allow user to open MainActivity when tap notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text.")
                .setSmallIcon(R.drawable.ic_android)
                // Allow user to open MainActivity when tap notification
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                // Add priority and defaults to notification for backward compatibility
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return notifyBuilder;
    }

    /*
    NOTIFICATION
     */
    public void createNotificationChannel()
    {
        mNotifyManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Mascot Notification", NotificationManager
                    .IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    /*
    NOTIFICATION
     */
    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // Update the notification
            updateNotification();
        }
    }

    /*
    JOB SCHEDULER
     */
    public void scheduleJob() {
        // Will schedule a job to send notification ONLY iff on WIFI && Charging assuming no other values set
        mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName serviceName = new ComponentName(getPackageName(),
                NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName)
                                                .setRequiredNetworkType(mNetworkType)
                                                .setRequiresCharging(mChargingReq)
                                                .setRequiresDeviceIdle(mIdleReq);

        if (mOverride != 0){
            builder.setOverrideDeadline(mOverride);
        }
        else {
        }

        JobInfo myJobInfo = builder.build();
        mScheduler.schedule(myJobInfo);
        Toast.makeText(this, "Job Scheduled", Toast.LENGTH_SHORT)
                .show();
    }

    /*
    JOB SCHEDULER
    */
    public void cancelJobs() {

        if (mScheduler != null) {
            mScheduler.cancelAll();
            mScheduler = null;
            Toast.makeText(this, "Job Cancelled!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /*
    NAVIGATION DRAWER
    */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
    NAVIGATION DRAWER
    ACTIVITIES & INTENTS (WITH EXTRAS)
    */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_jobs) {
            Intent intent  = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("FRAGMENT_ID", 0);
            startActivity(intent);
        } else if (id == R.id.nav_pickers) {
            Intent intent  = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("FRAGMENT_ID", 1);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            Intent intent  = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("FRAGMENT_ID", 2);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(MainActivity.this,RecyclerViewActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    MENU & PICKER
    THEMES
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // Change the label of the menu based on the state of the app.
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if(nightMode == AppCompatDelegate.MODE_NIGHT_YES){
            menu.findItem(R.id.action_nightmode).setTitle(R.string.day_mode);
        } else{
            menu.findItem(R.id.action_nightmode).setTitle(R.string.night_mode);
        }
        return true;
    }

    /*
    MENU & PICKER
    ACTIVITIES & INTENTS (WITH EXTRAS)
    APP SETTINGS
    THEMES
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent  = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("FRAGMENT_ID", 2);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_jobs) {
            Intent intent  = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("FRAGMENT_ID", 0);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_pickers) {
            Intent intent  = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("FRAGMENT_ID", 1);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_nightmode) {
            // Get the night mode state of the app.
            int nightMode = AppCompatDelegate.getDefaultNightMode();
            //Set the theme mode for the restarted activity
            if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode
                        (AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode
                        (AppCompatDelegate.MODE_NIGHT_YES);
            }
            // Recreate the activity for the theme change to take effect.
            recreate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    MENU & PICKER
    */
    public void onClickShowAlert() {
        AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(MainActivity.this);
        // Set the dialog title and message.
        myAlertBuilder.setTitle("Alert");
        myAlertBuilder.setMessage("Click OK to continue, or Cancel to stop:");
        // Add the dialog buttons.
        myAlertBuilder.setPositiveButton("OK", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked OK button.
                        Toast.makeText(getApplicationContext(), "Pressed OK",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        myAlertBuilder.setNegativeButton("Cancel", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled the dialog.
                        Toast.makeText(getApplicationContext(), "Pressed Cancel",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        // Create and show the AlertDialog.
        myAlertBuilder.show();
    }

}
