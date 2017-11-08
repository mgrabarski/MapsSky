package mateusz.grabarski.mapsskyrise.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mateusz.grabarski.mapsskyrise.R;
import mateusz.grabarski.mapsskyrise.adapters.listeners.PlaceListener;
import mateusz.grabarski.mapsskyrise.models.Result;

/**
 * Created by MGrabarski on 07.11.2017.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private Context mContext;
    private List<Result> mResults;
    private PlaceListener mListener;

    public PlacesAdapter(Context mContext, List<Result> mResults, PlaceListener listener) {
        this.mContext = mContext;
        this.mResults = mResults;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.populate(mResults.get(position));
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_place_name_tv)
        TextView nameTv;

        @BindView(R.id.item_place_address_tv)
        TextView addressTv;

        @BindView(R.id.item_place_root_cv)
        CardView rootCv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void populate(final Result result) {

            rootCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPlaceSelected(result);
                }
            });

            nameTv.setText(result.getName());
            addressTv.setText(result.getVicinity());
        }
    }
}
