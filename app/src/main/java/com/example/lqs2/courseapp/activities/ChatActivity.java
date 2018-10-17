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

/**
 * 聊天活动
 *
 * @author lqs2
 */
public class ChatActivity extends ActivityCollector {


    private EditText mEditText;
    private TextView titleView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        StatusBarUtils.setStatusBarTransparentAndTextColorBlack(this);

        bindViews();
        bindActions();
        bindIntentData();
    }

    /**
     * 从intent中获取聊天所需要的信息
     */
    public void bindIntentData() {
        Intent i = getIntent();
        String un = i.getStringExtra("un");
        String friendId = i.getStringExtra("friendId");
        titleView.setText(friendId);
    }

    /**
     * 绑定视图
     */
    private void bindViews() {
        mEditText = findViewById(R.id.chat_edit_text);
        RecyclerView recyclerView = findViewById(R.id.chat_msg_recycler_view);
        titleView = findViewById(R.id.chat_title);
        ChatMsgAdapter msgAdapter = new ChatMsgAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(msgAdapter);
    }

    /**
     * 绑定聊天框点击发送消息的动作
     */
    private void bindActions() {
        mEditText.setOnEditorActionListener((v, actionId, event) -> {
            boolean flag = actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction());
            if (flag) {
                //处理事件
            }
            return true;
        });
    }
}
