package org.tasclin1.mopet.regime;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tasclin1.mopet.domain.Day;
import org.tasclin1.mopet.domain.Times;
import org.tasclin1.mopet.domain.Tree;

public class TaskDrugForm implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final Log log = LogFactory.getLog(getClass());
    private Tree drugT;
    private Integer idt;
    private Tree targetT;

    public void initTaskDrugDose() {
	log.debug("---------------");
	setIdt(drugT.getDrugDoseT().getId());
	log.debug("---------------");
    }

    public void initTaskDrug() {
	setIdt(drugT.getId());
    }

    public Tree getTargetT() {
	return targetT;
    }

    public Integer getIdt() {
	return idt;
    }

    public void setIdt(Integer idt) {
	this.idt = idt;
	setTargetT();
    }

    /**
     * Find target tree equals idt value.
     */
    private void setTargetT() {
	if (idt.equals(drugT.getId()))
	    targetT = drugT;
	else
	    for (Tree t1 : drugT.getChildTs())
		if (idt.equals(t1.getId())) {
		    targetT = t1;
		    break;
		} else
		    for (Tree t2 : t1.getChildTs())
			if (idt.equals(t2.getId())) {
			    targetT = t2;
			    break;
			} else
			    for (Tree t3 : t2.getChildTs())
				if (idt.equals(t3.getId())) {
				    targetT = t3;
				    break;
				}
    }

    public TaskDrugForm(Tree drugT, Integer idt) {
	this.drugT = drugT;
	setIdt(idt);
    }

    public String toString() {
	String string = "day::type=" + type + ":fromday=" + fromday + ":totheday=" + totheday + ":absset=" + absset
		+ ":-:idt=" + idt;
	// log.debug(string);
	return string;
    };

    /**
     * Set of days.
     */
    Set<Integer> absset;

    public Set<Integer> getAbsset() {
	return absset;
    }

    public void setAbsset(Set<Integer> absset) {
	this.absset = absset;
    }

    /**
     * Set of hour.
     */
    Set<Integer> absHourSet;

    public Set<Integer> getAbsHourSet() {
	return absHourSet;
    }

    public void setAbsHourSet(Set<Integer> absHourSet) {
	this.absHourSet = absHourSet;
    }

    String type;

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    Integer fromday, totheday;

    public Integer getFromday() {
	return fromday;
    }

    public void setFromday(Integer fromday) {
	this.fromday = fromday;
    }

    public Integer getTotheday() {
	return totheday;
    }

    public void setTotheday(Integer totheday) {
	this.totheday = totheday;
    }

    public String actionStateTimes() {
	initTargetTimesT();
	if (null == edTimes) {
	    edTimes = "edTimesAbs";
	    Times timesO = targetT.getTimesO();
	    if (null != timesO) {
		if (null != targetT.getRef()) {
		    edTimes = "edTimesRelative";
		} else {
		    String abs = timesO.getAbs();
		    if (abs.contains("=")) {
			edTimes = "edTimesMeal";
		    } else if (abs.contains(",")) {
			edTimes = "edTimesAbs";
		    }
		}
	    }
	}
	return edTimes;
    }

    String edDay, edTimes;

    public String actionStateDay() {
	initTargetDayT();
	if (null == edDay) {
	    edDay = "edDayAbs";
	    Day dayO = targetT.getDayO();
	    if (null != dayO) {
		String newtype = dayO.getNewtype();
		if ("a".equals(newtype))
		    edDay = "edDayAbs";
		else if ("p".equals(newtype))
		    edDay = "edDayPeriod";
	    }
	}
	return edDay;
    }

    public void initHourAbs() {
	edTimes = "edTimesAbs";
	if (null == absHourSet) {
	    log.debug(targetT);
	    Times timesO = targetT.getTimesO();
	    if (null != timesO) {
		absHourSet = timesO.getAbsSet();
	    } else {
		absHourSet = new HashSet();
	    }
	}
    }

    public void initAbs() {
	edDay = "edDayAbs";
	// initTargetDayT();
	log.debug("---------------" + absset);
	if (null == absset) {
	    Day dayO = targetT.getDayO();
	    if (null != dayO)
		absset = dayO.getAbsSet();
	    else
		absset = new HashSet<Integer>();
	}
    }

    private void initTargetTimesT() {
	if (!targetT.isTimes()) {
	    if (targetT.isDay())
		targetT = targetT.getDrugDayTimesT(0);
	    else {
		Tree dayT = drugT.getDrugDayT(0);
		targetT = dayT.getDrugDayTimesT(0);
	    }
	    idt = targetT.getId();
	}

    }

    private void initTargetDayT() {
	if (!targetT.isDay()) {
	    targetT = drugT.getDrugDayT(0);
	}
    }

    public void initPeriod() {
	edDay = "edDayPeriod";
	if (null == getFromday()) {
	    if (targetT.isMtlDayO() && "p".equals(targetT.getDayO().getNewtype())) {
		String abs = targetT.getDayO().getAbs();
		String[] split = abs.split("-");
		int parseInt = Integer.parseInt(split[0]);
		int parseInt2 = Integer.parseInt(split[1]);
		setFromday(parseInt);
		setTotheday(parseInt2);
	    }
	}
    }
}
