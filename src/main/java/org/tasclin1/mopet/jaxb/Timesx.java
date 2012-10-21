package org.tasclin1.mopet.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.tasclin1.mopet.domain.Times;
import org.tasclin1.mopet.domain.Tree;

@XmlRootElement
public class Timesx extends Treex {

    public Timesx() {
    }

    public Timesx(Tree timesT) {
	super(timesT);
	Times timesO = timesT.getTimesO();
	if (null != timesO) {
	    abs = timesO.getAbs();
	    apporder = timesO.getApporder();
	    relunit = timesO.getRelunit();
	    relvalue = timesO.getRelvalue();
	    visual = timesO.getVisual();
	}
    }

    private String abs, apporder, relunit;
    private Integer relvalue, visual;

    @XmlAttribute
    public String getAbs() {
	return abs;
    }

    @XmlAttribute
    public String getApporder() {
	return apporder;
    }

    @XmlAttribute
    public String getRelunit() {
	return relunit;
    }

    @XmlAttribute
    public Integer getRelvalue() {
	return relvalue;
    }

    @XmlAttribute
    public Integer getVisual() {
	return visual;
    }

}
