package org.tasclin1.mopet.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.tasclin1.mopet.domain.Tree;

@XmlRootElement(name = "task")
public class Taskx extends Treex {
    public Taskx() {

    }

    public Taskx(Tree tree) {
	super(tree);
	taskName = tree.getTaskO().getTask();
    }

    private String taskName, taskvar, type;

    @XmlAttribute
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    @XmlAttribute
    public String getTaskvar() {
	return taskvar;
    }

    public void setTaskvar(String taskvar) {
	this.taskvar = taskvar;
    }

    @XmlAttribute
    public String getTaskName() {
	return taskName;
    }

    public void setTaskName(String taskName) {
	this.taskName = taskName;
    }

    ArrayList<Taskx> task;

    public ArrayList<Taskx> getTask() {
	if (null == task)
	    task = new ArrayList<Taskx>();
	return task;
    }

    public void setTask(ArrayList<Taskx> task) {
	this.task = task;
    }

    ArrayList<Drugx> drug;

    public ArrayList<Drugx> getDrug() {
	if (null == drug)
	    drug = new ArrayList<Drugx>();
	return drug;
    }

    public void setDrug(ArrayList<Drugx> drug) {
	this.drug = drug;
    }

}
