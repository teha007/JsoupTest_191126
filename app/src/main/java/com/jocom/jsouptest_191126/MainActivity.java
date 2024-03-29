package com.jocom.jsouptest_191126;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sdsmdg.tastytoast.TastyToast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // 깃허브 내에서 2019.11.26 화요일 저녁 10:30분 수정

    Thread t;

    Button btn;
    Button btn2;
    private String htmlPageUrl = "http://www.yonhapnews.co.kr/";
    private TextView textviewHtmlDocument;
    private String htmlContentInStringFormat;

    int cnt = 0;
    int gauge = 0;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textviewHtmlDocument = findViewById(R.id.textView);
        textviewHtmlDocument.setMovementMethod(new ScrollingMovementMethod());
        btn = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);

//        Button htmlTitleButton = findViewById(R.id.button);
//        htmlTitleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                System.out.println((cnt+1)+"번째 파싱");
//                JsoupAsyncTsk jsoupAsyncTsk = new JsoupAsyncTsk();
//                jsoupAsyncTsk.execute();
//                cnt++;
//
//
//            }
//        });

    }

    public void clickBtn(View view) {

        btn.setEnabled(false);

        htmlContentInStringFormat = "";
//        textviewHtmlDocument.setText("");

        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

//            Toast.makeText(this, "네트워크 연결됨", Toast.LENGTH_SHORT).show();
            TastyToast.makeText(getApplicationContext(), "오늘의 뉴스", TastyToast.LENGTH_LONG, TastyToast.SUCCESS)
                    .setGravity(Gravity.CENTER, 0, 200);

            System.out.println((cnt + 1) + "번째 파싱");
            JsoupAsyncTsk jsoupAsyncTsk = new JsoupAsyncTsk();
            jsoupAsyncTsk.execute();
            cnt++;

            t.setDaemon(true);
            t.start();

        } else if (status == NetworkStatus.TYPE_NOT_CONNECTED) {
//            Toast.makeText(this, "네트워크 연결 안 됨", Toast.LENGTH_SHORT).show();
            TastyToast.makeText(getApplicationContext(), "네트워크 X", TastyToast.LENGTH_SHORT, TastyToast.ERROR);

            btn.setEnabled(true);
        }

//        try {
//            t.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public void clickBtn02(View view) {
        Intent intent = new Intent
                (Intent.ACTION_VIEW, Uri.parse("http://www.yonhapnews.co.kr/"));
        startActivity(intent);
    }

    public void clickshare(View view) {
        Intent shareintent = new Intent();
        shareintent.setAction(Intent.ACTION_SEND);
        shareintent.putExtra(Intent.EXTRA_TEXT, htmlContentInStringFormat);

        shareintent.setType("text/plain");
        startActivity(shareintent);
    }

private class JsoupAsyncTsk extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage("콘텐츠 확인 중입니다.");
        dialog.setMax(100);
        dialog.show();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dialog.setProgress(gauge);

        t = new Thread() {
            @Override
            public void run() {
                gauge = 0;

                while (gauge < 100) {
                    gauge++;
                    dialog.setProgress(gauge);

                    //50ms(0.05초) 대기
                    try {
//                            Thread.sleep(50);
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }//while

                dialog.dismiss();
                dialog = null;

            }
        };


//            try {
//                t.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... params) {

        int ccnt = 0;
        int ccnt2 = 0;
        int ccnt3 = 0;

        try {

            Document doc = Jsoup.connect(htmlPageUrl).get();

            //테스트1
            Elements titles = doc.select("div.news-con h1.tit-news");
            System.out.println("-------------------------------------------------------------");
            for (Element e : titles) {
                ccnt++;
                System.out.println("title: " + e.text());
                htmlContentInStringFormat += "연합 Head >>> " + ccnt + "번" + "\n" + e.text().trim() + "\n\n";
            }

            //테스트2
            titles = doc.select("div.news-con h2.tit-news");

            System.out.println("-------------------------------------------------------------");
            for (Element e : titles) {
                ccnt2++;
                System.out.println("title: " + e.text());
                htmlContentInStringFormat += "연합 Main >> " + ccnt2 + "번" + "\n" + e.text().trim() + "\n\n";
            }

            //테스트3
            titles = doc.select("li.section02 div.con h2.news-tl");

            System.out.println("-------------------------------------------------------------");
            for (Element e : titles) {
                ccnt3++;
                System.out.println("title: " + e.text());
                htmlContentInStringFormat += "연합 Sub > " + ccnt3 + "번" + "\n" + e.text().trim() + "\n\n";
            }
            System.out.println("-------------------------------------------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    @Override
    protected void onPostExecute(Void result) {

        textviewHtmlDocument.setText(htmlContentInStringFormat);
        btn.setEnabled(true);
    }
}
}
