package deep.entity;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Node {

    // SLF4J Logger
    private static final Logger LOG = LoggerFactory.getLogger(Node.class);

    private final List<Node> parents;
    private final List<Node> childs;
    private final int id;

    private boolean status;

    public Node(final int id) {
        this.id = id;
        this.status = true;
        this.parents = Lists.newArrayList();
        this.childs = Lists.newArrayList();
    }

    public Node setStatus(final boolean status) {
        this.status = status;
        return this;
    }

    public void addParent(final Node node) {
        this.parents.add(node);
    }

    public void addChild(final Node node) {
        this.childs.add(node);
    }

    public List<Node> getChilds() {
        return childs;
    }

    public int getId() {
        return id;
    }

    public boolean isUp() {
        return status;
    }

    public List<Node> getParents() {
        return parents;
    }

    @Override
    @SuppressWarnings ({"UnclearExpression", "ControlFlowStatementWithoutBraces", "SimplifiableIfStatement", "OverlyComplexBooleanExpression"})
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Node node = (Node) o;
        return Objects.equal(id, node.id);
    }

    @Override
    @SuppressWarnings ({"UnclearExpression", "ControlFlowStatementWithoutBraces", "SimplifiableIfStatement", "OverlyComplexBooleanExpression"})
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
