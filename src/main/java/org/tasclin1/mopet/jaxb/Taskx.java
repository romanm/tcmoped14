package org.tasclin1.mopet.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "task")
public class Taskx extends Treex {
    ArrayList<Drugx> drug;

}
