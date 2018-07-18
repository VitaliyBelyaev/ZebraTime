package com.androidacademy.team5.zebratime;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidacademy.team5.zebratime.domain.Session;
import com.androidacademy.team5.zebratime.domain.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskVH> {

    private List<Task> tasks;
    private String projectId;
    private TaskOnClickHandler onClickHandler;

    public interface TaskOnClickHandler {
        void onTaskClick(String taskId, String projectId);
    }

    public void replaceWith(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public TasksAdapter(TaskOnClickHandler onClickHandler, String projectId) {
        this.onClickHandler = onClickHandler;
        this.projectId = projectId;
    }

    @NonNull
    @Override
    public TasksAdapter.TaskVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TasksAdapter.TaskVH(inflater.inflate(R.layout.task_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final TasksAdapter.TaskVH holder, int position) {
        String title = tasks.get(position).getTitle();
        holder.titleTextView.setText(title);

        String taskId = tasks.get(position).getId();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tasksRef = database.getReference("Sessions");

        ValueEventListener sessionsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Session> sessions = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();

                for(DataSnapshot snapshot:snapshots){
                    sessions.add(snapshot.getValue(Session.class));
                }

                long totalTime = 0;
                if(sessions != null){
                    for(Session session:sessions){
                        totalTime = totalTime + session.getDuration();
                    }

                }

                holder.durationTextView.setText(formatTime(totalTime));
                holder.pomodorosTextView.setText(String.valueOf(sessions.size()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        };


        tasksRef.orderByChild("idTask").equalTo(taskId).addValueEventListener(sessionsListener);


    }

    @Override
    public int getItemCount() {
        if (tasks != null) {
            return tasks.size();
        }
        return 0;
    }

    public class TaskVH extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView durationTextView;
        TextView pomodorosTextView;


        public TaskVH(View itemView) {
            super(itemView);


            titleTextView = itemView.findViewById(R.id.tv_task_title);
            durationTextView = itemView.findViewById(R.id.tv_task_duration);
            pomodorosTextView = itemView.findViewById(R.id.tv_task_pomodoros);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    onClickHandler.onTaskClick(tasks.get(position).getId(), projectId);
                }
            });
        }
    }

    private String formatTime(long time){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH'h' mm'm'");
        return timeFormat.format(time - 3*60*60*1000);
    }
}
