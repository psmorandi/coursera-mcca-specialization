package br.com.psmorandi.cousera.dailyselfie.data;

import java.io.File;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class SelfieContentProvider extends ContentProvider {
	
	private static final String TAG = "CONTENT_PROVIDER";

	private DatabaseOpenHelper mDbHelper;

	@Override
	public boolean onCreate() {
		mDbHelper = new DatabaseOpenHelper(getContext());
		return (mDbHelper != null);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(SelfieDataContract.SELFIE_TABLE_NAME);

		Cursor cursor = qb.query(mDbHelper.getWritableDatabase(), projection,
				selection, selectionArgs, null, null, sortOrder);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// not implemented
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = mDbHelper.getWritableDatabase().insert(
				SelfieDataContract.SELFIE_TABLE_NAME, "", values);
		if (rowID > 0) {
			Uri fullUri = ContentUris.withAppendedId(
					SelfieDataContract.CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(fullUri, null);
			return fullUri;
		}
		throw new SQLException("Failed to add record into" + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		SQLiteDatabase sqliteDatabase = mDbHelper.getWritableDatabase();

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(SelfieDataContract.SELFIE_TABLE_NAME);

		Cursor cursor = qb.query(sqliteDatabase, new String[] {
			SelfieDataContract._ID,
			SelfieDataContract.FILE_LOCATION_COLLUMN },
				selection, selectionArgs, null, null, null);
		
		while(cursor.moveToNext()){

			String fileLocation = cursor.getString(cursor
					.getColumnIndex(SelfieDataContract.FILE_LOCATION_COLLUMN));
			
			File selfiePhoto = new File(fileLocation);
			if(selfiePhoto.exists()){
				Log.d(TAG, "Deleting selfie: " + fileLocation);
				selfiePhoto.delete();
			}
		}

		cursor.close();

		int rowsDeleted = sqliteDatabase.delete(
				SelfieDataContract.SELFIE_TABLE_NAME, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(
				SelfieDataContract.CONTENT_URI, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// not implemented
		return 0;
	}

}
