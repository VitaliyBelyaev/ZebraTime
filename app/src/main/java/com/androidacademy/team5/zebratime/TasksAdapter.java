package com.androidacademy.team5.zebratime;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidacademy.team5.zebratime.domain.Project;
import com.androidacademy.team5.zebratime.domain.Task;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskVH>{

    private List<Task> tasks;
    private TaskOnClickHandler onClickHandler;

    public interface TaskOnClickHandler{
        void onTaskClick(String taskId);
    }

    public void replaceWith(List<Task> tasks){
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public TasksAdapter(TaskOnClickHandler onClickHandler) {
        this.onClickHandler = onClickHandler;
    }

    @NonNull
    @Override
    public TasksAdapter.TaskVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TasksAdapter.TaskVH(inflater.inflate(R.layout.task_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TasksAdapter.TaskVH holder, int position) {
        String title = tasks.get(position).getTitle();
        holder.titleTextView.setText(title);
    }

    @Override
    public int getItemCount() {
        if(tasks != null){
            return tasks.size();
        }
        return 0;
    }

    public class TaskVH extends RecyclerView.ViewHolder{

        TextView titleTextView;
        TextView durationTextView;
        TextView pomodorosTextView;


        public TaskVH(View itemView){
            super(itemView);



            titleTextView = itemView.findViewById(R.id.tv_task_title);
            durationTextView = itemView.findViewById(R.id.tv_task_duration);
            pomodorosTextView = itemView.findViewById(R.id.tv_task_pomodoros);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    onClickHandler.onTaskClick(tasks.get(position).getId());
                }
            });
        }
    }
}
