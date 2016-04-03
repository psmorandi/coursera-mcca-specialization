package br.com.psmorandi.moma.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class MoreInformationFragmentDialog extends DialogFragment {

	private OnClickListener positiveClick;

	public MoreInformationFragmentDialog(OnClickListener positiveClick) {
		this.positiveClick = positiveClick;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// You should not pass null to root, so I used what they suggested here:
		// http://stackoverflow.com/questions/23846146/what-should-i-pass-for-root-when-inflating-a-layout-to-use-for-a-menuitems-acti
		// Also if you read the documentation of inflate:
		// - root Optional view to be the parent of the generated hierarchy (if
		// attachToRoot is true), or else simply an object that provides a set
		// of LayoutParams values for root of the returned hierarchy (if
		// attachToRoot is false.)
		View view = inflater.inflate(R.layout.more_info_dialog,
				new LinearLayout(getActivity()), false);

		return new AlertDialog.Builder(getActivity())
				.setView(view)

				// User cannot dismiss dialog by hitting back button
				.setCancelable(false)

				// Set up Not Now Button
				.setNegativeButton(R.string.button_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dismiss();
							}
						})

				// Set up Visit Moma Button
				.setPositiveButton(R.string.button_visit,
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									int id) {
								if (positiveClick != null)
									positiveClick.onClick(dialog, id);
								dismiss();
							}
						}).create();
	}
}