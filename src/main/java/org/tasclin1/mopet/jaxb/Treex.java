package org.tasclin1.mopet.jaxb;

import javax.xml.bind.annotation.XmlAttribute;

import org.tasclin1.mopet.domain.Tree;

public class Treex {
    Integer did, id, idclass;

    public void setIdclass(Integer idclass) {
	this.idclass = idclass;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    private Tree tree;
    private Long sort;

    @XmlAttribute
    public Long getSort() {
	return sort;
    }

    public Tree getTree() {
	return tree;
    }

    public Treex(Tree tree) {
	this.tree = tree;
	id = tree.getId();
	idclass = tree.getIdClass();
	did = tree.getParentT().getId();
	sort = tree.getSort();
    }

    public Treex() {
    }

    @XmlAttribute
    public Integer getDid() {
	return did;
    }

    public void setDid(Integer id) {
	did = id;
    }

    @XmlAttribute
    public Integer getIdclass() {
	return idclass;
    }

    @XmlAttribute
    public Integer getId() {
	return id;
    }
}
