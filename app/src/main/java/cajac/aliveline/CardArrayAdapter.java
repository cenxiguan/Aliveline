package cajac.aliveline;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Amauris on 6/14/2015.
 */
public class CardArrayAdapter extends ArrayAdapter<Todo> {
    List<Todo> todoCards;

    public CardArrayAdapter(Context context, int resource, int textViewResourceId, List<Todo> todoCards){
        super(context, resource, textViewResourceId, todoCards);
        this.todoCards = todoCards;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if (v == null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.card_view_list, null);
        }
        Todo todo = todoCards.get(position);

        CardView cardView = (CardView)v.findViewById(R.id.card_view);
        TextView tv = (TextView)cardView.findViewById(R.id.card_header);
        TextView desc = (TextView)cardView.findViewById(R.id.desc);
        desc.setText(todo.getDescription());
        tv.setText(todo.getTitle());

        return v;
    }

}
