package org.tasclin1.mopet.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.tasclin1.mopet.domain.Diagnose;
import org.tasclin1.mopet.domain.Dose;
import org.tasclin1.mopet.domain.Drug;
import org.tasclin1.mopet.domain.Expr;
import org.tasclin1.mopet.domain.Finding;
import org.tasclin1.mopet.domain.Folder;
import org.tasclin1.mopet.domain.Ivariable;
import org.tasclin1.mopet.domain.Labor;
import org.tasclin1.mopet.domain.MObject;
import org.tasclin1.mopet.domain.Notice;
import org.tasclin1.mopet.domain.Patient;
import org.tasclin1.mopet.domain.Pvariable;
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
	Folder drugF = (Folder) em.createQuery("SELECT f FROM Folder f WHERE f.folder=:folder")
		.setParameter("folder", "drug").getSingleResult();
	model.addAttribute("drugF", drugF);
    }

    // home END

    // folder
    @Transactional(readOnly = true)
    public void readFolderO2folder(Integer idFolder, Model model) {
	Folder folderO = readFolderO2doc(idFolder, model);
	Tree folderT = em.find(Tree.class, idFolder);
	model.addAttribute("folderT", folderT);
	for (Tree tree : folderT.getChildTs())
	    setMtlO(tree);
	while (!"folder".equals(folderO.getParentF().getFolder()))
	    folderO = folderO.getParentF();
	model.addAttribute("firstFolderO", folderO);
    }

    @Transactional(readOnly = true)
    public Folder readFolderO2doc(Integer idFolder, Model model) {
	model.addAttribute(idFolder);
	Folder folderO = em.find(Folder.class, idFolder);
	model.addAttribute("folderO", folderO);
	return folderO;
    }

    // folder END

    // patient
    @Transactional(readOnly = true)
    public void setPatientO(Integer idPatient, Model model) {
	Patient patientO = setPatientO2doc(idPatient, model);
	Tree patientT = readPatientDoc(model, idPatient);
	patientT.setMtlO(patientO);
    }

    private Tree readPatientDoc(Model model, Integer idPatient) {
	Tree patientT = setPatientT(model, idPatient);
	for (Tree t1 : patientT.getChildTs()) {
	    setMtlO(t1);
	    setPatientDocAttribute(model, t1);
	    for (Tree t2 : t1.getChildTs()) {
		setMtlO(t2);
		for (Tree t3 : t2.getChildTs()) {
		    setMtlO(t3);
		    setRefT(t3);
		    for (Tree t4 : t3.getChildTs()) {
			setMtlO(t4);
		    }
		}
	    }
	}
	return patientT;
    }

    private void setRefT(Tree t3) {
	if (t3.hasRef()) {
	    Tree refT = em.find(Tree.class, t3.getRef());
	    t3.setRefT(refT);
	    setMtlO(refT);
	    setMtlO(refT.getParentT());
	}
    }

    @Transactional(readOnly = true)
    public Tree readPatientDocShort(Integer idPatient, Model model) {
	Patient patientO = setPatientO2doc(idPatient, model);
	Tree patientT = setPatientT(model, idPatient);
	patientT.setMtlO(patientO);
	for (Tree t1 : patientT.getChildTs()) {
	    setMtlO(t1);
	    setPatientDocAttribute(model, t1);
	    for (Tree t2 : t1.getChildTs()) {
		setMtlO(t2);
	    }
	}
	return patientT;
    }

    private Tree setPatientT(Model model, Integer idPatient) {
	Tree patientT = em.find(Tree.class, idPatient);
	model.addAttribute("patientT", patientT);
	return patientT;
    }

    private void setPatientDocAttribute(Model model, Tree t1) {
	if (t1.isFinding()) {
	    if ("weight".equals(t1.getFinding().getFinding())) {
		addFirstAtt(model, t1, "lastWeightT");
	    } else if ("bsaType".equals(t1.getFinding().getFinding())) {
		addFirstAtt(model, t1, "lastBsaTypeT");
	    } else if ("height".equals(t1.getFinding().getFinding())) {
		addFirstAtt(model, t1, "lastHeightT");
	    }
	} else if (t1.isDiagnose()) {
	    addFirstAtt(model, t1, "lastDiagnoseT");
	}
    }

    private Patient setPatientO2doc(Integer idPatient, Model model) {
	model.addAttribute(idPatient);
	Patient patientO = em.find(Patient.class, idPatient);
	model.addAttribute("patientO", patientO);
	return patientO;
    }

    /**
     * Added only one attribute in model.
     * @param model
     * @param t1
     * @param attName
     */
    private void addFirstAtt(Model model, Tree t1, String attName) {
	if (!model.containsAttribute(attName)) {
	    model.addAttribute(attName, t1);
	}
    }

    // patient END

    // concept
    @Transactional(readOnly = true)
    public Tree readConceptT(Integer idStudy, Model model) {
	Tree conceptT = em.find(Tree.class, idStudy);
	setMtlO(conceptT);
	model.addAttribute("conceptT", conceptT);
	return conceptT;
    }

    @Transactional(readOnly = true)
    public void readConceptDocT(Integer idStudy, Model model) {
	Tree conceptT = readConceptT(idStudy, model);
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

    // concept END

    // regime
    public void initRegimeDocT(Model model) {
	log.debug("-------------------- 1");
	Tree regimeT = (Tree) model.asMap().get("regimeT");
	model.addAttribute("drugNoticeExprM", new HashMap<Tree, List<Tree>>());
	for (Tree t1 : regimeT.getChildTs()) {
	    for (Tree t2 : t1.getChildTs()) {
		addDrugNoticeExpr(model, t2);
		for (Tree t3 : t2.getChildTs()) {
		    addDrugNoticeExpr(model, t3);
		    for (Tree t4 : t3.getChildTs()) {
			addDrugNoticeExpr(model, t4);
		    }
		}
	    }
	}
    }

    private void addDrugNoticeExpr(Model model, Tree noticeExprT) {
	Map<Tree, List<Tree>> drugNoticeExprM = (Map<Tree, List<Tree>>) model.asMap().get("drugNoticeExprM");
	Tree drugT = noticeExprT.getParentT();
	while (!drugT.getParentT().isTask())
	    drugT = drugT.getParentT();
	log.debug("-------------------- 2");
	if (noticeExprT.isNotice() || noticeExprT.isExpr()) {
	    if (!drugNoticeExprM.containsKey(drugT)) {
		log.debug("-------------------- 3 " + drugT.getId());
		List<Tree> drugNoticeExprL = new ArrayList<Tree>();
		drugNoticeExprM.put(drugT, drugNoticeExprL);
	    }
	    log.debug("-------------------- 4");
	    drugNoticeExprM.get(drugT).add(noticeExprT);
	    log.debug("-------------------- 5 " + drugNoticeExprM.get(drugT));
	}
    }

    @Transactional(readOnly = true)
    public void readRegimeDocT(Integer idRegime, Model model) {
	Tree regimeT = em.find(Tree.class, idRegime);
	setMtlO(regimeT);
	model.addAttribute("regimeT", regimeT);
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
	else if ("pvariable".equals(tabName))
	    mO = em.find(Pvariable.class, tree.getIdClass());
	else if ("ivariable".equals(tabName))
	    mO = em.find(Ivariable.class, tree.getIdClass());
	else if ("finding".equals(tabName))
	    mO = em.find(Finding.class, tree.getIdClass());
	else if ("labor".equals(tabName))
	    mO = em.find(Labor.class, tree.getIdClass());
	else if ("protocol".equals(tabName))
	    mO = em.find(Concept.class, tree.getIdClass());
	else if ("task".equals(tabName))
	    mO = em.find(Task.class, tree.getIdClass());
	else if ("notice".equals(tabName))
	    mO = em.find(Notice.class, tree.getIdClass());
	else if ("diagnose".equals(tabName))
	    mO = em.find(Diagnose.class, tree.getIdClass());
	else if ("patient".equals(tabName))
	    mO = em.find(Patient.class, tree.getIdClass());
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
