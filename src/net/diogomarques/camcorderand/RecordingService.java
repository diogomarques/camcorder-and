package net.diogomarques.camcorderand;

import java.io.File;
import java.util.Date;

import net.diogomarques.camcorderandand.R;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

/**
 * Background service that records video.
 * 
 * @author cman
 *         (http://stackoverflow.com/questions/15049041/background-video-recording
 *         -in-android-4-0)
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 * 
 */
// TODO: select front or rear camera according to preferences.
// TODO: handle max video size from preferences; includes notification to user.
// TODO: handle recording quality from preferences
// TODO: advertise recording state through prefs.

public class RecordingService extends Service implements SurfaceHolder.Callback {

	public static enum Orientation {
		PORTRAIT, LANDSCAPE, AUTO
	}

	private static final String BACKGROUND_VIDEOS_FOLDER = "BackgroundVideos";
	private WindowManager mWindowManager;
	private SurfaceView mSurfaceView;
	private Camera mCamera = null;
	private MediaRecorder mMediaRecorder = null;

	private static final int NOTIFICATION_ID = 981532;

	// Default prefs - ft camera w/ high quality, no maximum size
	private boolean mPrefUseFTCamera = true;
	private boolean mPrefHQRecording = true;
	private int mPrefMaxSize = -1;
	private Orientation mPrefOrientation = Orientation.PORTRAIT;

	public RecordingService() {
	}

	// TODO: add notifications in pre JB or target > JB
	// TODO: Open MainActivity on notification click.
	// Min JB for this notification syntax
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onCreate() {

		// Start foreground service to avoid unexpected kill
		Notification notification = new Notification.Builder(this)
				.setContentTitle("Background recording")
				.setContentText(
						(mPrefUseFTCamera ? "front" : "back") + " camera; "
								+ (mPrefHQRecording ? "high" : "low")
								+ " quality.")
				.setSmallIcon(R.drawable.ic_notif_recording).build();
		startForeground(NOTIFICATION_ID, notification);

		// Create new SurfaceView, set its size to 1x1, move it to the top left
		// corner and set this service as a callback
		mWindowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		mSurfaceView = new SurfaceView(this);
		LayoutParams layoutParams = new WindowManager.LayoutParams(1, 1,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);
		layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		mWindowManager.addView(mSurfaceView, layoutParams);
		mSurfaceView.getHolder().addCallback(this);

	}

	// Method called right after Surface created (initializing and starting
	// MediaRecorder)
	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {

		// Select front of back facing camera
		mCamera = Camera.open(mPrefUseFTCamera ? CameraInfo.CAMERA_FACING_FRONT
				: CameraInfo.CAMERA_FACING_BACK);
		mMediaRecorder = new MediaRecorder();
		mCamera.unlock();

		// TODO: check if this is always the desired behavior
		// Rotate camera
		// setCameraOrientation();
		if (mPrefUseFTCamera)
			mMediaRecorder.setOrientationHint(270);

		// Magic here:
		mMediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
		mMediaRecorder.setCamera(mCamera);
		// Use the an audio source with the same orientation than the camerA
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		// Select profile according to camera / quality
		mMediaRecorder.setProfile(CamcorderProfile.get(
				(mPrefUseFTCamera ? CameraInfo.CAMERA_FACING_FRONT
						: CameraInfo.CAMERA_FACING_BACK),
				(mPrefHQRecording ? CamcorderProfile.QUALITY_HIGH
						: CamcorderProfile.QUALITY_LOW)));

		mMediaRecorder.setOutputFile(getOutputFilePath());
		try {
			mMediaRecorder.prepare();
		} catch (Exception e) {
		}
		mMediaRecorder.start();

	}

	private String getOutputFilePath() {
		File file = new File(
				Environment.getExternalStorageDirectory()
						+ "/"
						+ BACKGROUND_VIDEOS_FOLDER
						+ "/"
						+ DateFormat.format("yyyy-MM-dd_kk-mm-ss",
								new Date().getTime()) + ".mp4");
		return file.getPath();
	}

	private void setCameraOrientation() {
		switch (mPrefOrientation) {
		case LANDSCAPE:
			mCamera.setDisplayOrientation(0);
			break;

		case PORTRAIT:
			mCamera.setDisplayOrientation(90);
			break;
		case AUTO:
			int rotation = mWindowManager.getDefaultDisplay().getRotation();

			switch (rotation) {
			case Surface.ROTATION_0:
				mCamera.setDisplayOrientation(0);
				break;
			case Surface.ROTATION_90:
				mCamera.setDisplayOrientation(90);
				break;
			case Surface.ROTATION_180:
				mCamera.setDisplayOrientation(0);
				break;
			case Surface.ROTATION_270:
				mCamera.setDisplayOrientation(90);
				break;
			}
			break;
		}

	}

	// Stop recording and remove SurfaceView
	@Override
	public void onDestroy() {

		mMediaRecorder.stop();
		mMediaRecorder.reset();
		mMediaRecorder.release();

		mCamera.lock();
		mCamera.release();

		mWindowManager.removeView(mSurfaceView);

	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
			int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
