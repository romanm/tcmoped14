package org.tasclin1.mopet.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.tasclin1.mopet.domain.Day;
import org.tasclin1.mopet.domain.Tree;

@XmlRootElement
public class Dayx extends Treex {

    public Dayx() {
    }

    public Dayx(Tree dayT) {
	super(dayT);
	Day dayO = dayT.getDayO();
	if (null != dayO) {
	    abs = dayO.getAbs();
	    newtype = dayO.getNewtype();
	}
    }

    Timesx times;

    public Timesx getTimes() {
	return times;
    }

    public void setTimes(Timesx times) {
	this.times = times;
    }

    private String abs, newtype;

    public void setNewtype(String newtype) {
	this.newtype = newtype;
    }

    public void setAbs(String abs) {
	this.abs = abs;
    }

    @XmlAttribute
    public String getAbs() {
	return abs;
    }

    @XmlAttribute
    public String getNewtype() {
	return newtype;
    }
}
