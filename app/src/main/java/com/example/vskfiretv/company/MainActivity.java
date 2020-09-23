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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.vskfiretv.company.reporter.DynamicCapabilityReporter;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.vskfiretv.company.utils.Constants.ACTION_SEARCH_DISPLAY;
import static com.example.vskfiretv.company.utils.Constants.EXTRA_SEARCHED_MOVIES;
import static com.example.vskfiretv.company.utils.Constants.EXTRA_SEARCH_TEXT;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private VSKReferenceApplication myVSKReferenceApplication;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        myVSKReferenceApplication = VSKReferenceApplication.getInstance();
        super.onCreate(savedInstanceState);
        Log.i(TAG, "MainActivity.onCreate");

        setContentView(R.layout.activity_main);

        // Report Dynamic capabilities to the VSK Agent.
        // You can delay this step until active user signed-in to your application.
        final DynamicCapabilityReporter reporter = myVSKReferenceApplication.getDynamicCapabilityReporter();
        // Spin a background thread to report the capabilities
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(reporter::reportDynamicCapabilities);

        // Requesting a Test directive from the VSK Agent
        executorService.execute(reporter::requestTestDirectiveFromVSKAgent);

        Log.i(TAG, "MainActivity created and initialized");
    }

    public void showResults(final String searchTerm, final List<Movie> searchedMovies) {
        final MainFragment fragmentDemo = (MainFragment) getFragmentManager().findFragmentById(R.id.main_browse_fragment);
        fragmentDemo.displaySearchResults(searchTerm, searchedMovies);
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.i(TAG, "MainActivity fragment attached");

        final Intent intent = getIntent();
        if(intent != null) {
            onNewIntent(intent);
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        Log.i(TAG, MessageFormat.format("Received a new Intent {0}", intent));
        super.onNewIntent(intent);

        final String intentAction = intent.getAction();
        if(ACTION_SEARCH_DISPLAY.equals(intentAction)) {
            final String searchText = (String) intent.getSerializableExtra(EXTRA_SEARCH_TEXT);
            final List<Movie> searchedMovies = (List<Movie>) intent.getSerializableExtra(EXTRA_SEARCHED_MOVIES);

            if(searchText != null && searchedMovies != null) {
                showResults(searchText, searchedMovies);
            }
        } else {
            Log.i(TAG, "Unknown Intent received");
        }

        Log.i(TAG, "Finished processing the received intent.");
    }
}
