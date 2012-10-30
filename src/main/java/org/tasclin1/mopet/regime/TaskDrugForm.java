package org.tasclin1.mopet.regime;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tasclin1.mopet.domain.Day;
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

    Set<Integer> absset;

    public Set<Integer> getAbsset() {
	return absset;
    }

    public void setAbsset(Set<Integer> absset) {
	this.absset = absset;
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

    public void initAbs() {
	initTargetDayT();
	log.debug("---------------" + absset);
	if (null == absset) {
	    Day dayO = targetT.getDayO();
	    absset = dayO.initAbsSet();
	}
    }

    private void initTargetDayT() {
	if (!targetT.isDay()) {
	    targetT = drugT.getDrugDayT(0);
	}
    }

    public void initPeriod() {
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
