package deep.generator;

public interface OutageListener  {

    void onOutage(final int outage);
    void onSolve(final int outage);
}
