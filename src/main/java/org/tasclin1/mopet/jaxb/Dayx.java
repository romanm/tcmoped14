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
	abs = dayO.getAbs();
	newtype = dayO.getNewtype();
    }

    Timesx times;

    public Timesx getTimes() {
	return times;
    }

    public void setTimes(Timesx times) {
	this.times = times;
    }

    private String abs, newtype;

    @XmlAttribute
    public String getAbs() {
	return abs;
    }

    @XmlAttribute
    public String getNewtype() {
	return newtype;
    }
}
