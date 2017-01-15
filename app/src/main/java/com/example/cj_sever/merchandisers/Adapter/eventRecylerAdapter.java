package com.example.cj_sever.merchandisers.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cj_sever.merchandisers.Constants;
import com.example.cj_sever.merchandisers.EventPop;
import com.example.cj_sever.merchandisers.Models.Event;
import com.example.cj_sever.merchandisers.R;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by cj-sever on 11/20/16.
 */
public class eventRecylerAdapter extends RecyclerView.Adapter<eventRecylerAdapter.viewHolder> {
    private List<Event> eventList;
    private Context context;
    private DatabaseReference mDatabaseReference;

    public eventRecylerAdapter(List<Event> eventList, Context context, DatabaseReference mDatabaseReference) {
        this.eventList = eventList;
        this.context = context;
        this.mDatabaseReference = mDatabaseReference;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_custom,null);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, int position) {
        final Event eventItem = eventList.get(position);
        holder.mTitle.setText(eventItem.getTitle());
        holder.mDescrition.setText(eventItem.getDescription());
        holder.mDate.setText(eventItem.getDateFrom());
        holder.mVenue.setText("Venue: " +eventItem.getVenue());
        holder.mTime.setText(eventItem.getStartTime());

        if (holder.mImage!=null){
            Picasso.with(context)
                    .load(eventItem.getImage())
                    .resize(100,0)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(holder.mImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            if(holder.progressBar != null){
                                holder.progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError() {
                            holder.progressBar.setVisibility(View.GONE);

                        }
                    });
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,EventPop.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.EVENTNAME,eventItem.getTitle());
                intent.putExtra(Constants.EVENTIMAGE,eventItem.getImage());
                intent.putExtra(Constants.EVENTDESCRIPTION,eventItem.getDescription());
                intent.putExtra(Constants.EVENT_DAY_FROM,eventItem.getDateFrom());
                intent.putExtra(Constants.EVENT_DAY_TO,eventItem.getDateTo());
                intent.putExtra(Constants.EVENT_START_TIME, eventItem.getStartTime());
                intent.putExtra(Constants.EVENT_END_TIME,eventItem.getEndTime());
                intent.putExtra(Constants.EVENTVENUE,eventItem.getVenue());
                intent.putExtra(Constants.EVENTORGANIZER,eventItem.getOrganiser());
                intent.putExtra(Constants.EVENTOWNER,eventItem.getOwner());
                context.startActivity(intent);
                Toast.makeText(context,"layer cliked",Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        if(eventList !=null && eventList.size() != 0);
        return eventList.size();
    }

    public  static class viewHolder extends RecyclerView.ViewHolder{
        private ImageView mImage;
        private TextView mTitle,mDescrition, mDate,mTime, mVenue;
        private ProgressBar progressBar;
        private LinearLayout layout;

        public viewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            mImage = (ImageView) itemView.findViewById(R.id.eventImage);
            layout = (LinearLayout) itemView.findViewById(R.id.listLayout);
            mVenue = (TextView) itemView.findViewById(R.id.tvvenue);
            mDate = (TextView) itemView.findViewById(R.id.tvdate);
            mTime = (TextView) itemView.findViewById(R.id.tvtime);
            mDescrition  =  (TextView) itemView.findViewById(R.id.tventDescrition);
            progressBar = (ProgressBar) itemView.findViewById(R.id.imageprogressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
