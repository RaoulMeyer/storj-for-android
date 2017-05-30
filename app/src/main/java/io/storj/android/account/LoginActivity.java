package io.storj.android.account;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import io.storj.android.R;
import io.storj.android.api.StorjService;
import io.storj.android.bucket.BucketActivity_;
import io.storj.android.crypto.SHA256;
import io.storj.android.persistence.PreferenceStore;
import io.storj.android.util.Function;

@EActivity
public class LoginActivity extends AppCompatActivity {

    @Bean
    public PreferenceStore preferenceStore;

    @Bean
    public StorjService storjService;

    @ViewById
    public EditText username;

    @ViewById
    public EditText password;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String username = preferenceStore.getString("username");
        String password = preferenceStore.getString("password");

        if (username != null || password != null) {
            BucketActivity_.intent(this).start();
        }
    }

    @Click
    public void login() {
        System.out.println("Attempting login...");

        String usernameText = username.getText().toString();
        String passwordText = password.getText().toString();
        if (usernameText.isEmpty()) {
            username.setError("No email address specified");
            return;
        }

        if (!usernameText.contains("@") || !usernameText.contains(".")) {
            username.setError("Invalid email address");
            return;
        }

        if (passwordText.isEmpty()) {
            password.setError("No password specified");
            return;
        }

        progress = new ProgressDialog(this);
        progress.setTitle("Logging in...");
        progress.setCancelable(false);
        progress.show();

        testLogin(usernameText, passwordText);
    }

    @Background
    public void testLogin(final String username, final String password) {
        storjService.testUserLogin(username, password, new Function<Boolean>() {
            @Override
            public void call(Boolean success) {
                System.out.println(success);
                progress.dismiss();
                if (success) {
                    preferenceStore.setString("username", username);
                    preferenceStore.setString("password", SHA256.hash(password));

                    storjService.updateAuthData(username, password);

                    BucketActivity_.intent(LoginActivity.this).start();
                } else {
                    showError("Login failed. Check your credentials.");
                }
            }
        });
    }

    @UiThread
    public void showError(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(error)
                .setTitle("Error occurred")
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
