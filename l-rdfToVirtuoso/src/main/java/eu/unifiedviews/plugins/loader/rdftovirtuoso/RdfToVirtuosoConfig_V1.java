package eu.unifiedviews.plugins.loader.rdftovirtuoso;

public class RdfToVirtuosoConfig_V1 {

    private String virtuosoUrl = "jdbc:virtuoso://localhost:1111/charset=UTF-8/";

    private String username = "dba";

    private String password = "dba";

    private boolean clearDestinationGraph = false;

    private String targetGraphName = "";

    private int threadCount = 1;

    private boolean skipOnError = false;

    private int commitSize = 100000;

    public RdfToVirtuosoConfig_V1() {
    }

    public String getVirtuosoUrl() {
        return virtuosoUrl;
    }

    public void setVirtuosoUrl(String virtuosoUrl) {
        this.virtuosoUrl = virtuosoUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isClearDestinationGraph() {
        return clearDestinationGraph;
    }

    public void setClearDestinationGraph(boolean clearDestinationGraph) {
        this.clearDestinationGraph = clearDestinationGraph;
    }

    public String getTargetGraphName() {
        return targetGraphName;
    }

    public void setTargetGraphName(String targetGraphName) {
        this.targetGraphName = targetGraphName;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public boolean isSkipOnError() {
        return skipOnError;
    }

    public void setSkipOnError(boolean skipOnError) {
        this.skipOnError = skipOnError;
    }

    public int getCommitSize() {
        return commitSize;
    }

    public void setCommitSize(int commitSize) {
        this.commitSize = commitSize;
    }
}
