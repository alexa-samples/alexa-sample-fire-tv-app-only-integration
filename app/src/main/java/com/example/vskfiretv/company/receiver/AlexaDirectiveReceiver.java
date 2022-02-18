package com.example.vskfiretv.company.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amazon.alexa.vsk_app_agent_api.VSKIntentConstants;
import com.example.vskfiretv.company.DetailsActivity;
import com.example.vskfiretv.company.MainActivity;
import com.example.vskfiretv.company.PlaybackActivity;
import com.example.vskfiretv.company.VSKReferenceApplication;
import com.example.vskfiretv.company.Movie;
import com.example.vskfiretv.company.MovieList;
import com.example.vskfiretv.company.reporter.DynamicCapabilityReporter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.vskfiretv.company.utils.Constants.ACTION_PLAYBACK_ADJUST_SEEK_POSITION;
import static com.example.vskfiretv.company.utils.Constants.ACTION_PLAYBACK_PAUSE;
import static com.example.vskfiretv.company.utils.Constants.ACTION_PLAYBACK_PLAY;
import static com.example.vskfiretv.company.utils.Constants.ACTION_PLAYBACK_REWIND;
import static com.example.vskfiretv.company.utils.Constants.ACTION_PLAYBACK_STOP;
import static com.example.vskfiretv.company.utils.Constants.ACTION_SEARCH_DISPLAY;
import static com.example.vskfiretv.company.utils.Constants.ADJUST_SEEK_POSITION;
import static com.example.vskfiretv.company.utils.Constants.CHANGE_CHANNEL;
import static com.example.vskfiretv.company.utils.Constants.DEFAULT_FAST_FORWARD_TIME;
import static com.example.vskfiretv.company.utils.Constants.DELTA_POSITION_MILLI_SECONDS_JSON_NAME;
import static com.example.vskfiretv.company.utils.Constants.DIRECTIVE_VERSION_3_1;
import static com.example.vskfiretv.company.utils.Constants.ENTITIES_JSON_NAME;
import static com.example.vskfiretv.company.utils.Constants.EXTRA_PLAYBACK_SEEK_TIME_OFFSET;
import static com.example.vskfiretv.company.utils.Constants.FAST_FORWARD;
import static com.example.vskfiretv.company.utils.Constants.IDENTIFIER_JSON_NAME;
import static com.example.vskfiretv.company.utils.Constants.LAUNCH_TARGET;
import static com.example.vskfiretv.company.utils.Constants.NEXT;
import static com.example.vskfiretv.company.utils.Constants.PAUSE;
import static com.example.vskfiretv.company.utils.Constants.PLAY;
import static com.example.vskfiretv.company.utils.Constants.PREVIOUS;
import static com.example.vskfiretv.company.utils.Constants.REWIND;
import static com.example.vskfiretv.company.utils.Constants.EXTRA_SEARCHED_MOVIES;
import static com.example.vskfiretv.company.utils.Constants.SEARCH_AND_DISPLAY_RESULTS;
import static com.example.vskfiretv.company.utils.Constants.SEARCH_AND_PLAY;
import static com.example.vskfiretv.company.utils.Constants.EXTRA_SEARCH_TEXT;
import static com.example.vskfiretv.company.utils.Constants.SEARCH_TEXT_JSON_NAME;
import static com.example.vskfiretv.company.utils.Constants.SEND_KEYSTROKE;
import static com.example.vskfiretv.company.utils.Constants.START_OVER;
import static com.example.vskfiretv.company.utils.Constants.STOP;
import static com.example.vskfiretv.company.utils.Constants.CURRENT_CAPABILITIES;
import static com.example.vskfiretv.company.utils.Constants.TRANSCRIBED_TEXT_JSON_NAME;
import static com.example.vskfiretv.company.utils.Constants.URI_FOR_PLAY_SOMETHING;
import static com.example.vskfiretv.company.utils.Constants.URI_FOR_PLAY_SOMETHING_ELSE;
import static com.example.vskfiretv.company.utils.Constants.VALUE_JSON_NAME;

/**
 * A broadcast receiver that handles various intents from VSK Agent. Alexa directives sent from
 * VSK Agent are handled here.
 */
public class AlexaDirectiveReceiver extends BroadcastReceiver {

    private static final String TAG = AlexaDirectiveReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i(TAG, MessageFormat.format("Handling Intent from VSK Agent: {0}", intent));
        if(context == null || intent == null) {
            return;
        }

        if (VSKIntentConstants.ACTION_ALEXA_DIRECTIVE.equals(intent.getAction())) {
            final String directiveNameSpace = intent.getStringExtra(VSKIntentConstants.EXTRA_DIRECTIVE_NAMESPACE);
            final String directiveName = intent.getStringExtra(VSKIntentConstants.EXTRA_DIRECTIVE_NAME);
            final String directivePayload = intent.getStringExtra(VSKIntentConstants.EXTRA_DIRECTIVE_PAYLOAD);
            final String directivePayloadVersion = intent.getStringExtra(VSKIntentConstants.EXTRA_DIRECTIVE_PAYLOAD_VERSION);

            Log.i(TAG, MessageFormat.format("Received an Alexa Directive: {0}#{1} with payload version: {2} and payload: {3}",
                    directiveNameSpace, directiveName, directivePayloadVersion, directivePayload));

            if (directiveName != null) {
                if (SEARCH_AND_PLAY.equals(directiveName)) {
                    handleSearchAndPlay(directivePayload);
                } else if (SEARCH_AND_DISPLAY_RESULTS.equals(directiveName)) {
                    handleSearchAndDisplayResults(directivePayload);
                } else if (PAUSE.equals(directiveName)) {
                    handlePause();
                } else if (PLAY.equals(directiveName)) {
                    handlePlay();
                } else if (STOP.equals(directiveName)) {
                    handleStop();
                } else if (NEXT.equals(directiveName)) {
                    handleNext();
                } else if (PREVIOUS.equals(directiveName)) {
                    handlePrevious();
                } else if (FAST_FORWARD.equals(directiveName)) {
                    handleFastForward();
                } else if (REWIND.equals(directiveName)) {
                    handleRewind();
                } else if (START_OVER.equals(directiveName)) {
                    handleStartOver();
                } else if (ADJUST_SEEK_POSITION.equals(directiveName)) {
                    handleAdjustSeekPosition(directivePayload);
                } else if (CHANGE_CHANNEL.equals(directiveName)) {
                    handleChangeChannel();
                } else if (SEND_KEYSTROKE.equals(directiveName)) {
                    handleSendKeystroke();
                } else if(LAUNCH_TARGET.equals(directiveName) && DIRECTIVE_VERSION_3_1.equals(directivePayloadVersion)) {
                    handleLaunchTarget(directivePayload);
                } else if(CURRENT_CAPABILITIES.equals(directiveName)) {
                    handleTestDirective();
                } else {
                    Log.i(TAG, "Unknown Alexa Directive. Sending a failure response intent to the VSK Agent");
                    sendPendingIntentResponse(context, intent, false);
                    return;
                }

                // Send the PendingIntent back to the VSK agent if handling the directive is successful
                Log.i(TAG, "Sending a success response intent to the VSK Agent");
                sendPendingIntentResponse(context, intent, true);
            } else {
                Log.i(TAG, "Received an empty directive from the VSK Agent");
            }
        } else if (VSKIntentConstants.ACTION_REPORT_CAPABILITIES.equals(intent.getAction())) {
            Log.i(TAG, "Received a request to report the dynamic capabilities");

            // Report Dynamic capabilities to the VSK Agent
            final DynamicCapabilityReporter reporter = VSKReferenceApplication.getInstance().getDynamicCapabilityReporter();
            // Spin a background thread to report the capabilities
            final ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(reporter::reportDynamicCapabilities);
        } else {
            Log.i(TAG, "Unknown Intent from the VSK Agent");
        }

        Log.i(TAG, "Finished handling Intent from the VSK Agent");
    }

    private void handleLaunchTarget(final String directivePayload) {
        Log.i(TAG, "Handling LaunchTarget directive...");
        String shortcutId = getShortcutIdFromDirectivePayload(directivePayload);
        if (URI_FOR_PLAY_SOMETHING.equals(shortcutId) || URI_FOR_PLAY_SOMETHING_ELSE.equals(shortcutId)) {
            List<Movie> moviesList = MovieList.getList();
            Random rand = new Random();
            playMovie(moviesList.get(rand.nextInt(moviesList.size())));
        }
        Log.i(TAG, "Handling LaunchTarget directive finished");
    }

    private String getShortcutIdFromDirectivePayload(final String launchTargetPayload) {
        try {
            final JsonParser jsonParser = new JsonParser();
            final JsonElement launchTargetPayloadJsonTree = jsonParser.parse(launchTargetPayload);
            if (launchTargetPayloadJsonTree.isJsonObject()) {
                final JsonObject launchTargetJsonObject = launchTargetPayloadJsonTree.getAsJsonObject();
                return launchTargetJsonObject.get(IDENTIFIER_JSON_NAME).getAsString();
            }
        } catch (final Exception ex) {
            Log.e(TAG, "Error processing LaunchTarget directive", ex);
        }
        return null;
    }

    private void sendPendingIntentResponse(final Context context, final Intent intent, final boolean directiveExecutionStatus) {
        final PendingIntent pendingIntent = intent.getParcelableExtra(VSKIntentConstants.EXTRA_DIRECTIVE_RESPONSE_PENDING_INTENT);
        if(pendingIntent != null) {
            final Intent responseIntent = new Intent().putExtra(VSKIntentConstants.EXTRA_DIRECTIVE_STATUS, directiveExecutionStatus);
            try {
                pendingIntent.send(context, 0 , responseIntent);
            } catch (final PendingIntent.CanceledException ex) {
                Log.e(TAG, "Error sending pending intent to the VSK agent", ex);
            }
        }
    }

    private void handleSearchAndPlay(final String directivePayload) {
        Log.i(TAG, "Handling SearchAndPlay directive...");

        final Movie movieToBePlayed = getMovieFromDirectivePayload(directivePayload);
        playMovie(movieToBePlayed);

        Log.i(TAG, "Handling SearchAndPlay directive finished");
    }

    private void playMovie(Movie movieToBePlayed) {
        final String movieName = movieToBePlayed.getTitle();
        Log.d(TAG, "Playing Movie " + movieName);

        final Intent playIntent = new Intent();
        final String packageName = VSKReferenceApplication.getInstance().getPackageName();
        playIntent.setClassName(packageName, PlaybackActivity.class.getName());
        playIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // PlaybackActivity expects a movie to be currently selected, set this now in case there wasn't one
        playIntent.putExtra(DetailsActivity.MOVIE, movieToBePlayed);
        VSKReferenceApplication.getInstance().startActivity(playIntent);
    }

    private Movie getMovieFromDirectivePayload(final String searchAndPlayPayload) {
        // Process directive payload here and build an appropriate movie object to be played

        // For demonstration purposes, grabbing the first item in the sample movies list. Doesn't correspond to movie ID
        final Movie someMovie = MovieList.getList().get(0);

        final JsonParser jsonParser = new JsonParser();
        final JsonElement searchAndPlayPayloadJsonTree = jsonParser.parse(searchAndPlayPayload);

        if(searchAndPlayPayloadJsonTree.isJsonObject()) {
            final JsonObject searchPayloadJsonObject = searchAndPlayPayloadJsonTree.getAsJsonObject();

            final JsonObject searchTermJsonObject = searchPayloadJsonObject.getAsJsonObject(SEARCH_TEXT_JSON_NAME);
            final String searchAndPlayText = searchTermJsonObject.get(TRANSCRIBED_TEXT_JSON_NAME).getAsString();
            final JsonArray searchEntitiesJsonArray = searchPayloadJsonObject.getAsJsonArray(ENTITIES_JSON_NAME);

            return MovieList.buildMovieInfo(someMovie.getMovieId(), searchAndPlayText,
                    searchEntitiesJsonArray.toString(), someMovie.getStudio(), someMovie.getVideoUrl(),
                    someMovie.getCardImageUrl(), someMovie.getBackgroundImageUrl());
        } else {
            Log.w(TAG, "Invalid json for SearchAndPlay payload");
        }
        return someMovie;
    }

    private void handleTestDirective() {
        Log.i(TAG, "Handling Test directive...");
        // TODO: Implement sample test directive
    }

    private void handleSearchAndDisplayResults(final String searchPayload) {
        Log.i(TAG, "Handling SearchAndDisplayResults directive...");

        final JsonParser jsonParser = new JsonParser();
        final JsonElement searchPayloadJsonTree = jsonParser.parse(searchPayload);

        if(searchPayloadJsonTree.isJsonObject()) {
            final JsonObject searchPayloadJsonObject = searchPayloadJsonTree.getAsJsonObject();

            final JsonObject searchTermJsonObject = searchPayloadJsonObject.getAsJsonObject(SEARCH_TEXT_JSON_NAME);
            final String searchText = searchTermJsonObject.get(TRANSCRIBED_TEXT_JSON_NAME).getAsString();

            final JsonArray searchEntitiesJsonArray = searchPayloadJsonObject.getAsJsonArray(ENTITIES_JSON_NAME);
            final Iterator<JsonElement> searchEntitiesIterator = searchEntitiesJsonArray.iterator();

            final Random rand = new Random();
            final List<Movie> movieList = MovieList.getList();
            int moviesCount = movieList.size();
            final List<Movie> searchedMovies = new ArrayList<>();

            while (searchEntitiesIterator.hasNext()) {
                final JsonElement entityElement = searchEntitiesIterator.next();
                final JsonObject entity = entityElement.getAsJsonObject();
                final String entityValue = entity.get(VALUE_JSON_NAME).getAsString();
                final String entityJsonString = entityElement.toString();
                final Movie movie = movieList.get(rand.nextInt(moviesCount));

                searchedMovies.add(MovieList.buildMovieInfo(movie.getMovieId(), entityValue,
                        entityJsonString, movie.getStudio(), movie.getVideoUrl(),
                        movie.getCardImageUrl(), movie.getBackgroundImageUrl()));
            }

            final Intent searchIntent = new Intent();
            searchIntent.setAction(ACTION_SEARCH_DISPLAY);
            final String packageName = VSKReferenceApplication.getInstance().getPackageName();
            searchIntent.setClassName(packageName, MainActivity.class.getName());
            searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            searchIntent.putExtra(EXTRA_SEARCH_TEXT, searchText);
            searchIntent.putExtra(EXTRA_SEARCHED_MOVIES, (Serializable) searchedMovies);
            VSKReferenceApplication.getInstance().startActivity(searchIntent);
        } else {
            Log.w(TAG, "Invalid json for search payload");
        }

        Log.i(TAG, "Handling SearchAndDisplayResults directive finished");
    }

    private void handlePause() {
        Log.i(TAG, "Handling Pause directive...");

        final Intent pauseIntent = new Intent(ACTION_PLAYBACK_PAUSE);
        pauseIntent.setPackage(VSKReferenceApplication.getInstance().getPackageName());
        VSKReferenceApplication.getInstance().sendBroadcast(pauseIntent);

        Log.i(TAG, "Handling Pause directive finished");
    }

    private void handlePlay() {
        Log.i(TAG, "Handling Play directive...");

        final Intent playIntent = new Intent(ACTION_PLAYBACK_PLAY);
        playIntent.setPackage(VSKReferenceApplication.getInstance().getPackageName());
        VSKReferenceApplication.getInstance().sendBroadcast(playIntent);

        Log.i(TAG, "Handling Play directive finished");
    }

    private void handleStop() {
        Log.i(TAG, "Handling Stop directive...");

        final Intent stopIntent = new Intent(ACTION_PLAYBACK_STOP);
        stopIntent.setPackage(VSKReferenceApplication.getInstance().getPackageName());
        VSKReferenceApplication.getInstance().sendBroadcast(stopIntent);

        Log.i(TAG, "Handling Stop directive finished");
    }

    private void handleNext() {
        Log.i(TAG, "Handling Next directive...");
        //TODO: Implement Next operation
    }

    private void handlePrevious() {
        Log.i(TAG, "Handling Previous directive...");
        //TODO: Implement Previous operation
    }

    private void handleFastForward() {
        Log.i(TAG, "Handling FastForward directive...");

        final Intent fastForwardIntent = new Intent(ACTION_PLAYBACK_ADJUST_SEEK_POSITION);
        fastForwardIntent.setPackage(VSKReferenceApplication.getInstance().getPackageName());
        fastForwardIntent.putExtra(EXTRA_PLAYBACK_SEEK_TIME_OFFSET, DEFAULT_FAST_FORWARD_TIME);
        VSKReferenceApplication.getInstance().sendBroadcast(fastForwardIntent);

        Log.i(TAG, "Handling FastForward directive finished");
    }

    private void handleRewind() {
        Log.i(TAG, "Handling Rewind directive...");

        final Intent rewindIntent = new Intent(ACTION_PLAYBACK_REWIND);
        rewindIntent.setPackage(VSKReferenceApplication.getInstance().getPackageName());
        VSKReferenceApplication.getInstance().sendBroadcast(rewindIntent);

        Log.i(TAG, "Handling Rewind directive finished");
    }

    private void handleStartOver() {
        Log.i(TAG, "Handling StartOver directive...");

        final Intent startOverIntent = new Intent(ACTION_PLAYBACK_REWIND);
        startOverIntent.setPackage(VSKReferenceApplication.getInstance().getPackageName());
        VSKReferenceApplication.getInstance().sendBroadcast(startOverIntent);

        Log.i(TAG, "Handling StartOver directive finished");
    }

    private void handleAdjustSeekPosition(final String directivePayload) {
        Log.i(TAG, "Handling AdjustSeekPosition directive...");

        final JsonParser jsonParser = new JsonParser();
        final JsonElement directivePayloadJsonTree = jsonParser.parse(directivePayload);

        if(directivePayloadJsonTree.isJsonObject()) {
            final JsonObject directivePayloadJsonObject = directivePayloadJsonTree.getAsJsonObject();
            final long seekPositionInMilliSeconds = directivePayloadJsonObject.get(DELTA_POSITION_MILLI_SECONDS_JSON_NAME).getAsInt();

            final Intent seekIntent = new Intent(ACTION_PLAYBACK_ADJUST_SEEK_POSITION);
            seekIntent.setPackage(VSKReferenceApplication.getInstance().getPackageName());
            seekIntent.putExtra(EXTRA_PLAYBACK_SEEK_TIME_OFFSET, seekPositionInMilliSeconds);
            VSKReferenceApplication.getInstance().sendBroadcast(seekIntent);
        } else {
            Log.w(TAG, "Invalid json for Seek payload");
        }

        Log.i(TAG, "Handling AdjustSeekPosition directive finished");
    }

    private void handleChangeChannel() {
        Log.i(TAG, "Handling ChangeChannel directive...");
        //TODO: Implement ChangeChannel operation
    }

    private void handleSendKeystroke() {
        Log.i(TAG, "Handling SendKeystroke directive...");
        //TODO: Implement SendKeystroke operation
    }

}
