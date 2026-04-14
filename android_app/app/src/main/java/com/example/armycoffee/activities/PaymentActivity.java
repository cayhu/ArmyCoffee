package com.example.armycoffee.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.example.armycoffee.databinding.ActivityPaymentBinding;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String paymentUrl = getIntent().getStringExtra("PAYMENT_URL");

        binding.btnBack.setOnClickListener(v -> finish());

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setDomStorageEnabled(true);
        
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // Kiểm tra nếu URL trả về là URL thành công của VNPay
                if (url.contains("vnp_ResponseCode=00")) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("PAYMENT_STATUS", "SUCCESS");
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else if (url.contains("vnp_ResponseCode") && !url.contains("vnp_ResponseCode=00")) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("PAYMENT_STATUS", "FAILED");
                    setResult(RESULT_CANCELED, resultIntent);
                    finish();
                }
            }
        });

        binding.webView.loadUrl(paymentUrl);
    }
}
