package com.codepath.apps.tumblrsnap.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.tumblrsnap.PhotosAdapter;
import com.codepath.apps.tumblrsnap.R;
import com.codepath.apps.tumblrsnap.TumblrClient;
import com.codepath.apps.tumblrsnap.activities.PreviewPhotoActivity;
import com.codepath.apps.tumblrsnap.models.Photo;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class PhotosFragment extends Fragment {
	private static final int TAKE_PHOTO_CODE = 1;
	private static final int PICK_PHOTO_CODE = 2;
	private static final int CROP_PHOTO_CODE = 3;
	private static final int POST_PHOTO_CODE = 4;
	
	private Bitmap photoBitmap;
	
	TumblrClient client;
	ArrayList<Photo> photos;
	PhotosAdapter photosAdapter;
	PullToRefreshListView lvPhotos;
	
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_photos, container, false);
		setHasOptionsMenu(true);
		return view;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		client = ((TumblrClient) TumblrClient.getInstance(
				TumblrClient.class, getActivity()));
		photos = new ArrayList<Photo>();
		photosAdapter = new PhotosAdapter(getActivity(), photos);
		lvPhotos = (PullToRefreshListView) getView().findViewById(R.id.lvPhotos);
		 // Set a listener to be invoked when the list should be refreshed.
		lvPhotos.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
               lvPhotos.onRefreshComplete();
            }
        });
		lvPhotos.setAdapter(photosAdapter);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		reloadPhotos();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.photos, menu);
	}
	
	public Uri getPhotoFileUri(String fileName) {
		  File mediaStorageDir = new File(
		        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
		        "MyCameraApp");

		    // Create the storage directory if it does not exist
		    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
		        Log.d("MyCameraApp", "failed to create directory");
		    }
		    // Specify the file target for the photo
		   return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator +
				        fileName));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_take_photo:
			{
				// Take the user to the camera app
				
				// create Intent to take a picture and return control to the calling application
			    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			 // set the image file name
			    intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri("photo.jpg")); 
			    // start the image capture Intent
			    startActivityForResult(intent, TAKE_PHOTO_CODE);
			}
			break;
			case R.id.action_use_existing:
			{
				// Take the user to the gallery app
				Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, PICK_PHOTO_CODE);
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == TAKE_PHOTO_CODE) {
				// Extract the photo that was just taken by the camera
				Uri takenPhotoUri = getPhotoFileUri("photo.jpg");
				// Call the method below to trigger the cropping
				cropPhoto(takenPhotoUri);
			} else if (requestCode == PICK_PHOTO_CODE) {
				// Extract the photo that was just picked from the gallery
				Uri galleryPhotoUri = data.getData();
				// Call the method below to trigger the cropping
				cropPhoto(galleryPhotoUri);
			} else if (requestCode == CROP_PHOTO_CODE) {
				Uri cropPhotoUri = getPhotoFileUri("cropPhoto.jpg");
				try {
					// Get bitmap from Uri
					photoBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), cropPhotoUri);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				startPreviewPhotoActivity();
			} else if (requestCode == POST_PHOTO_CODE) {
				reloadPhotos();
			}
		}
	}
	
	private void reloadPhotos() {
		client.getTaggedPhotos(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int code, JSONObject response) {
				try {
					JSONArray photosJson = response.getJSONArray("response");
					photosAdapter.clear();
					photosAdapter.addAll(Photo.fromJson(photosJson));
					//mergeUserPhotos(); // bring in user photos
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
				Log.d("DEBUG", arg0.toString());
			}
		});
	}
	
	private void cropPhoto(Uri photoUri) {
		//call the standard crop action intent (the user device may not support it)
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		//indicate image type and Uri
		cropIntent.setDataAndType(photoUri, "image/*");
		//set crop properties
		cropIntent.putExtra("crop", "true");
		//indicate aspect of desired crop
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		//indicate output X and Y
		cropIntent.putExtra("outputX", 300);
		cropIntent.putExtra("outputY", 300);
		//retrieve data on return
		//set return-data to "false", you will not receive a Bitmap back from the onActivityResult Intent in-line.
		cropIntent.putExtra("return-data", false);
		//start the activity - set MediaStore.EXTRA_OUTPUT to a Uri (of File scheme only) where you want the Bitmap to be stored.
		cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri("cropPhoto.jpg")); 
		startActivityForResult(cropIntent, CROP_PHOTO_CODE);
	}
	
	
	private void startPreviewPhotoActivity() {
		Intent i = new Intent(getActivity(), PreviewPhotoActivity.class);
        i.putExtra("photo_bitmap", photoBitmap);
        startActivityForResult(i, POST_PHOTO_CODE);
	}
	
}
