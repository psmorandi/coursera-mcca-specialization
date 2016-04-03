package br.com.psmorandi.cousera.dailyselfie.data;

import android.net.Uri;

public class SelfieDataContract {

	public static final String AUTHORITY = "br.com.psmorandi.cousera.provider";
	public static final Uri BASE_URI = Uri
			.parse("content://" + AUTHORITY + "/");

	public static final String SELFIE_TABLE_NAME = "selfies";

	public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI,
			SELFIE_TABLE_NAME);

	public static final String _ID = "_id";
	public static final String IMAGE_NAME_COLLUMN = "imageName";
	public static final String FILE_LOCATION_COLLUMN = "fileLocation";
}
