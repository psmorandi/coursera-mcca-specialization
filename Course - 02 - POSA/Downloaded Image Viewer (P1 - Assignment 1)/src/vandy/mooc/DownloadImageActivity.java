package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

/**
 * An Activity that downloads an image, stores it in a local file on the local
 * device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends Activity {
	/**
	 * Debugging tag used by the Android logger.
	 */
	private final String TAG = getClass().getSimpleName();

	private Handler mHandler = new Handler();

	/**
	 * Hook method called when a new instance of Activity is created. One time
	 * initialization code goes here, e.g., UI layout and some class scope
	 * variable initialization.
	 * 
	 * @param savedInstanceState
	 *            object that contains saved state information.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Always call super class for necessary
		// initialization/implementation.
		// @@ TODO -- you fill in here.
		super.onCreate(savedInstanceState);

		// Get the URL associated with the Intent data.
		// @@ TODO -- you fill in here.
		Intent intent = getIntent();
		if (intent == null) {
			finish();
		}

		final Uri url = intent.getData();

		// Download the image in the background, create an Intent that
		// contains the path to the image file, and set this as the
		// result of the Activity.

		// @@ TODO -- you fill in here using the Android "HaMeR"
		// concurrency framework. Note that the finish() method
		// should be called in the UI thread, whereas the other
		// methods should be called in the background thread.
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final Uri downloadedFileUri = DownloadUtils.downloadImage(
						DownloadImageActivity.this, url);
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						//if something goes wrong...
						if (downloadedFileUri == null) {
							//..tell the activity who called this activity that download was canceled
							setResult(RESULT_CANCELED);
						} else {
							//Everything worked..
							Intent resultIntent = new Intent();
							resultIntent.setData(downloadedFileUri);
							// set the result
							setResult(RESULT_OK, resultIntent);
						}
						// finishs application
						finish();
					}
				});
			}
		}).start();
	}
}
