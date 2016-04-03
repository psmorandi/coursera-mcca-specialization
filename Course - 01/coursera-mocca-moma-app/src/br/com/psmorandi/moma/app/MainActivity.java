package br.com.psmorandi.moma.app;

import java.util.Random;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends FragmentActivity {

	private SeekBar colorSeekBar = null;
	private LinearLayout leftLayoutOne;
	private LinearLayout leftLayoutTwo;
	private LinearLayout leftLayoutThree;
	private LinearLayout rightLayout;
	private int leftLayoutOneInitColor;
	private int leftLayoutTwoInitColor;
	private int leftLayoutThreeInitColor;
	private int rightLayoutInitColor;
	private int finalLayoutBackgroundColor;
	Random random = new Random();

	private static final String MOMA_URL = "http://www.moma.org";
	static private final String CHOOSER_TEXT = "Load " + MOMA_URL + " with:";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// this is the final background color for all layouts. The progress bar
		// will help to change from initial background color to this final
		// color.
		finalLayoutBackgroundColor = getRandomColorFromResources();

		// There are three left layouts...
		leftLayoutOne = (LinearLayout) findViewById(R.id.left_one);
		leftLayoutOneInitColor = getRandomColor();
		leftLayoutOne.setBackgroundColor(leftLayoutOneInitColor);

		leftLayoutTwo = (LinearLayout) findViewById(R.id.left_two);
		leftLayoutTwoInitColor = getRandomColor();
		leftLayoutTwo.setBackgroundColor(leftLayoutTwoInitColor);

		leftLayoutThree = (LinearLayout) findViewById(R.id.left_three);
		leftLayoutThreeInitColor = getRandomColor();
		leftLayoutThree.setBackgroundColor(leftLayoutThreeInitColor);

		// ..and one non-gray to the right
		rightLayout = (LinearLayout) findViewById(R.id.right_one);
		rightLayoutInitColor = getRandomColor();
		rightLayout.setBackgroundColor(rightLayoutInitColor);

		colorSeekBar = (SeekBar) findViewById(R.id.colorSeekBar);

		colorSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				changeLayoutBackgroundColor(leftLayoutOne, leftLayoutOneInitColor,
						finalLayoutBackgroundColor, progress);
				changeLayoutBackgroundColor(leftLayoutTwo, leftLayoutTwoInitColor,
						finalLayoutBackgroundColor, progress);
				changeLayoutBackgroundColor(leftLayoutThree, leftLayoutThreeInitColor,
						finalLayoutBackgroundColor, progress);
				changeLayoutBackgroundColor(rightLayout, rightLayoutInitColor,
						finalLayoutBackgroundColor, progress);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// nothing to do here
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// nothing to do here
			}
		});
	}

	// Gets a random color from the array of colors in the resources
	private int getRandomColorFromResources() {
		int[] raibowColors = getResources().getIntArray(R.array.rainbow);
		int index = random.nextInt(raibowColors.length);
		return raibowColors[index];
	}

	private int getRandomColor() {
		return Color.rgb(random.nextInt(256), random.nextInt(256),
				random.nextInt(256));
	}

	private void changeLayoutBackgroundColor(LinearLayout layout, int startColor,
			int finalColor, int progress) {

		// I made this algorithm based on the week 4 discussion forum
		// https://class.coursera.org/android-002/forum/thread?thread_id=1785

		int redStart = Color.red(startColor);
		int greenStart = Color.green(startColor);
		int blueStart = Color.blue(startColor);

		int redFinal = Color.red(finalColor);
		int greenFinal = Color.green(finalColor);
		int blueFinal = Color.blue(finalColor);

		int newRed = calculateNewColor(redStart, redFinal, progress);
		int newGreen = calculateNewColor(greenStart, greenFinal, progress);
		int newBlue = calculateNewColor(blueStart, blueFinal, progress);

		layout.setBackgroundColor(Color.rgb(newRed, newGreen, newBlue));
	}

	private int calculateNewColor(int start, int end, int progress) {
		int delta = end - start;
		return start + (delta * progress / 100);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_more_info) {
			DialogFragment momaWebsiteDialog = new MoreInformationFragmentDialog(
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							showMomaWebSite();
						}
					});
			momaWebsiteDialog.show(getSupportFragmentManager(), "MOMA_WEBSITE");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showMomaWebSite() {
		Intent baseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MOMA_URL));
		Intent chooserIntent = Intent.createChooser(baseIntent, CHOOSER_TEXT);
		startActivity(chooserIntent);
	}
}
