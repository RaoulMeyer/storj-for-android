package io.storj.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

import io.storj.android.account.AccountLinkActivity_;
import io.storj.android.api.StorjService;
import io.storj.android.bucket.BucketActivity_;
import io.storj.android.persistence.PreferenceStore;

@EActivity
public class BootActivity extends AppCompatActivity {

    @Bean
    public StorjService storjService;

    @Bean
    public PreferenceStore preferenceStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);

        Security.addProvider(new BouncyCastleProvider());

        String username = preferenceStore.getString("username");
        String password = preferenceStore.getString("password");

        if (username == null || password == null) {
            AccountLinkActivity_.intent(this).startForResult(0);
        } else {
            BucketActivity_.intent(this).startForResult(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }
}
