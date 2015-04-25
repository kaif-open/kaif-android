package io.kaif.mobile.model;

import java.util.List;

public class DebateNode {

  private final Debate debate;

  private final List<DebateNode> children;

  public DebateNode(Debate debate, List<DebateNode> children) {
    this.debate = debate;
    this.children = children;
  }

  public Debate getDebate() {
    return debate;
  }

  public List<DebateNode> getChildren() {
    return children;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DebateNode that = (DebateNode) o;

    if (debate != null ? !debate.equals(that.debate) : that.debate != null) {
      return false;
    }
    return children.equals(that.children);

  }

  @Override
  public int hashCode() {
    int result = debate != null ? debate.hashCode() : 0;
    result = 31 * result + children.hashCode();
    return result;
  }
}
