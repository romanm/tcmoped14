package org.tasclin1.mopet.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.tasclin1.mopet.domain.Tree;

@XmlRootElement
public class Dosex extends Treex {

    public Dosex() {

    }

    public Dosex(Tree doseT) {
	super(doseT);
	value = doseT.getDoseO().getValue();
	unit = doseT.getDoseO().getUnit();
	app = doseT.getDoseO().getApp();
	pro = doseT.getDoseO().getPro();
	type = doseT.getDoseO().getType();
    }

    @XmlAttribute
    public Float getValue() {
	return value;
    }

    @XmlAttribute
    public String getUnit() {
	return unit;
    }

    @XmlAttribute
    public String getApp() {
	return app;
    }

    @XmlAttribute
    public String getPro() {
	return pro;
    }

    @XmlAttribute
    public String getType() {
	return type;
    }

    private Float value;

    public void setValue(Float value) {
	this.value = value;
    }

    private String unit, app, pro, type;

    public void setType(String type) {
	this.type = type;
    }

    public void setPro(String pro) {
	this.pro = pro;
    }

    public void setApp(String app) {
	this.app = app;
    }

    public void setUnit(String unit) {
	this.unit = unit;
    }

}
