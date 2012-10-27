package org.tasclin1.mopet.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.tasclin1.mopet.domain.Tree;

@XmlRootElement(name = "drug")
public class Drugx extends Treex {
    Dosex dose;

    public void setDose(Dosex dose) {
	this.dose = dose;
    }

    ArrayList<Dayx> day;

    public ArrayList<Dayx> getDay() {
	if (null == day)
	    day = new ArrayList<Dayx>();
	return day;
    }

    public void setDay(ArrayList<Dayx> day) {
	this.day = day;
    }

    public Dosex getDose() {
	return dose;
    }

    @XmlAttribute
    public String getDrug() {
	return drug;
    }

    String drug;

    public void setDrug(String drug) {
	this.drug = drug;
    }

    public Drugx() {
    }

    public Drugx(int id, int idclass, String drug) {
	this.id = id;
	this.idclass = idclass;
	this.drug = drug;
    }

    public Drugx(Tree tree) {
	super(tree);
	drug = tree.getDrugO().getDrug();
    }
}
