package com.qiniu.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qiniu.R;
import com.qiniu.auth.token.URLUtils;

import com.qiniu.auth.JSONObjectRet;

import com.qiniu.auth.token.GetPolicy;

import com.qiniu.auth.token.Mac;

import com.qiniu.conf.Conf;
import com.qiniu.io.IO;
import com.qiniu.io.PutExtra;


import org.json.JSONObject;

import java.io.File;

import java.util.HashMap;

public class MyActivity extends Activity implements View.OnClickListener{

	public static final int PICK_PICTURE_RESUMABLE = 0;
	public static final int PICK_PICTURE_CAMERA =1;
	private static final int UPLOAD_PICTURE =1;
    private static final int DOWNLOAD_LINK =2 ;
	// @gist upload_arg
	// 在七牛绑定的对应bucket的域名. 默认是bucket.qiniudn.com
	public static String bucketName = "testappdemo";
	public static String domain = bucketName + ".qiniudn.com";
	// upToken 这里需要自行获取. SDK 将不实现获取过程. 当token过期后才再获取一遍

	public String uptoken =Conf.getToken();
    private String picturePath; 
	// @endgist

	private Button btnUpload;
	private Button btnTakePic;
	private Button btnDownload;

	
	private TextView hint;
	String mCameraFileName;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mCameraFileName = savedInstanceState.getString("mCameraFileName");
		}
		setContentView(R.layout.main);
	
		
		initWidget();
	}

	/**
	 * 初始化控件
	 */
	private void initWidget() {
		hint = (TextView) findViewById(R.id.textView1);
		btnUpload = (Button) findViewById(R.id.button1);
		btnTakePic = (Button) findViewById(R.id.button2);
		btnDownload = (Button)findViewById(R.id.button3);
		btnUpload.setOnClickListener(this);
		btnTakePic.setOnClickListener(this);
		btnDownload.setOnClickListener(this);
	
	}

	// @gist upload
	boolean uploading = false;
	/**
	 * 普通上传文件
	 * @param uri
	 */
	private void doUpload(Uri uri) {
		if (uploading) {
			hint.setText("上传中，请稍后");
			return;
		}
		
		uploading = true;
		String key = IO.UNDEFINED_KEY; // 自动生成key
		PutExtra extra = new PutExtra();
		extra.params = new HashMap<String, String>();
		extra.params.put("x:a", "测试中文信息");
		hint.setText("上传中");
	    String[] filePathColumn = { MediaStore.Images.Media.DATA };
	    Cursor cursor = getContentResolver().query(uri,
	    filePathColumn, null, null, null);
	    cursor.moveToFirst();
	    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	    picturePath = cursor.getString(columnIndex);
	    cursor.close();
	    
	    if(picturePath!=null&&picturePath.length()>0)
	    {
	    	Log.d("Test String",picturePath);
	    	 showToast(picturePath);
	    	key = picturePath.substring(picturePath.lastIndexOf("/")+1);
	    	  Log.d("Test key String",key);
	    }
	   
		/*end*/
		IO.putFile(this, uptoken, key, uri, extra, new JSONObjectRet() {
			@Override
			public void onProcess(long current, long total) {
				hint.setText(current + "/" + total);
			}

			@Override
			public void onSuccess(JSONObject resp) {
				uploading = false;
				String hash = resp.optString("hash", "");				
				hint.setText("上传成功! " + hash);
		
			}

			@Override
			public void onFailure(Exception ex) {
				uploading = false;
				hint.setText("错误: " + ex.getMessage());
			}
		});
	}
	private void doUpload(File pic) {
		if (uploading) {
			hint.setText("上传中，请稍后");
			return;
		}
		
		uploading = true;
		String key =pic.getName(); // 自动生成key
		PutExtra extra = new PutExtra();
		extra.params = new HashMap<String, String>();
		extra.params.put("x:a", "测试中文信息");
		hint.setText("上传中");
		IO.putFile(uptoken, key, pic, extra, new JSONObjectRet() {
			@Override
			public void onProcess(long current, long total) {
				hint.setText(current + "/" + total);
			}

			@Override
			public void onSuccess(JSONObject resp) {
				uploading = false;
				String hash = resp.optString("hash", "");
				/*String value = resp.optString("x:a", "");
				String redirect = "http://" + domain + "/" + hash;*/
				hint.setText("上传成功! " + hash);
			/*	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect));
				startActivity(intent);*/
			}

			@Override
			public void onFailure(Exception ex) {
				uploading = false;
				hint.setText("错误: " + ex.getMessage());
			}
		});
	}
	private void getDownloadLink(String context)
	{
		String downloadUrl;
		String baseUrl;
		try{
		 Mac mac = new Mac(Conf.ACCESS_KEY, Conf.SECRET_KEY);
	     
	     baseUrl = URLUtils.makeBaseUrl(domain, context);
	     GetPolicy getPolicy = new GetPolicy();
	     downloadUrl = getPolicy.makeRequest(baseUrl, mac);
		}catch(Exception e)
		{
			return;
		}
		
	    showToast(downloadUrl);
	    Log.d("Link",downloadUrl);
	    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl));
		startActivity(intent);
	}
	// @endgist
	private void showToast(String msg) {
		Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		error.show();
	}
	private void showInputDialog(String title, String[] hint, final int type) {
		Log.d("upload pic","shouinputdialog");
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.input_dialog, null);
		final EditText edt = (EditText) layout.findViewById(R.id.et_input);
		final EditText edt2 = (EditText) layout.findViewById(R.id.et_input2);

		

	

		if (hint != null) {
			edt.setHint(hint[0]);
			if (hint.length == 2) {
				edt2.setHint(hint[1]);
			}
		}

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);
		builder.setTitle(title);
		if (layout != null)
			builder.setView(layout);
		builder.setPositiveButton(getString(R.string.be_sure),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						String content = edt.getText().toString();
						//String content2 = edt2.getText().toString();
						switch (type) {
						case UPLOAD_PICTURE:
							// 拍照上传
							// Take a photo and upload
							if (TextUtils.isEmpty(content)) {
								showToast(getString(R.string.input_cannot_enmpty)
										+ "！");
							} else {
								Log.d("upload pic","uploadPicture");
								uploadPicture(content + ".jpg");
							}
							break;

						
						
						case DOWNLOAD_LINK:
							// 根据拷贝引用获取下载链接
							// Get download link by copy reference
							if (TextUtils.isEmpty(content)) {
								showToast(getString(R.string.input_cannot_enmpty)
										+ "！");
							} else {
								getDownloadLink(content);
							}
							break;
					
					
						default:
							break;
						}
					}
				});
		builder.setNegativeButton(getString(R.string.be_cancel), null);
		final AlertDialog alert = builder.create();
		alert.show();
	}
	public File createDirFile(String path) {
		int pos = path.lastIndexOf("/");
		String dirpath = path.substring(0, pos + 1);
		if (!dirpath.startsWith("/"))
			dirpath = "/" + dirpath;
		File f = new File(dirpath);
		if (!f.exists())
			f.mkdirs();
		return new File(path);
	}
	private void uploadPicture(String name) {

		Intent intent = new Intent(); // Picture from camera
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

		// This is not the right way to do this, but for some reason, having it
		// store it in MediaStore.Images.Media.EXTERNAL_CONTENT_URI isn't
		// working right.

		String outPath = Environment.getExternalStorageDirectory()
				.getAbsoluteFile() + "/qiniu_SDK_cache/" + name;

		File outFile = createDirFile(outPath);

		mCameraFileName = outFile.toString();
		Uri outuri = Uri.fromFile(outFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
		
		try {
			startActivityForResult(intent, PICK_PICTURE_CAMERA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, getString(R.string.find_no_camera),
					Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("mCameraFileName", mCameraFileName);
		super.onSaveInstanceState(outState);
	}
	@Override
	public void onClick(View view) {
		if (view.equals(btnUpload)) {
			Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, PICK_PICTURE_RESUMABLE);
			return;
		}
		if(view.equals(btnDownload)){
			Log.d("btnDownload","btn click");
			showInputDialog(getString(R.string.get_download_link),
			new String[] { getString(R.string.please_input_down_file_path) },
			2);
			return;
		}
		if(view.equals(btnTakePic)){
			Log.d("upload pic","btn click");
			showInputDialog(
					getString(R.string.take_photo_upload),
					new String[] { getString(R.string.please_input_pic_name) },
					1);
			return;

			// This is not the right way to do this, but for some reason, having it
			// store it in MediaStore.Images.Media.EXTERNAL_CONTENT_URI isn't
			// working right.

			
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;
		if (requestCode == PICK_PICTURE_RESUMABLE) {
			doUpload(data.getData());
			return;
		}
		if(requestCode == PICK_PICTURE_CAMERA){
			Log.d("upload pic","testOnresult");
			File pic = new File(mCameraFileName);
			doUpload(pic);
			return;
		}
	}
}
