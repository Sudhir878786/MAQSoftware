package net.maqsoftware.maqsoftware;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.View;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.airbnb.lottie.LottieAnimationView;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;

import java.util.concurrent.Executor;
public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private RelativeLayout loadingOverlay;
    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize WebView and Loading Overlay
        webView = findViewById(R.id.webView);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);

        CustomWebViewClient client = new CustomWebViewClient(this, loadingOverlay, lottieAnimationView);
        webView.setWebViewClient(client);
        webView.getSettings().setJavaScriptEnabled(true);

        // Authenticate the user before loading the webpage
        authenticateUser();

        // Initialize Contact button and set click listener
        Button contactButton = findViewById(R.id.contactButton);
        contactButton.setOnClickListener(view -> {
            // Open LinkedIn in a browser
            String linkedInUrl = "https://www.linkedin.com/in/sudhirsharma87/";  // Replace with your LinkedIn profile URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedInUrl));
            startActivity(intent);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void authenticateUser() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                showBiometricPrompt();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "Biometric not available or not set up", Toast.LENGTH_SHORT).show();
                webView.loadUrl("https://testmaq.sharepoint.com/myspace/Pages/MySpace.aspx#");
                break;
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                webView.loadUrl("https://testmaq.sharepoint.com/myspace/Pages/MySpace.aspx#");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Frictionless Authentication ⚡🔓")
                .setSubtitle("Hmm! Are You MAQian? 🤔")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}

class CustomWebViewClient extends WebViewClient {
    private RelativeLayout loadingOverlay;
    private LottieAnimationView lottieAnimationView;

    public CustomWebViewClient(Activity activity, RelativeLayout loadingOverlay, LottieAnimationView lottieAnimationView) {
        this.loadingOverlay = loadingOverlay;
        this.lottieAnimationView = lottieAnimationView;
    }

    @Override
    public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        loadingOverlay.setVisibility(View.VISIBLE); // Show the loader
        lottieAnimationView.playAnimation(); // Play the animation
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        loadingOverlay.setVisibility(View.GONE); // Hide the loader
        lottieAnimationView.cancelAnimation(); // Stop the animation
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return false;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        return false;
    }
}