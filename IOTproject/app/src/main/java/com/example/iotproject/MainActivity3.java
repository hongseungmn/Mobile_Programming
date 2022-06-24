package com.example.iotproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.os.AsyncTask;
import android.widget.Toast;

import org.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity3 extends AppCompatActivity {

    private String htmlPageUrl = "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1=101";
    private TextView textviewHtmlDocument;
    private String htmlContentlnStringFormat="";
    private String nowCnt="";
    ProgressDialog dialog;
    private Elements titles;
    private int num;

    int cnt=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Button newsLink = (Button)findViewById(R.id.newsLink);
        String text = "naver뉴스 보러 가기=>(click)";
        newsLink.setText(text);

        //링크를 달아주는 코드로 링크주소는 뉴스를 크롤링한 주소와 동일하게 했다.
        Linkify.TransformFilter mtransform = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher matcher, String s) {
                return "";
            }
        };
        Pattern pattern = Pattern.compile("(click)");
        Linkify.addLinks(newsLink,pattern,htmlPageUrl,null,mtransform);


        textviewHtmlDocument = (TextView)findViewById(R.id.textNews);
        textviewHtmlDocument.setMovementMethod(new ScrollingMovementMethod());

        //버튼을 누르면 동작 할 수 있도록 버튼을 변수에 저장하고 리스너를 생성했다.
        Button htmlTitleButton = (Button)findViewById(R.id.uploadButton);
        htmlTitleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new ProgressDialog(MainActivity3.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("데이터를 확인하는 중입니다.");

                dialog.show();
                //버튼을 누를 때마다 실행(크롤링)한 횟수를 출력
                System.out.println((cnt + 1) + "번째 파싱");
                //크롤링을 위한 JSoupAsyncTask 객체를 만들었다.
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
                cnt++;



                Handler handler = new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        dialog.dismiss(); // 3초 시간지연 후 프로그레스 대화상자 닫기
                        Toast.makeText(MainActivity3.this, "로딩되었습니다.", Toast.LENGTH_LONG).show();
                    }
                }, 3000);
            }
        });



    }

    //크롤링을 위한 객체 생성
    private class JsoupAsyncTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                // 해당 크롤링 주소와 get방식으로 연결을 한다.
                Document doc = Jsoup.connect(htmlPageUrl).get();

                num =0;
                //긁어올 데이터의 태그 값을 삽입한다.
                titles = doc.select("div.cluster_head_inner a");
                System.out.println("----------------------------");
                //긁어온 값을 for 문을 통해 정렬하고 있다.
                for(Element e: titles) {
                    //긁어온 값을 출력을 통해 확인하고 있다.
                    System.out.println("title: " + e.text() + num);
                    if ((num %2) == 1) {
                        //관련뉴스 더보기 란이 반복해서 입력되므로 한 줄씩 건너띄어 값을 저장하고 공백을 제거 후 줄바꿈을 통해 기사를 구분했다.
                        htmlContentlnStringFormat +=  "\n\t\t" +e.text().trim() +"\n";
                    }
                    num++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        //기사를 넣을 Text값을 지정해 출력한다.
        @Override
        protected void onPostExecute(Void result) {
            textviewHtmlDocument.setText(htmlContentlnStringFormat);
        }
    }


}