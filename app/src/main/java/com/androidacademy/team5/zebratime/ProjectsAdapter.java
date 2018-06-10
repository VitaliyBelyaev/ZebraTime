package com.androidacademy.team5.zebratime;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import entity.Project;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectVH> {

    private List<Project> projects;
    private ProjectOnClickHandler onClickHandler;

    public interface ProjectOnClickHandler{
        void onProjectClick(String projectId);
    }

    public void replaceWith(List<Project> projects){
        this.projects = projects;
        notifyDataSetChanged();
    }

    public ProjectsAdapter(ProjectOnClickHandler onClickHandler) {
        this.onClickHandler = onClickHandler;
    }

    @NonNull
    @Override
    public ProjectsAdapter.ProjectVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ProjectVH(inflater.inflate(R.layout.project_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsAdapter.ProjectVH holder, int position) {
        String title = projects.get(position).getTitle();
        holder.textView.setText(title);
    }

    @Override
    public int getItemCount() {
        if(projects != null){
            return projects.size();
        }
        return 0;
    }

    public class ProjectVH extends RecyclerView.ViewHolder{

        TextView textView;

        public ProjectVH(View itemView){
            super(itemView);

            this.textView = itemView.findViewById(R.id.tv_project);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    onClickHandler.onProjectClick(projects.get(position).getId());
                }
            });
        }
    }
}
