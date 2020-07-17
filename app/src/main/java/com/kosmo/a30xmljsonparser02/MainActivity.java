package com.kosmo.a30xmljsonparser01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosmo.a30xmljsonparser02.ActorVO;
import com.kosmo.a30xmljsonparser02.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    Button btnJson;
    ListView listView;

    int[] profileImg={
            R.drawable.actor01,R.drawable.actor02,R.drawable.actor03,
            R.drawable.actor04,R.drawable.actor05,R.drawable.actor06,
            R.drawable.actor07,R.drawable.actor08,R.drawable.actor09,
            R.drawable.actor10,R.drawable.actor11,R.drawable.actor12
    };

    private List<ActorVO> items = new Vector<ActorVO>();

    private com.kosmo.a30xmljsonparser01.ActorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //JSON가져오기 버튼에 리스너 부착후 메소드 호출
        btnJson = (Button)findViewById(R.id.btn_json);
        btnJson.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getJsonParser();
            }
        });
    }////onCreate메소드

    //리소스에 저장된 txt파일에 IO스트림을 연결해서 내용을 읽어오는 메소드
    private String readJsonTxt()
    {
        //읽어온 JSON데이터를 저장할 변수
        String jsonData = null;
        //리소스폴더인 raw에 저장된 json.txt파일을 가져온다.
        InputStream inputStream = getResources().openRawResource(R.raw.json);
        //파일의 내용을 읽기위해 스트림을 생성한다.
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //JAVA에서 IO작업은 항상 예외를 발생시키므로 try~catch로 처리한다.
        int i;
        try{
            //스트림을 통해 파일의 내용을 읽어온다.
            i = inputStream.read();
            //파일이 끝까지 읽어오고, -1이 반환되면 끝으로 판단한다.
            while(i!=-1){
                //읽어온 내용을 저장
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            //저장된 내용을 문자열로 변환
            jsonData = byteArrayOutputStream.toString();
            inputStream.close();

        }catch(IOException e){
            e.printStackTrace();
        }
        return jsonData;
    }///readJsonTxt메소드

    private  void getJsonParser(){
        String jsonStr = readJsonTxt();
        Log.i("KOSMO61","json.txt내용 : "+jsonStr);

        try{
            //JSON은 전체적으로 객체이므로 JSONObject를 사용함
            JSONObject object = new JSONObject(jsonStr);

            //member 키값은 배열이므로 getJSONArray를 사용함.
            JSONArray array = object.getJSONArray("member");
            for(int i=0; i<array.length(); i++){
                //배열의 요소는 객체이므로 아래와같이 한명의 정보를 가져옴.
                JSONObject item = array.getJSONObject(i);

                //각 키의 해당하는 값을 가져옴
                String name = item.getString("name");
                String age = item.getString("age");

                //hobby 키값은 배열
                JSONArray hobbysArr = item.getJSONArray("hobbys");
                String hobbys ="";
                for(int j=0; j<hobbysArr.length(); j++){
                    hobbys +=hobbysArr.getString(j)+"";
                }

                //login키값은 객체
                String user = item.getJSONObject("login").getString("user");
                String pass = item.getJSONObject("login").getString("pass");
                String loginInfo = String.format("아이디 : %s, 비번:%s", user, pass);

                String printStr = String.format("이름:%s, 나이:%s, 취미:%s,"+ "아이디:%s, 피스워드:%s",
                        name, age, hobbys, user, pass);

                Log.i("KOSMO61","정보>"+printStr);

                /*
                파싱한 정보를 VO객체에 저장후 컬렉션에 추가한다.
                해당 컬렉션에 저장된 값을 어뎁터객체에서 데이터로 사용한다.
                 */
                items.add(new ActorVO(name, age, hobbys, loginInfo, profileImg[i]));
            }//for문

            //커스텀 어뎁터에 저장된 값을 리스트뷰와 연결한 후 리스너를 부착한다.
            adapter = new com.kosmo.a30xmljsonparser01.ActorAdapter(this, items, R.layout.actor_layout);
            listView = findViewById(R.id.listview);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getApplicationContext(), "선택한 배우:"+items.get(position).getName(), Toast.LENGTH_LONG).show();
                }
            });

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}