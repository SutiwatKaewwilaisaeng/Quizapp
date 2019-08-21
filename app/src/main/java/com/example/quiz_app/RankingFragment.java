package com.example.quiz_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quiz_app.Common.Common;
import com.example.quiz_app.Interface.ItemClickListener;
import com.example.quiz_app.Interface.RankingCallBack;
import com.example.quiz_app.Model.QuestionScore;
import com.example.quiz_app.Model.Ranking;
import com.example.quiz_app.ViewHolder.RankingViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RankingFragment extends Fragment {

    View myFragment;
    FirebaseDatabase database;
    DatabaseReference questionScore,rankingTbl1;

    RecyclerView rankingList;
    LinearLayoutManager layoutManager1;
    FirebaseRecyclerAdapter<Ranking, RankingViewHolder> adapter;

    RecyclerView listCategory;
    RecyclerView.LayoutManager layoutManager;

    int sum = 0;



    public static RankingFragment newInstance() {
        RankingFragment rankingFragment = new RankingFragment();
        return rankingFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        questionScore = database.getReference("Question_Score");
        rankingTbl1 = database.getReference("Ranking");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_ranking,container,false);

        //Init View
        rankingList = myFragment.findViewById(R.id.rankingList);
        layoutManager1 = new LinearLayoutManager(getActivity());
        rankingList.setHasFixedSize(true);

        //Because OrderBychild method of Firebase will sort list with ascending
        //So we need reverse our Recycler data
        //By LayoutManager
        layoutManager1.setReverseLayout(true);
        layoutManager1.setStackFromEnd(true);
        rankingList.setLayoutManager(layoutManager1);


        //Now , we need implement callback
        updateScore(Common.currentUser1.getUserName(), new RankingCallBack<Ranking>() {
            @Override
            public void callBack(Ranking ranking)
            {
                //update to Ranking table
                rankingTbl1.child(ranking.getUserName())
                        .setValue(ranking);
                          // showRanking();//After upload , we will sort Ranking table and show result

            }
        });

        //Set Adapter
        adapter = new FirebaseRecyclerAdapter<Ranking, RankingViewHolder>(
                Ranking.class,
                R.layout.layout_ranking,
                RankingViewHolder.class,
                rankingTbl1.orderByChild("score")
        ) {
            @Override
            protected void populateViewHolder(RankingViewHolder viewHolder, final Ranking model, int position)
            {
                viewHolder.txt_name.setText(model.getUserName());
                viewHolder.txt_score.setText(String.valueOf(model.getScore()));
                //Fixed crash when click to item
                viewHolder.setItemClickListener(new ItemClickListener()
                {
                    @Override
                    public void onClick(View view, int position, boolean isLongclick)
                    {
                        Intent scoreDetail = new Intent(getActivity(),ScoreDetailActivity.class);
                        scoreDetail.putExtra("viewUser",model.getUserName());
                        startActivity(scoreDetail);

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        rankingList.setAdapter(adapter);
        return myFragment;
    }




    private void updateScore(final String userName, final RankingCallBack<Ranking>callback)
    {
        questionScore.orderByChild("user").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot data: dataSnapshot.getChildren())
                        {
                            QuestionScore ques = data.getValue(QuestionScore.class);
                            sum += Integer.parseInt(ques.getScore());
                        }
                        //After summary all score , we need process sum variable here
                        //Because firebase is async db , so if process outside , our 'sum'
                        //value will be reset to 0
                        Ranking ranking = new Ranking(userName,sum);
                        callback.callBack(ranking);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
    }
}
