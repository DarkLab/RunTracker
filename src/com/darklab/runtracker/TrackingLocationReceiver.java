package com.darklab.runtracker;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class TrackingLocationReceiver extends LocationReceiver {
	
	private static final String TAG = "TrackingLocationReceiver";

	@Override
	protected void onLocationReceived(Context context, Location loc) {
		RunManager.get(context).insertLocation(loc);
		Log.d(TAG,
				this + " Got location from " + loc.getProvider() + ":"
						+ loc.getLatitude() + ", " + loc.getLongitude());
	}
}
