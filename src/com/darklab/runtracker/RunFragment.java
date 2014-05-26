package com.darklab.runtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.darklab.runtracker.R.id;

public class RunFragment extends Fragment {
	private static final String TAG = "RunFragment";
	private static final String ARG_RUN_ID = "RUN_ID";
	
	private Run mRun;
	private Location mLastLocation;

	private BroadcastReceiver mLocationReceiver = new LocationReceiver() {

		@Override
		protected void onLocationReceived(Context context, Location loc) {
			if (!mRunManager.isTrackingRun()) {
				return;
			}
			mLastLocation = loc;
			if (isVisible()) {
				updateUI();
			}
		}

		@Override
		protected void onProviderEnabledChanged(boolean enabled) {
			int toastText = enabled ? R.string.gps_enabled
					: R.string.gps_disabled;
			Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
		}

	};

	private RunManager mRunManager;

	private Button mStartButton, mStopButton;
	private TextView mStartedTextView, mLatitudeTextView, mLongitudeTextView,
			mAltitudeTextView, mDurationTextView;
	
	public static RunFragment newInstance(long runId){
		Bundle args = new Bundle();
		args.putLong(ARG_RUN_ID, runId);
		RunFragment rf = new RunFragment();
		rf.setArguments(args);
		return rf;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mRunManager = RunManager.get(getActivity());
		
		Bundle args = getArguments();
		if (args != null) {
			long runId = args.getLong(ARG_RUN_ID, -1);
			if (runId != -1) {
				mRun = mRunManager.getRun(runId);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_run, container, false);
		mStartedTextView = (TextView) view.findViewById(id.run_startedTextView);
		mLatitudeTextView = (TextView) view
				.findViewById(id.run_latitudeTextView);
		mLongitudeTextView = (TextView) view
				.findViewById(id.run_longitudeTextView);
		mAltitudeTextView = (TextView) view
				.findViewById(id.run_altitudeTextView);
		mDurationTextView = (TextView) view
				.findViewById(id.run_durationTextView);

		mStartButton = (Button) view.findViewById(id.run_startButton);
		mStartButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mRun == null) {
					mRun = mRunManager.startNewRun();
				} else {
					mRunManager.startTrackingRun(mRun);
				}
				
				updateUI();

			}
		});

		mStopButton = (Button) view.findViewById(id.run_stopButton);
		mStopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mRunManager.stopRun();
				updateUI();

			}
		});

		updateUI();

		return view;
	}

	private void updateUI() {
		boolean started = mRunManager.isTrackingRun();
		boolean trackingThisRun = mRunManager.isTrackingRun();

		if (mRun != null) {
			mStartedTextView.setText(mRun.getStartDate().toString());
		}

		int durationSeconds = 0;
		if (mRun != null && mLastLocation != null) {
			durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
			mLatitudeTextView.setText(Double.toString(mLastLocation
					.getLatitude()));
			mLongitudeTextView.setText(Double.toString(mLastLocation
					.getLongitude()));
			mAltitudeTextView.setText(Double.toString(mLastLocation
					.getAltitude()));
		}
		mDurationTextView.setText(Run.formatDuration(durationSeconds));

		mStartButton.setEnabled(!started);
		mStopButton.setEnabled(started && trackingThisRun);
	}

	@Override
	public void onStart() {
		super.onStart();
		getActivity().registerReceiver(mLocationReceiver,
				new IntentFilter(RunManager.ACTION_LOCATION));
	}

	@Override
	public void onStop() {
		getActivity().unregisterReceiver(mLocationReceiver);
		super.onStop();
	}

}
