package com.example.iotproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Date;


public class MainActivity2 extends AppCompatActivity {

    //각 함수에서 사용할 변수를 초기화 했다.
    private String htmlPageUrl = "https://weather.naver.com/today/11177580";
    private TextView textviewHtmlDocument;
    private String htmlContentlnStringFormat ="";
    private int nowCnt =0;
    private ImageView ImageView1;
    private ImageView ImageView2;
    private ImageView ImageView3;
    private ImageView ImageView4;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        //다이내믹 배경화면을 위해 이미지를 불러와서 저장했다.
        ImageView1 = (ImageView)findViewById(R.id.day);
        ImageView1.setImageResource(R.drawable.day);

        ImageView2 = (ImageView)findViewById(R.id.day_night);
        ImageView2.setImageResource(R.drawable.day_night);

        ImageView3 = (ImageView)findViewById(R.id.night);
        ImageView3.setImageResource(R.drawable.night);

        ImageView4 = (ImageView)findViewById(R.id.night_day);
        ImageView4.setImageResource(R.drawable.night_day);

        //현재 시간을 추출
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String getTime = sdf.format(date);
        System.out.println(getTime);

        int time = Integer.parseInt(getTime);

        //각 시간마다 변화하는 화면을 지정했다.
        // 오전 9시 부터 오후 3시까지는 오전 배경화면
        if(time>=9 && time <15){

            ImageView1.setVisibility(View.VISIBLE);
            ImageView2.setVisibility(View.INVISIBLE);
            ImageView3.setVisibility(View.INVISIBLE);
            ImageView4.setVisibility(View.INVISIBLE);
        }
        // 오후 3시부터 오후 9시까지는 오후 배경화면
        else if(time<21 && time >=15){

            ImageView1.setVisibility(View.INVISIBLE);
            ImageView2.setVisibility(View.VISIBLE);
            ImageView3.setVisibility(View.INVISIBLE);
            ImageView4.setVisibility(View.INVISIBLE);
        }
        // 오후 9시부터 새벽 3시까지는 밤 배경화면
        else if(time <03 || time >=21) {

            ImageView1.setVisibility(View.INVISIBLE);
            ImageView2.setVisibility(View.INVISIBLE);
            ImageView3.setVisibility(View.VISIBLE);
            ImageView4.setVisibility(View.INVISIBLE);
        }
        // 새벽 3시부터 오전 9시까지는 새벽 배경화면
        else if(time >=3 && time<9) {
            ImageView1.setVisibility(View.INVISIBLE);
            ImageView2.setVisibility(View.INVISIBLE);
            ImageView3.setVisibility(View.INVISIBLE);
            ImageView4.setVisibility(View.VISIBLE);
        }




        //버튼별 액티비티 이동을 위해 리스터를 입력했다. 이는 달력 버튼에 리스너를 통해 액티비티 전환을 했다.
        Button CalenderButton = findViewById(R.id.calendar);
        CalenderButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity2.this,MainActivity.class);
                startActivity(myIntent);

            }
        });
        //버튼별 액티비티 이동을 위해 리스터를 입력했다. 이는 뉴스 버튼에 리스너를 통해 액티비티 전환을 했다.
        Button newsButton = findViewById(R.id.newsButton);
        newsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity2.this,MainActivity3.class);
                startActivity(myIntent);
            }
        });


        //온도 크롤링을 위해 지정한 코드이다.
        textviewHtmlDocument = (TextView)findViewById(R.id.Weather);
        textviewHtmlDocument.setMovementMethod(new ScrollingMovementMethod());

        TextView HtmlWeather = (TextView)findViewById(R.id.Weather);
        HtmlWeather.setOnTouchListener(new View.OnTouchListener() {
            //해당 라벨 터치시에 동작할 수 있도록 했다 이 코드는 뉴스크롤링에서 사용한 코드를 재사용 했다.
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println((nowCnt + 1) + "번째 파싱");
                MainActivity2.JsoupAsyncTask jsoupAsyncTask = new MainActivity2.JsoupAsyncTask();
                jsoupAsyncTask.execute();
                nowCnt++;
                return true;
            }
        });

        //help 버튼을 눌러 copyRight와 레이팅 바를 보여주고 사라질 수 있게 하는 코드이다.
        TextView copy = (TextView) findViewById(R.id.copyRight);
        Button copyButton = (Button)findViewById(R.id.copyRightButton);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingbar);

        copy.setVisibility(View.INVISIBLE);
        ratingBar.setVisibility(View.INVISIBLE);
        copyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(copy.getVisibility() == View.INVISIBLE)
                {
                    copy.setVisibility(View.VISIBLE);
                    ratingBar.setVisibility(View.VISIBLE);
                }
                else if(copy.getVisibility() == View.VISIBLE)
                {
                    copy.setVisibility(View.INVISIBLE);
                    ratingBar.setVisibility(View.INVISIBLE);
                }

            }
        });


    }

    //html을 크롤링 하기 위해 필요한 코드이다. 동작흐름을 확인하기 위해 출력을 통해 확인할 수 있다.
    private class JsoupAsyncTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(htmlPageUrl).get();

                //크롤링할 html의 태그를 넣어 수집한다.
                Elements titles = doc.select("div.summary_img .current");
                System.out.println("----------------------------");
                for(Element e: titles) {
                    System.out.println("title: " + e.text());
                    htmlContentlnStringFormat = e.text().trim() + "\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        // 'hello' 텍스트 값을 크롤링한 온도 값으로 바꿔 주었다.
        @Override
        protected void onPostExecute(Void result) {
            textviewHtmlDocument.setText(htmlContentlnStringFormat);
        }
    }



}