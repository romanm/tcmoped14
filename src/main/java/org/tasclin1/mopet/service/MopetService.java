package org.tasclin1.mopet.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.tasclin1.mopet.domain.Tree;

@Service("mopetService")
@Repository
public class MopetService {
    protected final Log log = LogFactory.getLog(getClass());
    private EntityManager em;

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
	this.em = em;
    }

    public Tree getTree(Integer id) {
	log.debug(id);
	if (id >= 100) {
	}
	Tree treeO = em.find(Tree.class, 9930);
	log.debug(treeO);
	return treeO;
    }

}
