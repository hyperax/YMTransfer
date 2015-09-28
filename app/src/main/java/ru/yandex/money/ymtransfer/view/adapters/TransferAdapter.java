package ru.yandex.money.ymtransfer.view.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.money.ymtransfer.R;
import ru.yandex.money.ymtransfer.data.model.Transfer;
import ru.yandex.money.ymtransfer.data.model.constants.PaymentStatus;

public class TransferAdapter extends BaseAdapter {

    private final List<Transfer> items = new ArrayList<>();

    private LayoutInflater inflater;

    public TransferAdapter(Context context, List<Transfer> items) {
        inflater = LayoutInflater.from(context.getApplicationContext());
        setItems(items);
    }

    public void setItems(List<Transfer> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Transfer getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        ViewHolder holder;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.list_item_transfer, parent, false);
            holder = new ViewHolder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.bind(getItem(position));

        return rowView;
    }

    private class ViewHolder {
        private TextView title;
        private TextView date;

        public ViewHolder(View rowView) {
            title = (TextView) rowView.findViewById(R.id.title);
            date = (TextView) rowView.findViewById(R.id.date);
        }

        public void bind(Transfer item) {
            if (TextUtils.isEmpty(item.getComment())) {
                title.setText(R.string.no_comment);
            } else {
                title.setText(item.getComment());
            }

            switch (item.getStatus()) {
                case PaymentStatus.COMPLETE:
                    title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok,0,0,0);
                    break;
                case PaymentStatus.ERROR:
                    title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error,0,0,0);
                    break;
                default:
                    title.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    break;
            }

            date.setText(item.getDateCreation().toString("dd/MM/YY HH:mm"));
        }
    }
}