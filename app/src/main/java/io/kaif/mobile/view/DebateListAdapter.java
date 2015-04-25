package io.kaif.mobile.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.provider.Browser;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.kaif.mobile.R;
import io.kaif.mobile.kmark.KmarkProcessor;
import io.kaif.mobile.model.Article;
import io.kaif.mobile.model.Vote;
import io.kaif.mobile.view.daemon.ArticleDaemon;
import io.kaif.mobile.view.graphics.drawable.LevelDrawable;
import io.kaif.mobile.view.viewmodel.ArticleViewModel;
import io.kaif.mobile.view.viewmodel.DebateViewModel;
import io.kaif.mobile.view.widget.ArticleScoreTextView;
import io.kaif.mobile.view.widget.ClickableSpanTouchListener;
import io.kaif.mobile.view.widget.DebateActions;
import io.kaif.mobile.view.widget.OnVoteClickListener;
import io.kaif.mobile.view.widget.VoteArticleButton;

public class DebateListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  public interface OnReplyClickListener {
    void onReplyClicked(String parentDebateId, int newLevel);
  }

  static class ArticleViewHolder extends RecyclerView.ViewHolder {

    @InjectView(R.id.vote)
    public VoteArticleButton vote;
    @InjectView(R.id.vote_count)
    public ArticleScoreTextView voteCount;
    @InjectView(R.id.title)
    public TextView title;
    @InjectView(R.id.link)
    public TextView link;
    @InjectView(R.id.zone)
    public TextView zone;
    @InjectView(R.id.debate_count)
    public TextView debateCount;
    @InjectView(R.id.reply)
    public ImageButton replyButton;
    @InjectView(R.id.self_content)
    public TextView content;
    @InjectView(R.id.author_name)
    public TextView authorName;

    private ArticleViewModel articleViewModel;

    public ArticleViewHolder(View itemView) {
      super(itemView);
      ButterKnife.inject(this, itemView);
      content.setOnTouchListener(new ClickableSpanTouchListener());
    }

    @OnClick(R.id.title)
    public void onClickTitle() {
      if (articleViewModel.getArticleType() == Article.ArticleType.EXTERNAL_LINK) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
            Uri.parse(StringEscapeUtils.unescapeHtml4(articleViewModel.getLink())));
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, itemView.getContext().getPackageName());
        itemView.getContext().startActivity(intent);
      }
    }

    public void update(ArticleViewModel articleVm) {
      this.articleViewModel = articleVm;
      final Context context = itemView.getContext();
      vote.updateVoteState(articleVm.getCurrentVoeState());
      voteCount.update(articleVm.getScore(), articleVm.getCurrentVoeState());
      voteCount.setOnClickListener(v -> vote.performClick());
      debateCount.setText(context.getString(R.string.debate_count, articleVm.getDebateCount()));
      zone.setText(articleVm.getZoneTitle());
      title.setText(Html.fromHtml(articleVm.getTitle()));
      authorName.setText(articleVm.getAuthorName());
      if (articleVm.getArticleType() == Article.ArticleType.EXTERNAL_LINK) {
        link.setText("(" + Uri.parse(articleVm.getLink()).getAuthority() + ")");
        content.setVisibility(View.GONE);
        content.setText(null);
      } else {
        link.setText("("
            + itemView.getContext().getString(R.string.zone_path, articleVm.getZone())
            + ")");
        content.setVisibility(View.VISIBLE);
        content.setText(KmarkProcessor.process(context, articleVm.getContent()));
      }
    }

    void setOnReplyClickListener(View.OnClickListener onClickListener) {
      replyButton.setOnClickListener(view -> {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(),
            R.anim.scale_action_icon);
        view.startAnimation(animation);
        onClickListener.onClick(view);
      });
    }

    void setOnVoteClickListener(OnVoteClickListener onVoteClickListener) {
      vote.setOnVoteClickListener(onVoteClickListener);
    }

    public void showVoteEffect() {
      vote.showVoteColor(true);
      voteCount.showVoteColor(true);
    }
  }

  static class DebateViewHolder extends RecyclerView.ViewHolder {

    @InjectView(R.id.content)
    public TextView content;
    @InjectView(R.id.last_update_time)
    public TextView lastUpdateTime;
    @InjectView(R.id.vote_score)
    public TextView voteScore;
    @InjectView(R.id.debater_name)
    public TextView debaterName;
    @InjectView(R.id.debate_actions)
    public DebateActions debateActions;

    public DebateViewHolder(View itemView) {
      super(itemView);
      ButterKnife.inject(this, itemView);
      content.setOnTouchListener(new ClickableSpanTouchListener());
    }

    public void update(DebateViewModel debateViewModel, boolean showActions) {
      final Context context = itemView.getContext();
      itemView.setBackground(createDebateDrawable(debateViewModel, context));
      debaterName.setText(debateViewModel.getDebaterName());
      content.setText(KmarkProcessor.process(context, debateViewModel.getContent()));
      lastUpdateTime.setText(DateUtils.getRelativeTimeSpanString(debateViewModel.getLastUpdateTime()
          .getTime(), System.currentTimeMillis(), 0, DateUtils.FORMAT_ABBREV_RELATIVE));
      voteScore.setText(String.valueOf(debateViewModel.getVoteScore()));
      debateActions.updateVoteState(debateViewModel.getCurrentVoeState());
      if (showActions) {
        itemView.setActivated(true);
        debateActions.setVisibility(View.VISIBLE);
      } else {
        itemView.setActivated(false);
        debateActions.setVisibility(View.GONE);
      }
    }

    void setOnVoteClickListener(OnVoteClickListener onVoteClickListener) {
      debateActions.setOnVoteClickListener(onVoteClickListener);
    }

    void setOnReplyClickListener(View.OnClickListener onClickListener) {
      debateActions.setOnReplyClickListener(onClickListener);
    }

    private Drawable createDebateDrawable(DebateViewModel debateViewModel, Context context) {
      StateListDrawable stateListDrawable = new StateListDrawable();
      stateListDrawable.addState(new int[] { android.R.attr.state_activated },
          new LevelDrawable(context,
              debateViewModel.getLevel(),
              context.getResources().getColor(R.color.kaif_selected_blue)));
      stateListDrawable.addState(StateSet.WILD_CARD,
          new LevelDrawable(context, debateViewModel.getLevel(), Color.TRANSPARENT));
      return stateListDrawable;
    }

    public void showVoteEffect(Vote.VoteState from) {
      debateActions.playAnimations(from);
    }
  }

  private final List<DebateViewModel> debates;
  private final ArticleViewModel article;
  private final ArticleDaemon articleDaemon;
  private int selectedPosition = RecyclerView.NO_POSITION;
  private final OnReplyClickListener onReplyClickListener;

  public DebateListAdapter(ArticleViewModel article,
      ArticleDaemon articleDaemon,
      OnReplyClickListener onReplyClickListener) {
    this.onReplyClickListener = onReplyClickListener;
    this.debates = new ArrayList<>();
    this.article = article;
    this.articleDaemon = articleDaemon;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == R.layout.item_article_full) {
      final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
      return new ArticleViewHolder(view);
    }
    final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
    return new DebateViewHolder(view);
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return R.layout.item_article_full;
    }
    return R.layout.item_debate;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (position == 0) {
      ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;
      articleViewHolder.update(article);
      articleViewHolder.setOnReplyClickListener(v -> onReplyClickListener.onReplyClicked(null, 1));
      articleViewHolder.setOnVoteClickListener((from,
          to) -> articleDaemon.voteArticle(article.getArticleId(), from, to));
      return;
    }
    DebateViewModel debateVm = debates.get(position - 1);
    DebateViewHolder debateViewHolder = (DebateViewHolder) holder;
    debateViewHolder.update(debateVm, position == selectedPosition);
    debateViewHolder.setOnReplyClickListener(v -> onReplyClickListener.onReplyClicked(debateVm.getDebateId(),
        debateVm.getLevel() + 1));
    debateViewHolder.setOnVoteClickListener((from,
        to) -> articleDaemon.voteDebate(debateVm.getDebateId(), from, to));
    holder.itemView.setOnClickListener(v -> selectItem(holder.getAdapterPosition()));
  }

  @Override
  public int getItemCount() {
    return debates.size() + 1;
  }

  public void refresh(List<DebateViewModel> debates) {
    clearSelection();
    this.debates.clear();
    this.debates.addAll(debates);
    notifyDataSetChanged();
  }

  public void clearSelection() {
    int prev = selectedPosition;
    selectedPosition = RecyclerView.NO_POSITION;
    notifyItemChanged(prev);
  }

  public void selectItem(int position) {
    int prev = selectedPosition;
    selectedPosition = position;
    notifyItemChanged(prev);
    notifyItemChanged(position);
  }

  @Override
  public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
    if (holder instanceof DebateViewHolder) {
      int position = holder.getAdapterPosition();
      DebateViewModel debateVm = debates.get(position - 1);
      if (debateVm.shouldShowVoteEffect()) {
        debateVm.setCanShowVoteAnimation(false);
        ((DebateViewHolder) holder).showVoteEffect(debateVm.getPrevVoteState());
      }
      return;
    }
    if (article.shouldShowVoteEffect()) {
      article.setCanShowVoteAnimation(false);
      ((ArticleViewHolder) holder).showVoteEffect();
    }
  }

  public void updateArticleVote(String articleId, Vote.VoteState voteState) {
    if (article.getArticleId().equals(articleId)) {
      article.updateVoteState(voteState);
      notifyItemChanged(0);
    }
  }

  public void updateDebateVote(String debateId, Vote.VoteState voteState) {
    for (int i = 0; i < debates.size(); i++) {
      DebateViewModel debateVm = debates.get(i);
      if (debateVm.getDebateId().equals(debateId)) {
        debateVm.updateVoteState(voteState);
        notifyItemChanged(i + 1);
        break;
      }
    }
  }
}
