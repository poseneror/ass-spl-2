
package bgu.spl.a2.sim.json;

import java.util.List;

import bgu.spl.a2.sim.Computer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JsonInput {

    @SerializedName("threads")
    @Expose
    private Integer threads;
    @SerializedName("Computers")
    @Expose
    private List<JsonComputer> computers = null;
    @SerializedName("Phase 1")
    @Expose
    private List<JsonAction> phase1 = null;
    @SerializedName("Phase 2")
    @Expose
    private List<JsonAction> phase2 = null;
    @SerializedName("Phase 3")
    @Expose
    private List<JsonAction> phase3 = null;

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public List<JsonComputer> getComputers() {
        return computers;
    }

    public void setComputers(List<JsonComputer> computers) {
        this.computers = computers;
    }

    public List<JsonAction> getPhase1() { return phase1; }

    public void setPhase1(List<JsonAction> phase1) {
        this.phase1 = phase1;
    }

    public List<JsonAction> getPhase2() {
        return phase2;
    }

    public void setPhase2(List<JsonAction> phase2) {
        this.phase2 = phase2;
    }

    public List<JsonAction> getPhase3() {
        return phase3;
    }

    public void setPhase3(List<JsonAction> phase3) {
        this.phase3 = phase3;
    }

}
