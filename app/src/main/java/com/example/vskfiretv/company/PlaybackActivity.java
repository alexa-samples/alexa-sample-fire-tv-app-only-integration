/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.vskfiretv.company;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import java.text.MessageFormat;

import static com.example.vskfiretv.company.utils.Constants.ACTION_PLAYBACK_ADJUST_SEEK_POSITION;
import static com.example.vskfiretv.company.utils.Constants.ACTION_PLAYBACK_PAUSE;
import static com.example.vskfiretv.company.utils.Constants.ACTION_PLAYBACK_PLAY;
import static com.example.vskfiretv.company.utils.Constants.ACTION_PLAYBACK_REWIND;
import static com.example.vskfiretv.company.utils.Constants.ACTION_PLAYBACK_STOP;
import static com.example.vskfiretv.company.utils.Constants.EXTRA_PLAYBACK_SEEK_TIME_OFFSET;

/**
 * Loads {@link PlaybackVideoFragment}.
 */
public class PlaybackActivity extends FragmentActivity {

    private static final String TAG = PlaybackActivity.class.getSimpleName();
    protected PlaybackVideoFragment playbackVideoFragment;
    protected PlaybackReceiver playbackReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            playbackVideoFragment = new PlaybackVideoFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, playbackVideoFragment,"PlaybackVideoFragment")
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "Starting the playback activity");
        super.onStart();

        playbackReceiver = new PlaybackReceiver();
        final IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(ACTION_PLAYBACK_PAUSE);
        filter.addAction(ACTION_PLAYBACK_PLAY);
        filter.addAction(ACTION_PLAYBACK_REWIND);
        filter.addAction(ACTION_PLAYBACK_STOP);
        filter.addAction(ACTION_PLAYBACK_ADJUST_SEEK_POSITION);

        Log.i(TAG, "Registering the playback receiver...");
        this.registerReceiver(playbackReceiver, filter);
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "Stopping the playback activity");
        super.onStop();

        Log.i(TAG, "Un-registering the playback receiver...");
        unregisterReceiver(playbackReceiver);
    }

    class PlaybackReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, MessageFormat.format("Received a playback intent {0}", intent));

            if(intent == null || intent.getAction() == null) {
                Log.w(TAG, "Intent or Intent action is null. Cannot process this intent.");
                return;
            }

            final String intentAction = intent.getAction();
            if(intentAction.equals(ACTION_PLAYBACK_PLAY)) {
                playbackVideoFragment.getPlayer().play();
            } else if(intentAction.equals(ACTION_PLAYBACK_PAUSE)) {
                playbackVideoFragment.getPlayer().pause();
            } else if(intentAction.equals(ACTION_PLAYBACK_REWIND)) {
                playbackVideoFragment.getPlayer().seekTo(0);
            } else if(intentAction.equals(ACTION_PLAYBACK_STOP)) {
                finish();
            } else if(intentAction.equals(ACTION_PLAYBACK_ADJUST_SEEK_POSITION)) {
                final long seekTimeInMilliSeconds = (long) intent.getSerializableExtra(EXTRA_PLAYBACK_SEEK_TIME_OFFSET);
                playbackVideoFragment.getPlayer().seekTo(playbackVideoFragment.getPlayer().getCurrentPosition()+ seekTimeInMilliSeconds);
            } else {
                Log.i(TAG, "Unknown playback intent action");
            }

            Log.i(TAG, "Finished processing the playback intent");
        }
    }

}

