package br.com.psmorandi.cousera.dailyselfie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;

	private static final String CREATE_SELFIES_TABLE = "CREATE TABLE "
			+ SelfieDataContract.SELFIE_TABLE_NAME + " (" + SelfieDataContract._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ SelfieDataContract.IMAGE_NAME_COLLUMN + " TEXT NOT NULL, "
			+ SelfieDataContract.FILE_LOCATION_COLLUMN + " TEXT NOT NULL " + "); ";

	public DatabaseOpenHelper(Context context) {
		super(context, SelfieDataContract.SELFIE_TABLE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SELFIES_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "
				+ SelfieDataContract.SELFIE_TABLE_NAME);
		onCreate(db);
	}
}
