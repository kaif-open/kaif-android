package io.kaif.mobile.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.kaif.mobile.R;
import io.kaif.mobile.kmark.KmarkProcessor;
import io.kaif.mobile.view.viewmodel.DebateViewModel;
import io.kaif.mobile.view.widget.ClickableSpanTouchListener;

public class LatestDebateListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  static class DebateViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.content)
    public TextView content;
    @BindView(R.id.last_update_time)
    public TextView lastUpdateTime;
    @BindView(R.id.vote_score)
    public TextView voteScore;
    @BindView(R.id.debater_name)
    public TextView debaterName;
    @BindView(R.id.zone)
    public TextView zone;

    public DebateViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      content.setOnTouchListener(new ClickableSpanTouchListener());
    }

    public void update(DebateViewModel debateViewModel) {
      final Context context = itemView.getContext();
      debaterName.setText(debateViewModel.getDebaterName());
      content.setText(KmarkProcessor.process(context, debateViewModel.getContent()));
      lastUpdateTime.setText(DateUtils.getRelativeTimeSpanString(debateViewModel.getLastUpdateTime()
          .getTime(), System.currentTimeMillis(), 0, DateUtils.FORMAT_ABBREV_RELATIVE));
      voteScore.setText(String.valueOf(debateViewModel.getVoteScore()));
      zone.setText(itemView.getContext().getString(R.string.zone_path, debateViewModel.getZone()));
    }

  }

  private final List<DebateViewModel> debates;

  private boolean hasNextPage;

  public LatestDebateListAdapter() {
    this.debates = new ArrayList<>();
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
    if (position >= debates.size()) {
      return R.layout.item_loading;
    }
    return R.layout.item_debate_latest;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (position >= debates.size()) {
      return;
    }
    DebateViewModel debateVm = debates.get(position);
    DebateViewHolder debateViewHolder = (DebateViewHolder) holder;
    debateViewHolder.update(debateVm);
  }

  @Override
  public int getItemCount() {
    return debates.size() + (hasNextPage ? 1 : 0);
  }

  public void refresh(List<DebateViewModel> debates) {
    this.debates.clear();
    this.debates.addAll(debates);
    hasNextPage = !debates.isEmpty();
    notifyDataSetChanged();
  }

  public void addAll(List<DebateViewModel> debates) {
    if (debates.isEmpty()) {
      hasNextPage = false;
      return;
    }
    this.debates.addAll(debates);
    notifyItemRangeInserted(this.debates.size() - debates.size(), debates.size());
  }

  public String getLastDebateId() {
    return debates.get(debates.size() - 1).getDebateId();
  }

  public DebateViewModel findItem(int position) {
    if (position >= debates.size()) {
      return null;
    }
    return debates.get(position);
  }
}
