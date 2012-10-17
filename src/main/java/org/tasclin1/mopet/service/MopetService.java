package org.tasclin1.mopet.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.tasclin1.mopet.domain.Folder;
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

    public Tree checkId(Integer id) {
	List resultList = em.createQuery("SELECT t FROM Tree t WHERE  t.id = :id").setParameter("id", id)
		.getResultList();
	log.debug(resultList);
	if (resultList.size() == 0)
	    return null;
	return (Tree) resultList.get(0);
    }

    public Tree getTree(Integer id) {
	log.debug(id);
	if (id >= 100) {
	}
	Tree treeO = em.find(Tree.class, 9930);
	log.debug(treeO);
	return treeO;
    }

    public void home(Model model) {
	Folder patientF = (Folder) em.createQuery("SELECT f FROM Folder f WHERE f.folder=:folder")
		.setParameter("folder", "patient").getSingleResult();
	model.addAttribute("patientF", patientF);
	Folder protocolF = (Folder) em
		.createQuery(
			"SELECT f FROM Folder f, Folder f1 WHERE f1.folder='folder' and f1.id=f.parentF.id and"
				+ " f.folder=:folder").setParameter("folder", "protocol").getSingleResult();
	model.addAttribute("protocolF", protocolF);
    }

    @Transactional(readOnly = true)
    public void setFolder(Integer idFolder, Model model) {
	Folder folderO = em.find(Folder.class, idFolder);
	model.addAttribute("folderO", folderO);
	for (Folder folder : folderO.getChildFs()) {
	}
	while (!"folder".equals(folderO.getParentF().getFolder()))
	    folderO = folderO.getParentF();
    }

}
