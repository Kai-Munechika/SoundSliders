package com.kaim808.slider3;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    public static final String NOTIFICATION_TAG = "notificationId";
    private final int NOTIFICATION_ID = 1;

    SeekBar mRingtoneSeekBar;
    SeekBar mNotificationSoundSeekBar;
    SeekBar mSystemSoundsSeekbar;
    SeekBar mMediaSeekBar;

    SeekBar[] mSeekBars;
    int[] mStreamTypes;
    AudioManager mAudioManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRingtoneSeekBar            = (SeekBar) findViewById(R.id.ringtone_seekbar);
        mNotificationSoundSeekBar   = (SeekBar) findViewById(R.id.notification_sound_seekbar);
        mSystemSoundsSeekbar        = (SeekBar) findViewById(R.id.system_seekbar);
        mMediaSeekBar               = (SeekBar) findViewById(R.id.media_seekbar);

        mSeekBars       = new SeekBar[] {mRingtoneSeekBar, mNotificationSoundSeekBar, mSystemSoundsSeekbar, mMediaSeekBar};
        mStreamTypes    = new int[]     {AudioManager.STREAM_RING, AudioManager.STREAM_NOTIFICATION, AudioManager.STREAM_SYSTEM, AudioManager.STREAM_MUSIC};

        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int streamId = 0;
                if (seekBar.getId() == R.id.ringtone_seekbar) {
                    streamId = AudioManager.STREAM_RING;
                } else if (seekBar.getId() == R.id.notification_sound_seekbar) {
                    streamId = AudioManager.STREAM_NOTIFICATION;
                } else if (seekBar.getId() == R.id.system_seekbar) {
                    streamId = AudioManager.STREAM_SYSTEM;
                } else if (seekBar.getId() == R.id.media_seekbar) {
                    streamId = AudioManager.STREAM_MUSIC;
                }

                mAudioManager.setStreamVolume(streamId, i, 0);

                for (int k = 0; k < mSeekBars.length; k++) {
                    mSeekBars[k].setProgress(mAudioManager.getStreamVolume(mStreamTypes[k]));
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        for (int i = 0; i < mSeekBars.length; i++) {
            mSeekBars[i].setMax(mAudioManager.getStreamMaxVolume(mStreamTypes[i]));
            mSeekBars[i].setProgress(mAudioManager.getStreamVolume(mStreamTypes[i]));
            mSeekBars[i].setOnSeekBarChangeListener(seekBarListener);
        }

        showNotification();

    }

    public void showNotification() {

        Intent startActivityIntent = new Intent(this, MainActivity.class);
        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(this, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent closeNotficationIntent = new Intent(this, CloseReceiver.class);
        closeNotficationIntent.putExtra(NOTIFICATION_TAG, NOTIFICATION_ID);
        PendingIntent closeNotificationPIntent = PendingIntent.getBroadcast(this, 0, closeNotficationIntent, 0);


        // need to make 1 action: one to close the notification
        NotificationCompat.Action closeNotificationAction = new NotificationCompat.Action.Builder(0, "Close", closeNotificationPIntent).build();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Adjust Volumes")
                .setContentText("Click me")
                .setOngoing(true)
                .setContentIntent(startActivityPendingIntent)
                .addAction(closeNotificationAction);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

    }




    @Override
    protected void onPause() {
        super.onPause();
        finishAndRemoveTask();
    }

    public void finish(View v) {
        onPause();
    }

}
