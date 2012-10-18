package org.tasclin1.mopet.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.tasclin1.mopet.domain.Arm;
import org.tasclin1.mopet.domain.Concept;
import org.tasclin1.mopet.domain.Day;
import org.tasclin1.mopet.domain.Dose;
import org.tasclin1.mopet.domain.Drug;
import org.tasclin1.mopet.domain.Expr;
import org.tasclin1.mopet.domain.Folder;
import org.tasclin1.mopet.domain.MObject;
import org.tasclin1.mopet.domain.Notice;
import org.tasclin1.mopet.domain.Patient;
import org.tasclin1.mopet.domain.Task;
import org.tasclin1.mopet.domain.Times;
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

    // home
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

    // home END

    // folder
    @Transactional(readOnly = true)
    public void setFolderO(Integer idFolder, Model model) {
	Folder folderO = setFolderO2doc(idFolder, model);
	for (Folder folder : folderO.getChildFs()) {
	}
	while (!"folder".equals(folderO.getParentF().getFolder()))
	    folderO = folderO.getParentF();
	model.addAttribute("firstFolderO", folderO);
    }

    @Transactional(readOnly = true)
    public Folder setFolderO2doc(Integer idFolder, Model model) {
	model.addAttribute(idFolder);
	Folder folderO = em.find(Folder.class, idFolder);
	model.addAttribute("folderO", folderO);
	return folderO;
    }

    @Transactional(readOnly = true)
    public void setFolderT(Integer idFolder, Model model) {
	Tree folderT = em.find(Tree.class, idFolder);
	model.addAttribute("folderT", folderT);
	for (Tree tree : folderT.getChildTs())
	    setMtlO(tree);
    }

    // folder END

    // patient
    @Transactional(readOnly = true)
    public void setPatientO(Integer idPatient, Model model) {
	model.addAttribute(idPatient);
	Patient patientO = em.find(Patient.class, idPatient);
	model.addAttribute("patientO", patientO);
	Tree patientT = em.find(Tree.class, idPatient);
	patientT.setMtlO(patientO);
	model.addAttribute("patientT", patientT);
	for (Tree t1 : patientT.getChildTs()) {
	    setMtlO(t1);
	    for (Tree t2 : t1.getChildTs()) {
		setMtlO(t2);
		for (Tree t3 : t2.getChildTs()) {
		    setMtlO(t3);
		}
	    }
	}
    }

    // patient END

    // study
    @Transactional(readOnly = true)
    public void setStudyO(Integer idStudy, Model model) {
	Concept conceptO = setStudyO2doc(idStudy, model);
	Tree conceptT = em.find(Tree.class, idStudy);
	conceptT.setMtlO(conceptO);
	model.addAttribute("conceptT", conceptT);
	for (Tree t1 : conceptT.getChildTs()) {
	    if ("definition".equals(t1.getTabName()))
		model.addAttribute("conceptDefinitionT", t1);
	    setMtlO(t1);
	    for (Tree t2 : t1.getChildTs()) {
		setMtlO(t2);
		for (Tree t3 : t2.getChildTs()) {
		    setMtlO(t3);
		    for (Tree t4 : t3.getChildTs()) {
			setMtlO(t4);
		    }
		}
	    }
	}
	/*
	 * for (Tree t1 : conceptT.getChildTs()) { setMtlO(t1); if ("definition".equals(t1.getTabName())) {
	 * model.addAttribute("conceptDefinitionT", t1); for (Tree tree2 : t1.getChildTs()) setMtlO(tree2); } }
	 */
    }

    @Transactional(readOnly = true)
    public Concept setStudyO2doc(Integer idStudy, Model model) {
	model.addAttribute(idStudy);
	Concept conceptO = em.find(Concept.class, idStudy);
	model.addAttribute("conceptO", conceptO);
	return conceptO;
    }

    // study END

    // regime
    @Transactional(readOnly = true)
    public void setRegime(Integer idRegime, Model model) {
	Task regimeO = setRegimeO2doc(idRegime, model);
	Tree regimeT = em.find(Tree.class, idRegime);
	model.addAttribute("regimeT", regimeT);
	regimeT.setMtlO(regimeO);
	List<Tree> regimeTimesTs = new ArrayList<Tree>();
	for (Tree t1 : regimeT.getChildTs()) {
	    setMtlO(t1);
	    for (Tree t2 : t1.getChildTs()) {
		setMtlO(t2);
		for (Tree t3 : t2.getChildTs()) {
		    setMtlTimesO(t3, regimeTimesTs);
		    for (Tree t4 : t3.getChildTs()) {
			setMtlTimesO(t4, regimeTimesTs);
		    }
		}
	    }
	}
	model.addAttribute("regimeTimesTs", regimeTimesTs);
    }

    private void setMtlTimesO(Tree tree, List<Tree> regimeTimesTs) {
	setMtlO(tree);
	if ("times".equals(tree.getTabName()))
	    regimeTimesTs.add(tree);
    }

    @Transactional(readOnly = true)
    public Task setRegimeO2doc(Integer idRegime, Model model) {
	model.addAttribute(idRegime);
	Task regimeO = em.find(Task.class, idRegime);
	model.addAttribute("regimeO", regimeO);
	return regimeO;
    }

    // regime END

    private void setMtlO(Tree tree) {
	if (null == tree.getIdClass())
	    return;
	MObject mO = null;
	String tabName = tree.getTabName();
	if ("folder".equals(tabName))
	    mO = em.find(Folder.class, tree.getIdClass());
	else if ("drug".equals(tabName))
	    mO = em.find(Drug.class, tree.getIdClass());
	else if ("dose".equals(tabName))
	    mO = em.find(Dose.class, tree.getIdClass());
	else if ("day".equals(tabName))
	    mO = em.find(Day.class, tree.getIdClass());
	else if ("times".equals(tabName))
	    mO = em.find(Times.class, tree.getIdClass());
	else if ("patient".equals(tabName))
	    mO = em.find(Patient.class, tree.getIdClass());
	else if ("protocol".equals(tabName))
	    mO = em.find(Concept.class, tree.getIdClass());
	else if ("task".equals(tabName))
	    mO = em.find(Task.class, tree.getIdClass());
	else if ("notice".equals(tabName))
	    mO = em.find(Notice.class, tree.getIdClass());
	else if ("expr".equals(tabName))
	    mO = em.find(Expr.class, tree.getIdClass());
	else if ("studyarm".equals(tabName))
	    mO = em.find(Arm.class, tree.getIdClass());

	tree.setMtlO(mO);
    }

    public Tree checkId(Integer id) {
	List resultList = em.createQuery("SELECT t FROM Tree t WHERE  t.id = :id").setParameter("id", id)
		.getResultList();
	log.debug(resultList);
	if (resultList.size() == 0)
	    return null;
	return (Tree) resultList.get(0);
    }

}
