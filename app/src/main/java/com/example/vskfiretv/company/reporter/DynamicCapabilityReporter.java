package com.example.vskfiretv.company.reporter;

import android.content.Context;
import android.util.Log;

import com.amazon.alexa.vsk_app_agent_api.AddOrUpdateCapabilitiesRequest;
import com.amazon.alexa.vsk_app_agent_api.AlexaCapability;
import com.amazon.alexa.vsk_app_agent_client_lib.VSKAgentClient;
import com.example.vskfiretv.company.R;
import com.example.vskfiretv.company.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This is used for reporting app's dynamic capabilities to the VSK agent
 */
public class DynamicCapabilityReporter {
    private static final String TAG = DynamicCapabilityReporter.class.getSimpleName();
    private final Context context;
    private final VSKAgentClient client;

    public DynamicCapabilityReporter(final Context context) {
        this.context = context;
        // Create an instance of VSKAgentClient
        client = new VSKAgentClient(context);
    }

    /**
     * Reports Dynamic Capabilities to the VSK Agent
     */
    public void reportDynamicCapabilities() {
        // Create a list of supported capabilities in your app.
        final List<AlexaCapability> supportedCapabilities = new ArrayList<>();
        supportedCapabilities.add(getAlexaCapability(R.raw.remote_video_player_capability)); // RemoteVideoPlayer Capability
        supportedCapabilities.add(getAlexaCapability(R.raw.play_back_controller_capability)); // PlaybackController Capability
        supportedCapabilities.add(getAlexaCapability(R.raw.channel_controller_capability)); // ChannelController Capability
        supportedCapabilities.add(getAlexaCapability(R.raw.seek_controller_capability)); // SeekController Capability
        supportedCapabilities.add(getAlexaCapability(R.raw.keypad_controller_capability)); // KeypadController Capability
        supportedCapabilities.add(getAlexaCapability(R.raw.alexa_launcher_capability)); // Alexa.Launcher Capability

        // Make the capability reporting request
        final AddOrUpdateCapabilitiesRequest request = new AddOrUpdateCapabilitiesRequest(supportedCapabilities);

        // Report dynamic capabilities to the VSK agent
        Log.i(TAG, "Reporting dynamic capabilities to the VSK agent...");
        final boolean reportingStatus = client.addOrUpdateCapabilities(request);

        if(reportingStatus) {
            Log.i(TAG, "Successfully reported dynamic capabilities to the VSK agent");
            return;
        }
        Log.e(TAG, "Failed reporting dynamic capabilities to the VSK agent");
    }

    /**
     * This can be used to request a Test directive from the VSK Agent. Call this method when you want to test a sample directive.
     */
    public void requestTestDirectiveFromVSKAgent() {
        Log.i(TAG, "Requesting VSK Agent for a test directive to be sent to the app");
        client.sendCapabilityTestDirective();
        Log.i(TAG, "Test Directive Request completed");
    }

    /**
     * Returns an {@link AlexaCapability} based on the capability's resourceId declared in the resource folder
     * @param resourceId the uniqueId of the capability resource
     * @return {@link AlexaCapability}
     */
    public AlexaCapability getAlexaCapability(final int resourceId) {
        final String capabilityJson = ResourceUtils.getRawTextResource(context, resourceId);
        return new AlexaCapability(capabilityJson);
    }
}
