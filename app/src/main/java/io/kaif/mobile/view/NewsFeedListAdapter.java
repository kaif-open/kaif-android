package io.kaif.mobile.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import io.kaif.mobile.R;
import io.kaif.mobile.kmark.KmarkProcessor;
import io.kaif.mobile.view.viewmodel.DebateViewModel;
import io.kaif.mobile.view.viewmodel.FeedAssetViewModel;
import io.kaif.mobile.view.widget.ClickableSpanTouchListener;

public class NewsFeedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public interface OnItemClickListener {
    void onItemClick(DebateViewModel debateViewModel);
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  private OnItemClickListener onItemClickListener;

  class DebateViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.content)
    public TextView content;
    @Bind(R.id.last_update_time)
    public TextView lastUpdateTime;
    @Bind(R.id.vote_score)
    public TextView voteScore;
    @Bind(R.id.debater_name)
    public TextView debaterName;
    @Bind(R.id.zone)
    public TextView zone;

    private DebateViewModel debateViewModel;

    public DebateViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(v -> {
        if (onItemClickListener != null) {
          onItemClickListener.onItemClick(debateViewModel);
        }
      });
      content.setOnTouchListener(new ClickableSpanTouchListener());
    }

    public void update(FeedAssetViewModel feedAssetViewModel) {
      this.debateViewModel = feedAssetViewModel.getDebateViewModel();
      final Context context = itemView.getContext();
      debaterName.setText(debateViewModel.getDebaterName());
      content.setText(KmarkProcessor.process(context, debateViewModel.getContent()));
      lastUpdateTime.setText(DateUtils.getRelativeTimeSpanString(debateViewModel.getLastUpdateTime()
          .getTime(), System.currentTimeMillis(), 0, DateUtils.FORMAT_ABBREV_RELATIVE));
      voteScore.setText(String.valueOf(debateViewModel.getVoteScore()));
      zone.setText(itemView.getContext().getString(R.string.zone_path, debateViewModel.getZone()));
      itemView.setActivated(!feedAssetViewModel.isRead());
    }

  }

  private final List<FeedAssetViewModel> feedAssets;

  private boolean hasNextPage;

  public NewsFeedListAdapter() {
    this.feedAssets = new ArrayList<>();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
    if (viewType == R.layout.item_loading) {
      return new RecyclerView.ViewHolder(view) {
      };
    }
    return new DebateViewHolder(view);
  }

  @Override
  public int getItemViewType(int position) {
    if (position >= feedAssets.size()) {
      return R.layout.item_loading;
    }
    return R.layout.item_debate_feed;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (position >= feedAssets.size()) {
      return;
    }
    FeedAssetViewModel feedAssetViewModel = feedAssets.get(position);
    DebateViewHolder debateViewHolder = (DebateViewHolder) holder;
    debateViewHolder.update(feedAssetViewModel);
  }

  @Override
  public int getItemCount() {
    return feedAssets.size() + (hasNextPage ? 1 : 0);
  }

  public void refresh(List<FeedAssetViewModel> feedAssets) {
    this.feedAssets.clear();
    this.feedAssets.addAll(feedAssets);
    hasNextPage = !feedAssets.isEmpty();
    notifyDataSetChanged();
  }

  public void addAll(List<FeedAssetViewModel> feedAssets) {
    if (feedAssets.isEmpty()) {
      hasNextPage = false;
      return;
    }
    this.feedAssets.addAll(feedAssets);
    notifyItemRangeInserted(this.feedAssets.size() - feedAssets.size(), feedAssets.size());
  }

  public String getLastAssetId() {
    return feedAssets.get(feedAssets.size() - 1).getAssetId();
  }
}
