package com.emergencycall.android;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	Button Setinfo;
	Button LocationButton;
	GPSTracker gps;
	String Comp;
	EditText editphoneNo1;
	EditText editphoneNo2;
	EditText editMessage;
	EditText editEmail;
	SharedPreferences sharedpreferences;

	String phone;
	String email;
	String sms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//EDITTEXT'S
		editEmail = (EditText) findViewById(R.id.editTextEmail);
		editMessage = (EditText)findViewById(R.id.editTextSMS);
		editphoneNo2 = (EditText)findViewById(R.id.editTextPhoneNo2);
		editphoneNo1 = (EditText)findViewById(R.id.editTextPhoneNo1);
		//BUTTON
		LocationButton = (Button) findViewById(R.id.LocationButton);

		LocationButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View m) {
				// create class object
				GetLocation();
				SendEmail();
				SendSMS();
				Call();
			}
		});

		//BUTTON2
		Setinfo = (Button) findViewById(R.id.SetInfo);
		Setinfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				savePreferences("storedEmail", editEmail.getText().toString());
				savePreferences("storedPhone1", editphoneNo1.getText().toString());
				savePreferences("storedPhone2", editphoneNo2.getText().toString());
				savePreferences("storedSms", editMessage.getText().toString());
				Toast.makeText(getApplicationContext(),"Information Set, you don't need to enter it again now",Toast.LENGTH_LONG).show();
				}
		});
		loadSavedPreferences();
		// String FinalName = sharedpreferences.getString(Email, Email);
		// txtEmail.setTag(FinalName);
	}

	private void loadSavedPreferences() {

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


		String email = sharedPreferences.getString("storedEmail", "");
		String phone1 = sharedPreferences.getString("storedPhone1", "");
		String phone2 = sharedPreferences.getString("storedPhone2", "");
		String sms = sharedPreferences.getString("storedSms", "");

		editEmail.setText(email);
		editMessage.setText(sms);
		editphoneNo1.setText(phone1);
		editphoneNo2.setText(phone2);
	}

	private void savePreferences(String key, String value) {

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();

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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void GetLocation() {
		gps = new GPSTracker(MainActivity.this);

		double latitude = 0;
		double longitude = 0;
		// check if GPS enabled
		if (gps.canGetLocation()) {

			latitude = gps.getLatitude();
			longitude = gps.getLongitude();

			List<Address> addresses = null;
			Geocoder geocoder = new Geocoder(MainActivity.this, Locale.ENGLISH);

			try {
				addresses = geocoder.getFromLocation(latitude, longitude, 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String address = addresses.get(0).getAddressLine(0);
			String city = addresses.get(0).getAddressLine(1);
			String country = addresses.get(0).getAddressLine(2);

			StringBuilder builder = new StringBuilder();
			builder.append(address);
			builder.append(" ," + city);
			builder.append(" ," + country);
			Comp = builder.toString();
			Toast.makeText(getApplicationContext(), Comp, Toast.LENGTH_LONG)
					.show();

		}
	}

	protected void SendEmail() {
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse("mailto:" + editEmail.getText().toString()));
		intent.putExtra(Intent.EXTRA_SUBJECT, "URGENT !! Trouble ");
		intent.putExtra(Intent.EXTRA_TEXT,
				"I'm in trouble \n Please Come to me as soon as possible at this Address : \n" + Comp);
		startActivity(intent);
	}

	protected void Call() {
		Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + editphoneNo1.getText().toString()));  
		//i.setPackage("com.android.phone");               
		startActivity(i);
		
		
	}
	
	protected void SendSMS() {
		Log.i("Send SMS", "");

		String phoneNo = editphoneNo2.getText().toString();

		String message = editMessage.getText().toString();

		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phoneNo, null, message, null, null);
			Toast.makeText(getApplicationContext(), "SMS sent.",
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					"SMS failed, please try again.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	}
}
