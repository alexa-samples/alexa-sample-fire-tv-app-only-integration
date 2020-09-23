package com.example.vskfiretv.company.utils;

/**
 * Various Directive and Intent constants used within the application
 */
public class Constants {

    // Alexa Directive Names
    public static final String SEARCH_AND_PLAY = "SearchAndPlay";
    public static final String SEARCH_AND_DISPLAY_RESULTS = "SearchAndDisplayResults";
    public static final String PAUSE = "pause";
    public static final String PLAY = "play";
    public static final String STOP = "stop";
    public static final String NEXT = "next";
    public static final String PREVIOUS = "previous";
    public static final String FAST_FORWARD = "fastForward";
    public static final String REWIND = "rewind";
    public static final String START_OVER = "startOver";
    public static final String ADJUST_SEEK_POSITION = "adjustSeekPosition";
    public static final String CHANGE_CHANNEL = "ChangeChannel";
    public static final String SEND_KEYSTROKE = "SendKeystroke";
    public static final String CURRENT_CAPABILITIES = "CurrentCapabilities";

    // Default seek time in milli seconds when a video is fast-forwarded
    public static final long DEFAULT_FAST_FORWARD_TIME = 10000;

    //JSON field names in Alexa Directive JSON payload
    public static final String SEARCH_TEXT_JSON_NAME = "searchText";
    public static final String TRANSCRIBED_TEXT_JSON_NAME = "transcribed";
    public static final String ENTITIES_JSON_NAME = "entities";
    public static final String VALUE_JSON_NAME = "value";
    public static final String DELTA_POSITION_MILLI_SECONDS_JSON_NAME = "deltaPositionMilliseconds";

    // Search Intent Extras
    public static final String EXTRA_SEARCH_TEXT = "searchText";
    public static final String EXTRA_SEARCHED_MOVIES = "searchedMovies";

    // Playback Intent Extras
    public static final String EXTRA_PLAYBACK_SEEK_TIME_OFFSET = "seekTimeOffset";

    // Intent Actions for Search
    public static final String ACTION_SEARCH_DISPLAY = "com.example.vskfiretv.ACTION_SEARCH_DISPLAY";

    // Intent Actions for Playback operations
    public static final String ACTION_PLAYBACK_PLAY = "com.example.vskfiretv.ACTION_PLAYBACK_PLAY";
    public static final String ACTION_PLAYBACK_PAUSE = "com.example.vskfiretv.ACTION_PLAYBACK_PAUSE";
    public static final String ACTION_PLAYBACK_REWIND = "com.example.vskfiretv.ACTION_PLAYBACK_REWIND";
    public static final String ACTION_PLAYBACK_STOP = "com.example.vskfiretv.ACTION_PLAYBACK_STOP";
    public static final String ACTION_PLAYBACK_ADJUST_SEEK_POSITION = "com.example.vskfiretv.ACTION_PLAYBACK_ADJUST_SEEK_POSITION";

}
