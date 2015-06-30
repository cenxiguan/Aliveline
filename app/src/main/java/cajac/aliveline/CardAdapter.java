package cajac.aliveline;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Amauris on 6/14/2015.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder>{
    private List<Todo> dataSet;


    public static class CardViewHolder extends RecyclerView.ViewHolder{
        public View cardView;
        public TextView cardTitle;
        public TextView timeLeft;
        public TextView dueDate;
        public CardViewHolder(View v){
            super(v);
            cardView = v.findViewById(R.id.card_view);
            cardTitle =  (TextView)v.findViewById(R.id.card_header);
            timeLeft = (TextView)v.findViewById(R.id.time_left);
            dueDate = (TextView)v.findViewById(R.id.due_date);
        }
    }

    public CardAdapter(List<Todo> dataSet){
        this.dataSet = dataSet;
    }
    @Override
    public CardAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_list, parent, false);
        CardViewHolder vh = new CardViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder viewHolder, int i) {
        Todo todo = dataSet.get(i);
        viewHolder.cardTitle.setText(todo.getTitle());
        viewHolder.timeLeft.setText("For Today: " + todo.getTodaysTimeLeft());
        viewHolder.dueDate.setText("Due Date: " + todo.getDueDateString());
    }


    @Override
    public int getItemCount(){
        return dataSet.size();
    }

    public Todo removeItem(int position){
        Todo todo = dataSet.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, dataSet.size());
        return todo;
    }

    public void insert(Todo todo, int position){
        dataSet.add(position, todo);
        notifyItemInserted(position);
        notifyItemRangeChanged(0, dataSet.size());
    }

}
