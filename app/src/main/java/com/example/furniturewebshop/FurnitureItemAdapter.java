package com.example.furniturewebshop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FurnitureItemAdapter extends RecyclerView.Adapter<FurnitureItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<FurnitureItem> furnitureItems;
    private ArrayList<FurnitureItem> allFurnitureItems;
    private Context context;
    private int lastPos = -1;

    public FurnitureItemAdapter(Context context, ArrayList<FurnitureItem> items) {
        this.furnitureItems = items;
        this.allFurnitureItems = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.furniture_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FurnitureItemAdapter.ViewHolder holder, int position) {
        FurnitureItem currentItem = furnitureItems.get(position);

        holder.bindTo(currentItem);
        if(holder.getAdapterPosition() > lastPos) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in);
            holder.itemView.startAnimation(animation);
            lastPos = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return furnitureItems.size();
    }

    @Override
    public Filter getFilter() {
        return furnitureFilter;
    }

    private Filter furnitureFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<FurnitureItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.count = allFurnitureItems.size();
                results.values = allFurnitureItems;
            } else{
                String pattern = constraint.toString().toLowerCase().trim();

                for (FurnitureItem item : allFurnitureItems) {
                    if(item.getName().toLowerCase().contains(pattern)) {
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            furnitureItems = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView descriptionText;
        private TextView priceText;
        private ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.product_name);
            descriptionText = itemView.findViewById(R.id.product_description);
            priceText = itemView.findViewById(R.id.product_price);
            itemImage = itemView.findViewById(R.id.product_image);

            itemView.findViewById(R.id.addToCart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Activity", "Button clicked!");
                    ((FurnitureListActivity) context).updateIcon();
                }
            });
        }

        public void bindTo(FurnitureItem currentItem) {
            nameText.setText(currentItem.getName());
            descriptionText.setText(currentItem.getDescription());
            priceText.setText(currentItem.getPrice());

            Glide.with(context).load(currentItem.getImageResource()).into(itemImage);
        }
    }
}

