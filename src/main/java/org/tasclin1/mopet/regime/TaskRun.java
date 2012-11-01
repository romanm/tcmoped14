package org.tasclin1.mopet.regime;

import java.io.Serializable;
import java.util.Map;

import org.joda.time.MutableDateTime;
import org.joda.time.base.BaseDateTime;
import org.springframework.ui.Model;
import org.tasclin1.mopet.domain.Tree;
import org.tasclin1.mopet.service.MopetService;

public class TaskRun implements Serializable, Comparable<TaskRun> {
    private static final long serialVersionUID = 1L;
    private Integer defDay, calcDay;

    public Integer getDefDay() {
	return defDay;
    }

    private BaseDateTime begin, end;
    private Tree timesT;

    public Tree getTimesT() {
	return timesT;
    }

    public TaskRun(Tree timesT, Integer dayNr, Model model) {
	defDay = dayNr;
	this.timesT = timesT;
	Map<Long, TaskRun> regimeTaskRuns = (Map<Long, TaskRun>) model.asMap().get(MopetService.regimeTaskRuns);
	MutableDateTime mutableDateTime = new MutableDateTime();
	mutableDateTime.addDays(dayNr - 1);
	mutableDateTime.setHourOfDay(10);
	mutableDateTime.setMinuteOfHour(0);
	mutableDateTime.setSecondOfMinute(0);
	mutableDateTime.setMillisOfSecond(100);
	// correctur begin
	while (regimeTaskRuns.containsKey(mutableDateTime.getMillis())) {
	    mutableDateTime.addMillis(-1);
	}
	// correctur begin END
	begin = mutableDateTime;
	MutableDateTime clone = (MutableDateTime) mutableDateTime.clone();
	clone.addMillis(10);
	end = clone;
	regimeTaskRuns.put(mutableDateTime.getMillis(), this);
    }

    public BaseDateTime getBegin() {
	return begin;
    }

    public void setBegin(BaseDateTime begin) {
	this.begin = begin;
    }

    public int compareTo(TaskRun o) {
	return (int) (getBegin().getMillis() - o.getBegin().getMillis());
    }

}
