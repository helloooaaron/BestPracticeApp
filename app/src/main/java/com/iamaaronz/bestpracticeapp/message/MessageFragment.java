package com.iamaaronz.bestpracticeapp.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iamaaronz.bestpracticeapp.R;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    List<Message> mMessages = new ArrayList<>();

    RecyclerView mRecyclerView;

    EditText mEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        populateMessages();
        mRecyclerView = view.findViewById(R.id.recycler_view_messages);
        final RecyclerView.Adapter<MessageAdapter.ViewHolder> adapter = new MessageAdapter(mMessages);
        mRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mEditText = view.findViewById(R.id.edit_text_message);
        Button sendButton = view.findViewById(R.id.button_send_message);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mEditText.getText().toString();
                if (!TextUtils.isEmpty(msg)){
                    mMessages.add(new Message(msg, Message.TYPE_SENT));
                    adapter.notifyItemInserted(mMessages.size() - 1);
                    mRecyclerView.scrollToPosition(mMessages.size() - 1);
                    mEditText.setText("");
                }
            }
        });

        return view;
    }

    private void populateMessages() {
        mMessages.add(new Message("Hello", Message.TYPE_RECEIVED));
        mMessages.add(new Message("Hey", Message.TYPE_SENT));
        mMessages.add(new Message("How are you?", Message.TYPE_RECEIVED));
        mMessages.add(new Message("I'm good :)", Message.TYPE_SENT));
    }
}


class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    List<Message> messages;

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rcvLayout, sndLayout;
        TextView rcvTextView, sndTextView
                ;
        public ViewHolder(View view) {
            super(view);
            rcvLayout = view.findViewById(R.id.layout_rcv_msg);
            rcvTextView = view.findViewById(R.id.textview_rcv_msg);
            sndLayout = view.findViewById(R.id.layout_snd_msg);
            sndTextView = view.findViewById(R.id.textview_snd_msg);
        }
    }

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
//        below invocation cause error: "java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first."
//        View view = View.inflate(parent.getContext(), R.layout.message_item, parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);
        switch (message.type) {
            case Message.TYPE_RECEIVED:
                holder.rcvTextView.setText(message.content);
                holder.rcvLayout.setVisibility(View.VISIBLE);
                break;
            case Message.TYPE_SENT:
                holder.sndTextView.setText(message.content);
                holder.sndLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}


class Message {

    static final int TYPE_RECEIVED = 0;

    static final int TYPE_SENT = 1;

    final String content;

    final int type;

    Message(String message, int type) {
        this.content = message;
        this.type = type;
    }
}