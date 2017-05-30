package io.storj.android.account;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import io.storj.android.R;
import io.storj.android.bucket.BucketActivity_;
import io.storj.android.persistence.PreferenceStore;

@EActivity
public class AccountLinkActivity extends AppCompatActivity {

    @Bean
    public PreferenceStore preferenceStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_link);

        String username = preferenceStore.getString("username");
        String password = preferenceStore.getString("password");

        if (username != null || password != null) {
            BucketActivity_.intent(this).start();
        }
    }

    @Click
    public void register() {
        System.out.println("Register!");
    }

    @Click
    public void login() {
        LoginActivity_.intent(AccountLinkActivity.this).start();
    }
}
