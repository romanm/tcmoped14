package org.tasclin1.mopet.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.tasclin1.mopet.controller.NewFolderBean;
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

/**
 * @author roman
 * 
 */
@Service("mopetService")
@Repository
public class MopetService {
    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    @Qualifier("dbStructurService")
    private DbstructurService dbStructurService;

    private EntityManager em;

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
	this.em = em;
    }

    /**
     * Get next value from DB 'dbid' sequence.
     * @return next id number.
     */
    public int nextDbid() {
	int intValue = ((BigInteger) em.createNativeQuery("SELECT nextval('dbid')").getSingleResult()).intValue();
	return intValue;
    }

    public void init() {
	log.debug("--------init--------" + dbStructurService);
	dbStructurService.init();
    }

    public NewFolderBean createNewFolderBean() {
	return new NewFolderBean();
    }

    // home
    public void home(Model model) {
	log.debug("----------------" + dbStructurService);
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
	Tree folderT = setTreeWithMtlO(idFolder, model);
	model.addAttribute("folderT", folderT);
	for (Tree tree : folderT.getChildTs())
	    setMtlO(tree, model);
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

    @Transactional(readOnly = true)
    public Tree readTaskDrug(Integer idt, Model model) {
	Tree drugT = setTreeWithMtlO(idt, model);
	while (!(drugT.isDrug() && drugT.getParentT().isTask()))
	    drugT = drugT.getParentT();
	readNodes3(drugT.getId(), model);
	return drugT;

    }

    @Transactional(readOnly = true)
    public Tree readNodes3(Integer id, Model model) {
	Tree t0 = setTreeWithMtlO(id, model);
	for (Tree t1 : t0.getChildTs()) {
	    setMtlO(t1, model);
	    for (Tree t2 : t1.getChildTs()) {
		setMtlO(t2, model);
		for (Tree t3 : t2.getChildTs()) {
		    setMtlO(t3, model);
		}
	    }
	}
	return t0;
    }

    // patient

    @Transactional(readOnly = true)
    public Tree setPatientTO(Integer idPatient, Model model) {
	Tree patientT = setTreeWithMtlO(idPatient, model);
	model.addAttribute("patientT", patientT);
	model.addAttribute("patientO", patientT.getMtlO());
	return patientT;
    }

    // public void setPatientO(Integer idPatient, Model model) {
    // Patient patientO = setPatientO2doc(idPatient, model);
    // Tree patientT = readPatientDoc(model, idPatient);
    // patientT.setMtlO(patientO);
    // }

    @Transactional(readOnly = true)
    public Tree readPatientDoc(Model model, Integer idPatient) {
	Tree patientT = setPatientTO(idPatient, model);
	for (Tree t1 : patientT.getChildTs()) {
	    setMtlO(t1, model);
	    setPatientDocAttribute(model, t1);
	    for (Tree t2 : t1.getChildTs()) {
		setMtlO(t2, model);
		for (Tree t3 : t2.getChildTs()) {
		    setMtlO(t3, model);
		    // dose modification ref tree.
		    setRefT(t3, model);
		    for (Tree t4 : t3.getChildTs()) {
			setMtlO(t4, model);
		    }
		}
	    }
	}
	return patientT;
    }

    @Transactional(readOnly = true)
    public Tree readPatientDocShort(Integer idPatient, Model model) {
	setPatientTO(idPatient, model);
	Tree patientT = setPatientTO(idPatient, model);
	for (Tree t1 : patientT.getChildTs()) {
	    setMtlO(t1, model);
	    setPatientDocAttribute(model, t1);
	    for (Tree t2 : t1.getChildTs()) {
		setMtlO(t2, model);
	    }
	}
	return patientT;
    }

    @Transactional(readOnly = true)
    public Tree setTreeWithMtlO(Integer id, Model model) {
	Tree tree = em.find(Tree.class, id);
	setMtlO(tree, model);
	return tree;
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
	Tree conceptT = setTreeWithMtlO(idStudy, model);
	setMtlO(conceptT, model);
	model.addAttribute("conceptT", conceptT);
	return conceptT;
    }

    @Transactional(readOnly = true)
    public void readConceptDocT(Integer idStudy, Model model) {
	Tree conceptT = readConceptT(idStudy, model);
	for (Tree t1 : conceptT.getChildTs()) {
	    if ("definition".equals(t1.getTabName()))
		model.addAttribute("conceptDefinitionT", t1);
	    setMtlO(t1, model);
	    for (Tree t2 : t1.getChildTs()) {
		setMtlO(t2, model);
		if (!"definition".equals(t1.getTabName()))
		    for (Tree t3 : t2.getChildTs()) {
			setMtlO(t3, model);
			for (Tree t4 : t3.getChildTs()) {
			    setMtlO(t4, model);
			    // for (Tree t5 : t4.getChildTs()) {
			    // setMtlO(t5);
			    // for (Tree t6 : t5.getChildTs()) {
			    // setMtlO(t6);
			    // }
			    // }
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
	Tree regimeT = (Tree) model.asMap().get(REGIMET);
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
	if (noticeExprT.isNotice() || noticeExprT.isExpr()) {
	    if (!drugNoticeExprM.containsKey(drugT)) {
		List<Tree> drugNoticeExprL = new ArrayList<Tree>();
		drugNoticeExprM.put(drugT, drugNoticeExprL);
	    }
	    drugNoticeExprM.get(drugT).add(noticeExprT);
	}
    }

    final static String REGIMET = "regimeT";
    final static String fs_treeFromId = "treeFromId";

    @Transactional(readOnly = true)
    public void readRegimeDocT(Integer idRegime, Model model) {
	model.addAttribute(fs_treeFromId, new HashMap<Integer, Tree>());
	Tree regimeT = setTreeWithMtlO(idRegime, model);
	model.addAttribute(REGIMET, regimeT);
	List<Tree> regimeTimesTs = regimeTimesTs(regimeT, model);
	model.addAttribute("regimeTimesTs", regimeTimesTs);
    }

    private List<Tree> regimeTimesTs(Tree regimeT, Model model) {
	List<Tree> regimeTimesTs = new ArrayList<Tree>();
	for (Tree t1 : regimeT.getChildTs()) {
	    setMtlO(t1, model);
	    for (Tree t2 : t1.getChildTs()) {
		setMtlO(t2, model);
		for (Tree t3 : t2.getChildTs()) {
		    setMtlTimesO(t3, regimeTimesTs, model);
		    for (Tree t4 : t3.getChildTs()) {
			setMtlTimesO(t4, regimeTimesTs, model);
		    }
		}
	    }
	}
	return regimeTimesTs;
    }

    private void setMtlTimesO(Tree tree, List<Tree> regimeTimesTs, Model model) {
	setMtlO(tree, model);
	if (tree.isTimes() && tree.getParentT().getParentT().isDrug())
	    regimeTimesTs.add(tree);
    }

    // regime END

    private void setMtlO(Tree tree, Model model) {
	if (null != model && model.containsAttribute(fs_treeFromId)) {
	    Map<Integer, Tree> treeFromId = (Map<Integer, Tree>) model.asMap().get(fs_treeFromId);
	    treeFromId.put(tree.getId(), tree);
	}

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

    private void setRefT(Tree t3, Model model) {
	if (t3.hasRef()) {
	    Tree refT = em.find(Tree.class, t3.getRef());
	    t3.setRefT(refT);
	    setMtlO(refT, model);
	    setMtlO(refT.getParentT(), model);
	}
    }

    public Tree checkId(Integer id) {
	List resultList = em.createQuery("SELECT t FROM Tree t WHERE  t.id = :id").setParameter("id", id)
		.getResultList();
	if (resultList.size() == 0)
	    return null;
	return (Tree) resultList.get(0);
    }

    @Transactional(readOnly = false)
    public void order(Integer orderId2, String up_down) {
	Tree orderT = setTreeWithMtlO(orderId2, null);
	log.debug(orderT);
	if (orderT.isTask()) {
	    log.debug(1);
	    if ("support".equals(orderT.getTaskO().getTask())) {
		log.debug(2);
		if ("down".equals(up_down)) {
		    log.debug(3);
		    long timeInMillis = Calendar.getInstance().getTimeInMillis();
		    orderT.setSort(timeInMillis);
		    log.debug(orderT);
		    em.merge(orderT);
		}
	    }
	}
    }

}
