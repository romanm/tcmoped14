package org.tasclin1.mopet.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.tasclin1.mopet.controller.NewFolderBean;
import org.tasclin1.mopet.domain.App;
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
import org.tasclin1.mopet.regime.PlanChronoFormat;
import org.tasclin1.mopet.regime.TaskRun;

/**
 * @author Roman Mishchenko
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

    public Integer getIdFromHtmlId(String param) {
	String id2 = param.split("_")[1];
	Integer clipBoardId = Integer.parseInt(id2);
	return clipBoardId;
    }

    @Transactional(readOnly = true)
    public Tree setTreeWithMtlO(Integer id, Model model) {
	return readTreeWithMtlO(id, model);
    }

    private Tree readTreeWithMtlO(Integer id, Model model) {
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
			    for (Tree t5 : t4.getChildTs()) {
				setMtlO(t5, model);
				for (Tree t6 : t5.getChildTs()) {
				    setMtlO(t6, model);
				    for (Tree t7 : t6.getChildTs()) {
					setMtlO(t7, model);
				    }
				}
			    }
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
	model.addAttribute(MopetService.drugNoticeExprM, new HashMap<Tree, List<Tree>>());
	for (Tree t1 : regimeT.getChildTs()) {
	    for (Tree t2 : t1.getChildTs()) {
		initModel(model, t2);
		for (Tree t3 : t2.getChildTs()) {
		    initModel(model, t3);
		    for (Tree t4 : t3.getChildTs()) {
			initModel(model, t4);
		    }
		}
	    }
	}
	if (isRegimeViewPlan(model)) {
	    PlanChronoFormat planChronoFormat = new PlanChronoFormat();
	    model.addAttribute(planChronoFormat);
	    model.addAttribute(MopetService.regimeTaskRuns, new TreeMap<Long, TaskRun>());
	    model.addAttribute(MopetService.dayNrTaskRuns, new TreeMap<Integer, Set<TaskRun>>());
	    log.debug(1);
	    model.addAttribute(MopetService.daysHoursTaskRuns, new TreeMap<Integer, Map<Integer, Set<TaskRun>>>());
	    Map<Integer, List<Tree>> dayNrDayTs = new TreeMap<Integer, List<Tree>>();
	    model.addAttribute(MopetService.dayNrDayTs, dayNrDayTs);
	    HashSet<Tree> regimeDrugDayTs = new HashSet<Tree>();
	    model.addAttribute(MopetService.regimeDrugDayTs, regimeDrugDayTs);
	    Map<Tree, Set<Integer>> drugT_dayNr = new HashMap<Tree, Set<Integer>>();
	    model.addAttribute("drugT_dayNr", drugT_dayNr);
	    for (Tree dayT : regimeT.getDocNodes())
		if (dayT.isMtlDayO()) {
		    Tree drugT = dayT.getParentT();
		    if (!drugT_dayNr.containsKey(drugT))
			drugT_dayNr.put(drugT, new HashSet<Integer>());
		    for (Integer dayNr : dayT.getDayO().getHashSet()) {
			if (!dayNrDayTs.containsKey(dayNr))
			    dayNrDayTs.put(dayNr, new ArrayList<Tree>());
			dayNrDayTs.get(dayNr).add(dayT);
			drugT_dayNr.get(drugT).add(dayNr);
		    }
		    regimeDrugDayTs.add(dayT);
		}
	    calcTimes1iteration(model);
	    calcTimes1iteration(model);
	    calcTimes1iteration(model);
	    calcTimes1iteration(model);
	}
    }

    private boolean isRegimeViewPlan(Model model) {
	return "plan".equals((String) model.asMap().get(MopetService.regimeView))
		|| "plan2".equals((String) model.asMap().get(MopetService.regimeView));
    }

    private void calcTimes1iteration(Model model) {
	Set<Tree> regimeDrugDayTs = (Set<Tree>) model.asMap().get(MopetService.regimeDrugDayTs);
	for (Tree dayT : regimeDrugDayTs)
	    for (Tree timesT : dayT.getChildTs())
		if (timesT.isTimes()) {
		    if (0 == timesT.getTaskRuns().size()) {
			if (null == timesT.getRef()) {
			    log.debug("-------without ref---------");
			    log.debug(timesT.getParentT().getDayO());
			    Set<Integer> hashSet = timesT.getParentT().getDayO().getHashSet();
			    log.debug(hashSet);
			    for (Integer dayNr : hashSet) {
				log.debug(1);
				Times timesO = timesT.getTimesO();
				if (null != timesO) {
				    log.debug(2);
				    String abs = timesO.getAbs();
				    if (abs.contains("="))
					new TaskRun(timesT, dayNr, model);
				    else
					for (String timesAbs2 : abs.split(",")) {
					    String hour = timesAbs2.split(":")[0];
					    int hour2 = Integer.parseInt(hour);
					    MutableDateTime mutableDateTime = TaskRun.instanceMutableDateTime(dayNr,
						    hour2);
					    new TaskRun(timesT, dayNr, mutableDateTime, model);
					}
				} else
				    new TaskRun(timesT, dayNr, model);
			    }
			} else {// with ref
			    if (null == timesT.getRefT())
				timesT.setRefT(getTreeFromId(model).get(timesT.getRef()));
			    if (timesT.getRefT() == timesT.getDocT()) {
				for (Integer dayNr : timesT.getParentT().getDayO().getHashSet()) {
				    new TaskRun(timesT, dayNr, model);
				}
			    } else if (0 != timesT.getRefT().getTaskRuns().size()) {// refT is calculated
				for (Integer dayNr : timesT.getParentT().getDayO().getHashSet()) {
				    for (TaskRun refTaskRun : timesT.getRefT().getTaskRuns()) {
					if (refTaskRun.getDefDay().equals(dayNr)) {
					    new TaskRun(timesT, refTaskRun, model);
					}
				    }
				}
			    }
			}
		    }
		}
    }

    private void initModel(Model model, Tree tree) {
	if (tree.isNotice() || tree.isExpr()) {
	    Map<Tree, List<Tree>> drugNoticeExprM = (Map<Tree, List<Tree>>) model.asMap().get(
		    MopetService.drugNoticeExprM);
	    Tree drugT = tree.getParentT();
	    while (!drugT.getParentT().isTask())
		drugT = drugT.getParentT();
	    if (!drugNoticeExprM.containsKey(drugT)) {
		List<Tree> drugNoticeExprL = new ArrayList<Tree>();
		drugNoticeExprM.put(drugT, drugNoticeExprL);
	    }
	    drugNoticeExprM.get(drugT).add(tree);
	}
    }

    public final static String taskOneTimeses = "taskOneTimeses";
    public final static String regimeTimesTs = "regimeTimesTs";
    public final static String daysHoursTaskRuns = "daysHoursTaskRuns";
    public final static String dayNrTaskRuns = "dayNrTaskRuns";
    public final static String regimeTaskRuns = "regimeTaskRuns";
    public final static String dayNrDayTs = "dayNrDayTs";
    public final static String regimeDrugDayTs = "regimeDrugDayTs";
    public final static String drugNoticeExprM = "drugNoticeExprM";
    public final static String regimeView = "regimeView";
    public final static String REGIMET = "regimeT";
    final static String fs_treeFromId = "treeFromId";

    @Transactional(readOnly = true)
    public void readRegimeDocT(Integer idRegime, Model model) {
	HashMap<Integer, Tree> treeFromId = new HashMap<Integer, Tree>();
	model.addAttribute(fs_treeFromId, treeFromId);
	Tree regimeT = setTreeWithMtlO(idRegime, model);
	treeFromId.put(regimeT.getIdClass(), regimeT);
	model.addAttribute(REGIMET, regimeT);
	model.addAttribute(MopetService.regimeTimesTs, new ArrayList<Tree>());
	model.addAttribute(MopetService.taskOneTimeses, new HashMap<Tree, List<Tree>>());

	readNodes4(regimeT, model);
	for (Tree t : regimeT.getDocNodes()) {
	}
	initRegimeTimesOrdner(model);
	initRegimeDocT(model);
    }

    private void initRegimeTimesOrdner(Model model) {
	Tree regimeT = (Tree) model.asMap().get(REGIMET);
	List<Tree> regimeTimesTs = (List<Tree>) model.asMap().get(MopetService.regimeTimesTs);
	TreeMap<Integer, Tree> timesOrderMap = new TreeMap<Integer, Tree>();
	model.addAttribute("timesOrderMap", timesOrderMap);
	Map<Tree, Integer> timesBeginMills = new HashMap<Tree, Integer>();
	model.addAttribute("timesBeginMills", timesBeginMills);
	for (Tree timesT : regimeTimesTs) {
	    if (null != timesT.getTimesO() && timesT.getTimesO().getAbs().contains("="))
		continue;
	    log.debug(timesT);
	    // log.debug(timesT.getRef());
	    if (null == timesT.getRef()) {
		log.debug(1);
		calcTimesOrder(model, timesT, 0, 1);
	    } else {
		log.debug(2);
		// log.debug(timesT);
		Tree refT = setRefT(model, timesT, regimeT);
		log.debug(refT.getId() + "==" + regimeT.getId());
		if (refT == regimeT) {
		    log.debug(3);
		    calcTimesOrder(model, timesT, 0, 1);
		} else {// ref to other times
		    log.debug(4);
		    boolean containsValue = timesOrderMap.containsValue(refT);
		    if (containsValue) {
			Integer beginRefTask = timesBeginMills.get(refT);
			int durationRefTask = refT.getAppDurationSecond();
			log.debug("----------------------------" + beginRefTask + "+" + durationRefTask);
			calcTimesOrder(model, timesT, beginRefTask, durationRefTask);
		    }
		}
	    }
	}
    }

    private void calcTimesOrder(Model model, Tree timesT, int beginRefTask, int durationRefTask) {
	log.debug(1);
	Map<Integer, Tree> timesOrderMap = (Map<Integer, Tree>) model.asMap().get("timesOrderMap");
	Map<Tree, Integer> timesBeginMills = (Map<Tree, Integer>) model.asMap().get("timesBeginMills");

	int endRefTask = beginRefTask + durationRefTask;
	Integer durationValueSecond = timesT.getAppDurationSecond();
	Integer connectionIntervalSecond = 0;
	String apporder = "x";
	if (null != timesT.getTimesO()) {
	    connectionIntervalSecond = timesT.getTimesO().getConnectionIntervalSecond();
	    apporder = timesT.getTimesO().getApporder();
	}
	log.debug("apporder=" + apporder + " " + timesT);
	// apporder
	// ↘↖ - leading
	// ↑↓ - driven
	// 0 -↘↑- beginAfterEnd
	// 1 -↑↖- beginBeforeBegin
	// 2 -↘↓- endAfterEnd
	// 3 -↓↖- endBeforeBegin
	int beginTask = correcturKey2next(timesOrderMap, 0);
	if ("0".equals(apporder)) {
	    beginTask = endRefTask + connectionIntervalSecond;
	    beginTask = correcturKey2next(timesOrderMap, beginTask);
	} else if ("1".equals(apporder)) {
	    beginTask = beginRefTask - connectionIntervalSecond;
	    beginTask = correcturKey2previous(timesOrderMap, beginTask);
	} else if ("2".equals(apporder)) {
	    beginTask = endRefTask + connectionIntervalSecond - durationValueSecond;
	    beginTask = correcturKey2next(timesOrderMap, beginTask);
	} else if ("3".equals(apporder)) {
	    beginTask = beginRefTask - connectionIntervalSecond - durationValueSecond;
	    log.debug(beginTask + "=" + endRefTask + "-" + connectionIntervalSecond + "-" + durationValueSecond + "\n"
		    + timesT);
	    beginTask = correcturKey2previous(timesOrderMap, beginTask);
	    log.debug("beginTask=" + beginTask);
	}
	timesOrderMap.put(beginTask, timesT);
	timesBeginMills.put(timesT, beginTask);

	String string = "\n";
	for (Integer integer : timesOrderMap.keySet()) {
	    string += "" + integer + " id=" + timesOrderMap.get(integer).getId() + "\n";
	}
	log.debug(string);
    }

    private Tree setRefT(Model model, Tree timesT, Tree regimeT2) {
	Map<Integer, Tree> treeFromId = (Map<Integer, Tree>) model.asMap().get(MopetService.fs_treeFromId);
	Tree refT = treeFromId.get(timesT.getRef());
	if (null == refT && regimeT2.getIdClass().equals(timesT.getRef()))
	    refT = regimeT2;
	timesT.setRefT(refT);
	return refT;
    }

    private int correcturKey2previous(Map<Integer, Tree> timesOrderMap, int key) {
	while (timesOrderMap.containsKey(key))
	    key--;
	return key;
    }

    private int correcturKey2next(Map<Integer, Tree> timesOrderMap, int key) {
	while (timesOrderMap.containsKey(key))
	    key++;
	return key;
    }

    private void readNodes4(Tree t0, Model model) {
	for (Tree t1 : t0.getChildTs()) {
	    setMtlO(t1, model);
	    for (Tree t2 : t1.getChildTs()) {
		setMtlO(t2, model);
		for (Tree t3 : t2.getChildTs()) {
		    setMtlO(t3, model);
		    for (Tree t4 : t3.getChildTs()) {
			setMtlO(t4, model);
		    }
		}
	    }
	}
    }

    @Transactional(readOnly = true)
    public Tree readNodes4(Integer id, Model model) {
	log.debug(1);
	Tree t0 = setTreeWithMtlO(id, model);
	readNodes4(t0, model);

	// for (Tree t1 : t0.getChildTs()) {
	// setMtlO(t1, model);
	// for (Tree t2 : t1.getChildTs()) {
	// setMtlO(t2, model);
	// for (Tree t3 : t2.getChildTs()) {
	// setMtlO(t3, model);
	// for (Tree t4 : t3.getChildTs()) {
	// setMtlO(t4, model);
	// }
	// }
	// }
	// }
	return t0;
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

    // regime END

    private void setMtlO(Tree tree, Model model) {
	if (null != model && model.containsAttribute(fs_treeFromId)) {
	    Map<Integer, Tree> treeFromId = getTreeFromId(model);
	    treeFromId.put(tree.getId(), tree);
	}

	if (null != tree.getIdClass()) {
	    MObject mO = readMtlO(tree);
	    tree.setMtlO(mO);
	}
	// if (tree.isTimes() && tree.getParentT().getParentT().isDrug())
	Tree taskOneT = tree.getParentT().getParentT();
	if (tree.isTimes() && taskOneT.isTaskOne()) {
	    List<Tree> regimeTimesTs = (List<Tree>) model.asMap().get(MopetService.regimeTimesTs);
	    // in xml is null
	    if (null != regimeTimesTs) {
		regimeTimesTs.add(tree);
	    }
	    Map<Tree, List<Tree>> taskOneTimeses = (Map<Tree, List<Tree>>) model.asMap().get(
		    MopetService.taskOneTimeses);
	    // in xml is null
	    if (null != taskOneTimeses) {
		if (!taskOneTimeses.containsKey(taskOneT))
		    taskOneTimeses.put(taskOneT, new ArrayList<Tree>());
		taskOneTimeses.get(taskOneT).add(tree);
	    }
	}

    }

    private MObject readMtlO(Tree tree) {
	MObject mO = null;
	String tabName = tree.getTabName();
	if ("folder".equals(tabName))
	    mO = em.find(Folder.class, tree.getIdClass());
	else if ("drug".equals(tabName)) {
	    Drug drugO = em.find(Drug.class, tree.getIdClass());
	    Drug generic = drugO.getGeneric();
	    mO = drugO;
	} else if ("app".equals(tabName))
	    mO = em.find(App.class, tree.getIdClass());
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
	return mO;
    }

    private Map<Integer, Tree> getTreeFromId(Model model) {
	Map<Integer, Tree> treeFromId = (Map<Integer, Tree>) model.asMap().get(fs_treeFromId);
	return treeFromId;
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

    private void delete(Tree tree) {
	em.createNativeQuery("DELETE FROM Tree WHERE id=" + tree.getId()).executeUpdate();
    }

    @Transactional
    public void delete(Integer deleteId) {
	Tree deleteT = readTreeWithMtlO(deleteId, null);
	log.debug(deleteT);
	Tree parentT = deleteT.getParentT();
	if (deleteT.isTask() && parentT.isTask()) {
	    if ("support".equals(deleteT.getTaskO().getTask())) {
		if (0 == deleteT.getChildTs().size()) {
		    delete(deleteT);
		}
	    }

	}
    }

    @Transactional(readOnly = false)
    public void order(Integer orderId2, String up_down) {
	Tree orderT = setTreeWithMtlO(orderId2, null);
	log.debug(orderT);
	if (orderT.isTask() && orderT.getParentT().isTask()) {
	    log.debug(1);
	    if ("support".equals(orderT.getTaskO().getTask())) {
		log.debug(2);
		if ("down".equals(up_down)) {
		    log.debug(3);
		    long timeInMillis = Calendar.getInstance().getTimeInMillis();
		    orderT.setSort(timeInMillis);
		    log.debug(orderT);
		    // em.merge(orderT);
		}
	    }
	} else {
	    List<Tree> childTs = orderT.getParentT().getChildTs();
	    int indexOf = childTs.indexOf(orderT);
	    int indexOfNeighbor = indexOf;
	    log.debug(indexOf);
	    int size = childTs.size();
	    log.debug(size);
	    if (size > 1) {
		Tree neighborT = orderT;
		Long sort = orderT.getSort();
		log.debug(sort);
		if ("down".equals(up_down)) {
		    if (indexOf + 1 == size)
			indexOfNeighbor = 0;
		    else
			indexOfNeighbor = indexOf + 1;
		} else if ("up".equals(up_down)) {
		    if (indexOf == 0)
			indexOfNeighbor = size - 1;
		    else
			indexOfNeighbor = indexOf - 1;
		}
		neighborT = childTs.get(indexOfNeighbor);
		log.debug(neighborT);
		Long sortNext = neighborT.getSort();
		log.debug(sortNext);
		if (orderT == neighborT) {
		    log.debug("orderT == neighborT");
		} else if (sort != sortNext) {
		    neighborT.setSort(sort);
		    orderT.setSort(sortNext);
		} else {
		    log.info("TODO!");
		}
	    }
	}
    }

}
