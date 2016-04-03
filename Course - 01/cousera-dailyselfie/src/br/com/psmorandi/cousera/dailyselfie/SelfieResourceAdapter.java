package br.com.psmorandi.cousera.dailyselfie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.psmorandi.cousera.dailyselfie.data.SelfieDataContract;

public class SelfieResourceAdapter extends ResourceCursorAdapter {

	private static final int PREVIEW_IMAGE_WIDTH = 100;
	private static final int PREVIEW_IMAGE_HEIGHT = 100;

	public SelfieResourceAdapter(Context context, int layout, Cursor cursor) {
		super(context, layout, cursor, 0);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// search for the text view that will hold the image
		TextView textView = (TextView) view.findViewById(R.id.image_text);
		
		ImageView imageView = (ImageView)view.findViewById(R.id.image_preview);

		// gets the image name from cursor
		textView.setText(cursor.getString(cursor
				.getColumnIndex(SelfieDataContract.IMAGE_NAME_COLLUMN)));

		final String fileLocation = cursor.getString(cursor
				.getColumnIndex(SelfieDataContract.FILE_LOCATION_COLLUMN));

		// when the user clicks on the text view, we show the activity to display
		// the full image.
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SelfieViewActivity.class);
				intent.putExtra(SelfieViewActivity.FILE_LOCATION_EXTRAS,
						fileLocation);
				mContext.startActivity(intent);
			}
		});

		// gets the preview image in a memory efficient way
		Bitmap selfiePreview = getScaledBitmap(fileLocation);
		imageView.setImageBitmap(selfiePreview);
	}

	private Bitmap getScaledBitmap(String photoPath) {
		// Get the dimensions of the View
		int targetW = PREVIEW_IMAGE_WIDTH;
		int targetH = PREVIEW_IMAGE_HEIGHT;

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(photoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;

		return BitmapFactory.decodeFile(photoPath, bmOptions);
	}
}
