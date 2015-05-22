package io.kaif.mobile.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.kaif.mobile.R;
import io.kaif.mobile.model.Article;
import io.kaif.mobile.model.Vote;
import io.kaif.mobile.view.daemon.ArticleDaemon;
import io.kaif.mobile.view.daemon.VoteDaemon;
import io.kaif.mobile.view.viewmodel.ArticleViewModel;
import io.kaif.mobile.view.widget.ArticleScoreTextView;
import io.kaif.mobile.view.widget.VoteArticleButton;

public class ArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public interface OnItemClickListener {
    void onItemClick(ArticleViewModel item);
  }

  public interface OnItemVoteClickListener {
    void onItemVoteClick(View view, Vote.VoteState from, Vote.VoteState to);
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
    @InjectView(R.id.author_name)
    public TextView authorName;

    public ArticleViewHolder(View itemView) {
      super(itemView);
      ButterKnife.inject(this, itemView);
    }

    public void update(ArticleViewModel article) {
      final Context context = itemView.getContext();
      vote.updateVoteState(article.getCurrentVoeState());
      voteCount.update(article.getScore(), article.getCurrentVoeState());
      voteCount.setOnClickListener(v -> vote.performClick());
      title.setText(Html.fromHtml(article.getTitle()));
      debateCount.setText(context.getString(R.string.debate_count, article.getDebateCount()));
      zone.setText(article.getZoneTitle());
      authorName.setText(article.getAuthorName());
      if (article.getArticleType() == Article.ArticleType.EXTERNAL_LINK) {
        link.setText("(" + Uri.parse(article.getLink()).getAuthority() + ")");
      } else {
        link.setText("("
            + itemView.getContext().getString(R.string.zone_path, article.getZone())
            + ")");
      }
    }

    void setOnItemVoteClickListener(OnItemVoteClickListener onItemVoteClickListener) {
      vote.setOnVoteClickListener((from, to) -> onItemVoteClickListener.onItemVoteClick(itemView,
          from,
          to));
    }

    public void showVoteEffect() {
      vote.showVoteColor(true);
      voteCount.showVoteColor(true);
    }
  }

  private final List<ArticleViewModel> articles;

  private ArticleDaemon articleDaemon;
  private VoteDaemon voteDaemon;
  private final OnItemClickListener onItemClickListener;

  private boolean hasNextPage;

  public ArticleListAdapter(ArticleDaemon articleDaemon,
      VoteDaemon voteDaemon,
      OnItemClickListener onItemClickListener) {
    this.articleDaemon = articleDaemon;
    this.voteDaemon = voteDaemon;
    this.onItemClickListener = onItemClickListener;
    this.articles = new ArrayList<>();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    if (viewType == R.layout.item_loading) {
      return new RecyclerView.ViewHolder(LayoutInflater.from(viewGroup.getContext())
          .inflate(viewType, viewGroup, false)) {
      };
    }

    final ArticleViewHolder viewHolder = new ArticleViewHolder(LayoutInflater.from(viewGroup.getContext())
        .inflate(viewType, viewGroup, false));

    viewHolder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick((ArticleViewModel) v
        .getTag()));
    viewHolder.setOnItemVoteClickListener((v,
        from,
        to) -> voteDaemon.voteArticle(((ArticleViewModel) v.getTag()).getArticleId(), from, to));
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    if (isLoadingView(position)) {
      return;
    }
    final ArticleViewModel articleViewModel = getItemAtPosition(position);
    ArticleViewHolder articleViewHolder = (ArticleViewHolder) viewHolder;
    articleViewHolder.update(articleViewModel);
    articleViewHolder.itemView.setTag(articleViewModel);
  }

  @Override
  public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
    int position = holder.getAdapterPosition();
    if (isLoadingView(position)) {
      return;
    }

    ArticleViewModel articleViewModel = getItemAtPosition(position);
    if (articleViewModel.shouldShowVoteEffect()) {
      articleViewModel.setCanShowVoteAnimation(false);
      ((ArticleViewHolder) holder).showVoteEffect();
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (isLoadingView(position)) {
      return R.layout.item_loading;
    }
    return R.layout.item_article;
  }

  private boolean isLoadingView(int position) {
    return hasNextPage && position == articles.size();
  }

  private ArticleViewModel getItemAtPosition(int position) {
    return articles.get(position);
  }

  public String getLastArticleId() {
    return this.articles.get(this.articles.size() - 1).getArticleId();
  }

  @Override
  public int getItemCount() {
    return articles.size() + (hasNextPage ? 1 : 0);
  }

  public void refresh(List<ArticleViewModel> articles) {
    this.articles.clear();
    this.articles.addAll(articles);
    hasNextPage = !articles.isEmpty();
    notifyDataSetChanged();
  }

  public void addAll(List<ArticleViewModel> articles) {
    if (articles.isEmpty()) {
      hasNextPage = false;
      return;
    }
    this.articles.addAll(articles);
    notifyItemRangeInserted(this.articles.size() - articles.size(), articles.size());
  }

  public void updateVote(String articleId, Vote.VoteState voteState) {
    for (int i = 0; i < articles.size(); i++) {
      ArticleViewModel article = articles.get(i);
      if (article.getArticleId().equals(articleId)) {
        article.updateVoteState(voteState);
        notifyItemChanged(i);
        break;
      }
    }
  }
}
