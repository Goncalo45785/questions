package pt.goncalo.blissquestions.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import pt.goncalo.blissquestions.R;
import pt.goncalo.blissquestions.model.entity.Question;
import pt.goncalo.blissquestions.view.DetailActivity;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.ViewHolder> {
    private List<Question> dataset;

    public QuestionListAdapter(List<Question> dataset) {
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.question_list_item,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Question question = dataset.get(position);
        holder.title.setText(question.getQuestion());
        holder.date.setText(question.getPublishedAt());
        Picasso.get()
                .load(question.getThumbUrl())
                .placeholder(R.drawable.ic_broken_image_primary_24dp)
                .error(R.drawable.ic_broken_image_primary_24dp)
                .into(holder.thumbnail);
        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(context.getString(R.string.question_id_extra_key), question.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView title, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
        }
    }
}
