package org.tasclin1.mopet.jaxb;

import java.util.ArrayList;

import org.tasclin1.mopet.domain.Tree;

public class TaskOnex extends Treex {

    ArrayList<Dayx> day;

    public TaskOnex() {
    }

    public TaskOnex(Tree tree) {
	super(tree);
    }

    public ArrayList<Dayx> getDay() {
	if (null == day)
	    day = new ArrayList<Dayx>();
	return day;
    }

    public void setDay(ArrayList<Dayx> day) {
	this.day = day;
    }

}
