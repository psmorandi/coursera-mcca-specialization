package br.com.psmorandi.cousera.dailyselfie;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class SelfieViewActivity extends Activity {

	public static final String FILE_LOCATION_EXTRAS = "cousera.selfie.FILE_LOCATION";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent startIntent = getIntent();
		if (startIntent == null) {
			Toast.makeText(
					getApplicationContext(),
					"Selfie view activity needs an intent with the photo location.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		String fileLocation = startIntent.getStringExtra(FILE_LOCATION_EXTRAS);

		setContentView(R.layout.selfie_view);
		ImageView selfieImageView = (ImageView) findViewById(R.id.selfie_imageView);
		selfieImageView.setImageURI(Uri.parse(fileLocation));
	}

}
