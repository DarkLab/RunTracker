package com.darklab.runtracker;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class RunDatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "runs.sqlite";
	private static final int VERSION = 1;

	private static final String TABLE_RUN = "run";
	private static final String COLOUMN_RUN_ID = "_id";
	private static final String COLOUMN_RUN_START_DATE = "start_date";

	private static final String TABLE_LOCATION = "location";
	private static final String COLOUMN_LOCATION_LATITUDE = "latitude";
	private static final String COLOUMN_LOCATION_LONGITUDE = "longitude";
	private static final String COLOUMN_LOCATION_ALTITUDE = "altitude";
	private static final String COLOUMN_LOCATION_TIMESTAMP = "timestamp";
	private static final String COLOUMN_LOCATION_PROVIDER = "provider";
	private static final String COLOUMN_LOCATION_RUN_ID = "run_id";

	public RunDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table run ("
				+ "_id integer primary key autoincrement, start_date integer)");
		db.execSQL("create table location ("
				+ "timestamp integer, latitude real, longitude real, altitude real,"
				+ "provider varchar(100), run_id integer references run(_id))");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public long insertRun(Run run) {
		ContentValues cv = new ContentValues();
		cv.put(COLOUMN_RUN_START_DATE, run.getStartDate().getTime());
		return getWritableDatabase().insert(TABLE_RUN, null, cv);
	}

	public long insertLocation(long runId, Location location) {
		ContentValues cv = new ContentValues();
		cv.put(COLOUMN_LOCATION_LATITUDE, location.getLatitude());
		cv.put(COLOUMN_LOCATION_LONGITUDE, location.getLongitude());
		cv.put(COLOUMN_LOCATION_ALTITUDE, location.getAltitude());
		cv.put(COLOUMN_LOCATION_TIMESTAMP, location.getTime());
		cv.put(COLOUMN_LOCATION_PROVIDER, location.getProvider());
		cv.put(COLOUMN_LOCATION_RUN_ID, runId);
		return getWritableDatabase().insert(TABLE_LOCATION, null, cv);
	}

	public RunCursor queryRuns() {
		Cursor wrapped = getReadableDatabase().query(TABLE_RUN, null, null,
				null, null, null, COLOUMN_RUN_START_DATE + " asc");
		return new RunCursor(wrapped);
	}

	public RunCursor queryRun(long id) {
		Cursor wrapped = getReadableDatabase().query(
				TABLE_RUN, 
				null,
				COLOUMN_RUN_ID + " = ?", 
				new String[] { String.valueOf(id) },
				null, 
				null, 
				null, 
				"1");
		return new RunCursor(wrapped);
	}

	public class RunCursor extends CursorWrapper {

		public RunCursor(Cursor cursor) {
			super(cursor);
		}

		public Run getRun() {
			if (isBeforeFirst() || isAfterLast()) {
				return null;
			}
			Run run = new Run();
			long runId = getLong(getColumnIndex(COLOUMN_RUN_ID));
			run.setId(runId);
			long startDate = getLong(getColumnIndex(COLOUMN_RUN_START_DATE));
			run.setStartDate(new Date(startDate));
			return run;
		}

	}

}
