package com.example.lokerid.Fragment.Account;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.example.lokerid.Adapter.MyLokerAdapter;
import com.example.lokerid.Model.MyLokerModel;
import com.example.lokerid.R;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class KameraFragment extends Fragment {

//    RecyclerView myLoker;
//    ArrayList<MyLokerModel> list;
//    MyLokerAdapter myLokerAdapter;
    public WebView webView;
//    EditText gg;
//
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_kamera, container, false);
        webView = (WebView) v.findViewById(R.id.webview);
        webView.loadUrl("https://google.com");

        // Enable Javascript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        webView.setWebViewClient(new WebViewClient());

        return v;

    }
}
