package ork.liesa.solaceapplication;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatbotActivity  extends AppCompatActivity {
    private RecyclerView chatsRV;
    private EditText userMsgEdt;
    private FloatingActionButton sendMsgFAB;
    private final String BOT_KEY="bot";
    private final String USER_KEY="user";
    private ArrayList<ChatModal>chatModalArrayList;
    private ChatbotRVAdapter chatbotRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
//
//        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS)!=PackageManager.PERMISSION_GRANTED)
//        {
//            ActivityCompat.requestPermissions(this,
//            chatModalArrayList(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS)),
//            111
//        }

        chatsRV=findViewById(R.id.idRVChats);
        userMsgEdt=findViewById(R.id.EdtMessage);
        sendMsgFAB=findViewById(R.id.idFABSend);
        chatModalArrayList=new ArrayList<>();
        chatbotRVAdapter=new ChatbotRVAdapter(chatModalArrayList,this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatsRV.setLayoutManager(manager);
        chatsRV.setAdapter(chatbotRVAdapter);

        sendMsgFAB.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if(userMsgEdt.getText().toString().isEmpty()){
                    Toast.makeText(ChatbotActivity.this,"please enter your message",Toast.LENGTH_LONG).show();
                    return;
                }
                getResponse(userMsgEdt.getText().toString());
                userMsgEdt.setText("");
            }
        });
    }
    private void getResponse(String message){
        chatModalArrayList.add(new ChatModal(message,USER_KEY));
      chatbotRVAdapter.notifyDataSetChanged();
        String url = "http://api.brainshop.ai/get?bid=166135&key=HutlT9ZgXWrPpz3o&uid=uid&msg="+message;
        String BASE_URL="http://api.brainshop.ai/";
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MsgModal>call = retrofitAPI.getMessage(url);
        call.equals(new Callback<MsgModal>() {
            @Override
            public void onResponse(Call<MsgModal> call, Response<MsgModal> response) {
                if (response.isSuccessful()){
                    MsgModal modal = response.body();
                    chatModalArrayList .add(new ChatModal(modal.getCnt(),BOT_KEY));
                    chatbotRVAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onFailure(Call<MsgModal> call, Throwable t) {
              chatModalArrayList.add(new ChatModal("I did not get that",BOT_KEY));
              chatbotRVAdapter.notifyDataSetChanged();

            }

        });
    }

}
