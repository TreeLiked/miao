package com.example.lqs2.courseapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.ChatMsgAdapter;
import com.example.lqs2.courseapp.utils.StatusBarUtils;

public class ChatActivity extends ActivityCollector {



    private RecyclerView recyclerView;
    private EditText mEditText;
    private ChatMsgAdapter msgAdapter;
    private TextView titleView;

    private String un;
    private String friendId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        StatusBarUtils.setStatusBarTransparentAndTextColorBlack(this);

        bindViews();
        bindActions();
        bindIntentData();
//        testSomeData();


    }


    public void bindIntentData() {
        Intent i =  getIntent();
        un = i.getStringExtra("un");
        friendId = i.getStringExtra("friendId");
        titleView.setText(friendId);
    }

    private void bindViews() {
        mEditText = findViewById(R.id.chat_edit_text);
        recyclerView = findViewById(R.id.chat_msg_recycler_view);
        titleView = findViewById(R.id.chat_title);
        msgAdapter = new ChatMsgAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(msgAdapter);
    }

    private void bindActions() {
        mEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                //处理事件
            }
            return true;
        });
    }
}
