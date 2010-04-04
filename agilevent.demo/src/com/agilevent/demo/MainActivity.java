package com.agilevent.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;


public class MainActivity extends Activity 	implements TabHost.OnTabChangeListener {
    
	public static final int DIALOG_LOADING = 0; 
	public static final int DIALOG_LOADING_FINISHED = 1;
	public static final int DIALOG_MENU_ABOUT = 2; 
	
	public static final int MENU_ABOUT = 0; 
	
	
	ProgressDialog mDialog;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        // Get the tab host we want to work with. 
        TabHost tabs = (TabHost)findViewById(R.id.tabhost);
        tabs.setup();
        tabs.setOnTabChangedListener(this); 

        
        // Set up the tabs
        TabHost.TabSpec spec = tabs.newTabSpec("tab1");
        spec.setContent(R.id.tab1).setIndicator("Contact", getResources().getDrawable(R.drawable.mail_icon)); 
        tabs.addTab(spec);
        
        spec = tabs.newTabSpec("tab2"); 
        spec.setContent(R.id.tab2).setIndicator("Tweets", getResources().getDrawable(R.drawable.tweet_icon)); 
        tabs.addTab(spec);
        
     
        spec = tabs.newTabSpec("tab3"); 
        spec.setContent(R.id.tab3).setIndicator("Rate Session",	getResources().getDrawable(R.drawable.feedback_icon));
        tabs.addTab(spec);
        
        handleSendButtonClick(); 
        
    }
    
    

	private void handleSendButtonClick() {
		// Handle contact sending
        Button button = (Button)findViewById(R.id.send_button);
        button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				EditText subject = (EditText)findViewById(R.id.subject); 
				EditText message = (EditText)findViewById(R.id.message);
				
				
				// Start activity to send email. 
				Intent i = new Intent(Intent.ACTION_SEND); 
				i.setType("plain/text");
				i.putExtra(Intent.EXTRA_EMAIL, "dfelker@agilevent.com");
				i.putExtra(Intent.EXTRA_SUBJECT, "From Demo App: " + subject.getText());
				i.putExtra(Intent.EXTRA_TEXT, "Sent From the Agilevent Demo App\r\n" + message.getText());
				
				// Start with a choose just in case user DOES not 
				// have anything to handle the intent, we will not 
				// get an exception, or allow which app the user wants 
				// to send the email with. 
				startActivity(Intent.createChooser(i, "Send with ...")); 
			}
		
		}); 
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ABOUT, 0, "About").setIcon(R.drawable.about_icon); 
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case MENU_ABOUT: 
			showDialog(DIALOG_MENU_ABOUT); 
			break; 
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	


	@Override
	public void onTabChanged(String tabId) {
		// Figure out what tab has been selected and 
		// perform an action. 
		int id = getResources().getIdentifier(tabId, "id", getPackageName()); 
		
		switch(id) {
			case R.id.tab3: 
				loadWebPage();
				break; 
		}
	}

 
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_LOADING: 
				 return ProgressDialog.show(MainActivity.this, "", "Loading. Please Wait ...", true);
			case DIALOG_MENU_ABOUT: 
				return new AlertDialog.Builder(MainActivity.this)
							.setMessage(R.string.app_about)
							.setCancelable(true)
							.create(); 
							
		}
		return super.onCreateDialog(id);
	}	 
	
	
	
	private void loadWebPage() {
	

		WebView wv = (WebView)findViewById(R.id.feedback); 
		WebSettings settings = wv.getSettings(); 
		settings.setJavaScriptEnabled(true);
		
		 wv.setWebViewClient(new WebViewClient() {
			 
			 @Override
			 public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
				 showDialog(DIALOG_LOADING); 
			 };
			 
			 @Override 
			 public void onPageFinished(WebView view, String url) {
				 dismissDialog(DIALOG_LOADING);
			 };
			 
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true; 
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				dismissDialog(DIALOG_LOADING); 
				Toast.makeText(MainActivity.this, "Uh Oh! An Error Occured!", Toast.LENGTH_LONG).show(); 
				
			}
		 });

		
		wv.loadUrl("http://tccc.agilevent.com");
		
	}
}