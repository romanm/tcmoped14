package org.tasclin1.mopet.regime;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.joda.time.MutableDateTime;
import org.springframework.ui.Model;
import org.tasclin1.mopet.domain.App;
import org.tasclin1.mopet.domain.Times;
import org.tasclin1.mopet.domain.Tree;
import org.tasclin1.mopet.service.MopetService;

public class TaskRun implements Serializable, Comparable<TaskRun> {
    private static final long serialVersionUID = 1L;
    private Integer defDay, calcDay;

    public Integer getDefDay() {
	return defDay;
    }

    private MutableDateTime begin, end;

    public MutableDateTime getEnd() {
	return end;
    }

    private Tree timesT;

    public Tree getTimesT() {
	return timesT;
    }

    public TaskRun(Tree timesT, Integer dayNr, Model model) {
	this.timesT = timesT;
	defDay = dayNr;
	MutableDateTime mutableDateTime = new MutableDateTime();
	mutableDateTime.addDays(dayNr - 1);
	mutableDateTime.setHourOfDay(10);
	mutableDateTime.setMinuteOfHour(0);
	mutableDateTime.setSecondOfMinute(0);
	mutableDateTime.setMillisOfSecond(100);
	// correctur begin
	Map<Long, TaskRun> regimeTaskRuns = (Map<Long, TaskRun>) model.asMap().get(MopetService.regimeTaskRuns);
	while (regimeTaskRuns.containsKey(mutableDateTime.getMillis())) {
	    mutableDateTime.addMillis(-1);
	}
	begin = mutableDateTime;
	regimeTaskRuns.put(begin.getMillis(), this);
	// correctur begin END
	Integer durationValueSecond = getAppDurationSecond();
	end = endFromBegin().secondOfDay().add(durationValueSecond);

	modelThis(model);

    }

    private void modelThis(Model model) {
	timesT.getTaskRuns().add(this);
	Map<Integer, Set<TaskRun>> dayNrTaskRuns = (Map<Integer, Set<TaskRun>>) model.asMap().get(
		MopetService.dayNrTaskRuns);
	if (!dayNrTaskRuns.containsKey(defDay))
	    dayNrTaskRuns.put(defDay, new ConcurrentSkipListSet<TaskRun>());
	dayNrTaskRuns.get(defDay).add(this);
    }

    // apporder
    // 0 -↘↑- beginAfterEnd
    // 1 -↑↖- beginBeforeBegin
    // 2 -↘↓- endAfterEnd
    // 3 -↓↖- endBeforeBegin
    public TaskRun(Tree timesT, TaskRun refTaskRun, Model model) {
	this.timesT = timesT;
	defDay = refTaskRun.getDefDay();

	Integer durationValueSecond = getAppDurationSecond();
	Times refTimesO = timesT.getTimesO();

	Integer relValueSecond = refTimesO.getRelvalue();
	String relUnit = refTimesO.getRelunit();
	if ("M".equals(relUnit))
	    relValueSecond *= 60;
	else if ("H".equals(relUnit))
	    relValueSecond *= 60 * 60;

	String apporder = refTimesO.getApporder();
	if ("0".equals(apporder)) {
	    begin = refTaskRun.getEnd().copy().secondOfDay().add(relValueSecond);
	    end = endFromBegin().secondOfDay().add(durationValueSecond);
	} else if ("1".equals(apporder)) {
	    begin = refTaskRun.getBegin().copy().secondOfDay().add(0 - relValueSecond);
	    end = endFromBegin().secondOfDay().add(durationValueSecond);
	} else if ("2".equals(apporder)) {
	    end = refTaskRun.getEnd().copy().secondOfDay().add(relValueSecond);
	    begin = beginFromEnd().secondOfDay().add(0 - durationValueSecond);
	} else if ("3".equals(apporder)) {
	    end = refTaskRun.getBegin().copy().secondOfDay().add(0 - relValueSecond);
	    begin = beginFromEnd().secondOfDay().add(0 - durationValueSecond);
	}

	modelThis(model);

    }

    private Integer getAppDurationSecond() {
	Tree drugAppT = timesT.getParentT().getParentT().getDrugAppT();
	Integer durationValueSecond = 0;
	if (null != drugAppT) {
	    App appO = drugAppT.getAppO();
	    durationValueSecond = appO.getDurationValue();
	    if ("min".equals(appO.getUnit()))
		durationValueSecond *= 60;
	    if ("h".equals(appO.getUnit()))
		durationValueSecond *= 60 * 60;
	    if ("d".equals(appO.getUnit()))
		durationValueSecond *= 60 * 60 * 24;
	}
	return durationValueSecond;
    }

    private MutableDateTime endFromBegin() {
	return begin.copy().millisOfSecond().add(10);
    }

    private MutableDateTime beginFromEnd() {
	return end.copy().millisOfSecond().add(0 - 10);
    }

    public MutableDateTime getBegin() {
	return begin;
    }

    public int compareTo(TaskRun o) {
	return (int) (getBegin().getMillis() - o.getBegin().getMillis());
    }

}
