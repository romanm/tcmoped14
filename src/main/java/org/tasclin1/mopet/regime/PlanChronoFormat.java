package org.tasclin1.mopet.regime;

import java.util.ArrayList;
import java.util.List;

public class PlanChronoFormat {
    private List<Plancolumns> plancolumns;

    public List<Plancolumns> getPlancolumns() {
	return plancolumns;
    }

    public void setPlancolumns(List<Plancolumns> plancolumns) {
	this.plancolumns = plancolumns;
    }

    public PlanChronoFormat() {
	plancolumns = new ArrayList<Plancolumns>();
	plancolumns.add(Plancolumns.times);
	plancolumns.add(Plancolumns.drug);
	plancolumns.add(Plancolumns.dose);
	plancolumns.add(Plancolumns.appDuration);
	plancolumns.add(Plancolumns.doseHour);
	plancolumns.add(Plancolumns.timesNumer);
    }
}
