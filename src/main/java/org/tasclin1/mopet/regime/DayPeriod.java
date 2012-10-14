package org.tasclin1.mopet.regime;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DayPeriod implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final Log log = LogFactory.getLog(getClass());

    public String toString() {
	String string = "day::type=" + type + ":fromday=" + fromday + ":totheday=" + totheday + ":absset=" + absset;
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
	log.debug("-------------------");
	if (null == getAbsset()) {
	    absset = new HashSet<Integer>();
	    absset.add(1);
	    absset.add(3);
	    absset.add(6);
	    absset.add(-2);
	    log.debug("-------------------");
	}
    }

    public void initPeriod() {
	log.debug("-------------------");
	if (null == getFromday()) {
	    setFromday(3);
	    setTotheday(7);
	    log.debug("-------------------");
	}
    }
}
