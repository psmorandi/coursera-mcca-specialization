package br.com.psmorandi.cousera.dailyselfie;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import br.com.psmorandi.cousera.dailyselfie.data.SelfieDataContract;

public class MainActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final int REQUEST_IMAGE_CAPTURE = 1;

	private static final int SELFIE_ALARM_REQUEST_CODE = 10002;

	private static final String DAILY_SELFIE_DIR = "DailySelfie";
	
	private static final int SELFIE_ALARM_INTERVAL_MIN = 2;

	private String mCurrentPhotoPath;
	private String mCurrentPhotoName;
	private SelfieResourceAdapter mSelfieResourceAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(getApplicationContext(),
					"External Storage is not available.", Toast.LENGTH_LONG)
					.show();
			finish();
		}

		mSelfieResourceAdapter = new SelfieResourceAdapter(this,
				R.layout.list_item, null);
		setListAdapter(mSelfieResourceAdapter);

		getLoaderManager().initLoader(0, null, this);

		startSelfieTimer();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.take_picture) {
			dispatchTakePictureIntent();
			return true;
		}

		if (id == R.id.delete_all) {
			deleteAllSelfies();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void deleteAllSelfies() {
		ContentResolver contentResolver = getContentResolver();
		int selfiesDeleted = contentResolver.delete(
				SelfieDataContract.CONTENT_URI, null, null);

		Toast.makeText(getApplicationContext(),
				String.format("Deleted %d selfies.", selfiesDeleted),
				Toast.LENGTH_LONG).show();
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
				ex.printStackTrace();
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			saveImageInContentProvider(mCurrentPhotoName, mCurrentPhotoPath);
		}
	}

	private void saveImageInContentProvider(String photoName, String photoPath) {
		ContentValues values = new ContentValues();

		values.put(SelfieDataContract.IMAGE_NAME_COLLUMN, photoName);
		values.put(SelfieDataContract.FILE_LOCATION_COLLUMN, photoPath);

		ContentResolver contentResolver = getContentResolver();
		contentResolver.insert(SelfieDataContract.CONTENT_URI, values);
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		mCurrentPhotoName = "SELFIE_" + timeStamp;
		File externalPicDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File storageDir = new File(externalPicDir.getAbsolutePath() + "/"
				+ DAILY_SELFIE_DIR + "/");
		storageDir.mkdirs();
		File image = File.createTempFile(mCurrentPhotoName, ".jpg", storageDir);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String columnsToExtract[] = new String[] { SelfieDataContract._ID,
				SelfieDataContract.IMAGE_NAME_COLLUMN,
				SelfieDataContract.FILE_LOCATION_COLLUMN };

		// sort by increasing ID
		String sortOrder = SelfieDataContract._ID + " ASC";

		return new CursorLoader(this, SelfieDataContract.CONTENT_URI,
				columnsToExtract, null, null, sortOrder);
	}

	@Override
	public void onLoadFinished(android.content.Loader<Cursor> loader,
			Cursor data) {
		mSelfieResourceAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(android.content.Loader<Cursor> loader) {
		mSelfieResourceAdapter.swapCursor(null);
	}

	private void startSelfieTimer() {
		AlarmManager alarmManager = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(this, SelfieAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
				SELFIE_ALARM_REQUEST_CODE, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		long twoMinutesInMillis = TimeUnit.MINUTES.toMillis(SELFIE_ALARM_INTERVAL_MIN);

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + twoMinutesInMillis,
				twoMinutesInMillis, pendingIntent);
	}
}
