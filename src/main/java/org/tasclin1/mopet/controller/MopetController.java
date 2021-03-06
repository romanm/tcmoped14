package org.tasclin1.mopet.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.tasclin1.mopet.domain.Tree;
import org.tasclin1.mopet.service.MopetService;

@Controller
public class MopetController {
    protected final Log log = LogFactory.getLog(getClass());
    private MopetService mopetService;

    @Autowired
    public MopetController(MopetService mopetService) {
	this.mopetService = mopetService;
	// mopetService.init();
    }

    // copy&paste order delete
    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public String order(@RequestParam("docId")
    Integer docId, @RequestParam("up_down")
    String up_down, @RequestParam("orderId")
    String orderId, Model model) {
	log.debug("up_down=" + up_down);
	log.debug("orderId=" + orderId);
	if (orderId.contains("_")) {
	    Integer orderId2 = mopetService.getIdFromHtmlId(orderId);
	    log.debug("orderId2=" + orderId2);
	    mopetService.order(orderId2, up_down);
	}
	return fromId(docId);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@RequestParam("docId")
    Integer docId, @RequestParam("deleteId")
    String deleteId, Model model) {
	log.debug("-----------------" + deleteId);
	Integer deleteId2 = mopetService.getIdFromHtmlId(deleteId);
	log.debug("orderId2=" + deleteId2);
	mopetService.delete(deleteId2);
	return fromId(docId);
    }

    @RequestMapping(value = "/paste", method = RequestMethod.POST)
    public String paste(@RequestParam("docId")
    Integer docId, @RequestParam("pasteId")
    String pasteId, Model model) {
	log.debug("pasteId=" + pasteId);
	if (pasteId.contains("_")) {
	    Integer pasteId2 = mopetService.getIdFromHtmlId(pasteId);
	    Tree pasteT = mopetService.setTreeWithMtlO(pasteId2, model);
	    log.debug("pasteT=" + pasteT);
	    Integer clipBoardId = (Integer) getRequest().getSession().getAttribute("clipBoardId");
	    log.debug("clipBoardId=" + clipBoardId);
	    if (null != clipBoardId) {
		Tree copyT = mopetService.setTreeWithMtlO(clipBoardId, model);
		log.debug("copyT=" + copyT);
	    }
	}
	log.debug("docId=" + docId);
	return fromId(docId);
    }

    // Regimex xml(@PathVariable
    @RequestMapping(value = "/copy", method = RequestMethod.GET)
    public @ResponseBody
    String copy() {
	Integer clipBoardId = mopetService.getIdFromHtmlId(getRequest().getParameter("id"));
	getRequest().getSession().setAttribute("clipBoardId", clipBoardId);
	return "<b>clipBoardId</b> " + getRequest().getParameter("id");
    }

    // copy&paste END

    // home
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
	mopetService.home(model);
	return "home";
    }

    // home END

    // Patient ChemoRegime
    @RequestMapping(value = "/f={idFolder}/p={idPatient}/s={idStudy}/cere-{regimeView}={idRegime}", method = RequestMethod.GET)
    public void folderPatientConceptRegime(@PathVariable
    Integer idFolder, @PathVariable
    Integer idPatient, @PathVariable
    Integer idStudy, @PathVariable
    String regimeView, @PathVariable
    Integer idRegime, Model model) {
	Tree patientT = mopetService.readPatientDocShort(idPatient, model);
	lastUsedDocuments(model, patientT);
	readRegime(idFolder, idStudy, regimeView, idRegime, model);
    }

    // Patient ChemoRegime END
    // ChemoRegime
    private void readRegime(Integer idFolder, Integer idStudy, String regimeView, Integer idRegime, Model model) {
	addRegimeView(regimeView, model);
	mopetService.readFolderO2doc(idFolder, model);
	Tree conceptT = mopetService.readConceptT(idStudy, model);
	lastUsedDocuments(model, conceptT);
	mopetService.readRegimeDocT(idRegime, model);
	// mopetService.initRegimeDocT(model);
	model.addAttribute("docId", idRegime);
	Tree sessionPatientT = (Tree) getRequest().getSession().getAttribute("sessionPatientT");
	if (null != sessionPatientT)
	    model.addAttribute("sessionPatientT", sessionPatientT);
	Tree docT = (Tree) model.asMap().get(MopetService.REGIMET);
	lastUsedDocuments(model, docT);
    }

    private void lastUsedDocuments(Model model, Tree docT) {
	Map<Integer, Tree> lastUsedDocuments = (Map<Integer, Tree>) getRequest().getSession().getAttribute(
		"lastUsedDocuments");
	List<Integer> lastUsedDocumentsList = (List<Integer>) getRequest().getSession().getAttribute(
		"lastUsedDocumentsList");
	if (null == lastUsedDocuments) {
	    lastUsedDocuments = new HashMap<Integer, Tree>();
	    lastUsedDocumentsList = new ArrayList<Integer>();
	    getRequest().getSession().setAttribute("lastUsedDocuments", lastUsedDocuments);
	    getRequest().getSession().setAttribute("lastUsedDocumentsList", lastUsedDocumentsList);
	    model.addAttribute("lastUsedDocuments", lastUsedDocuments);
	    model.addAttribute("lastUsedDocumentsList", lastUsedDocumentsList);
	}
	if (!lastUsedDocuments.containsKey(docT.getId())) {
	    lastUsedDocuments.put(docT.getId(), docT);
	    lastUsedDocumentsList.add(0, docT.getId());
	}
	if (lastUsedDocuments.size() > 10) {
	    Integer lastId = lastUsedDocumentsList.get(lastUsedDocumentsList.size() - 1);
	    Tree lastDocT = lastUsedDocuments.get(lastId);
	    lastUsedDocuments.remove(lastDocT);
	}
    }

    @RequestMapping(value = "/f={idFolder}/s={idStudy}/cere-{regimeView}={idRegime}", method = RequestMethod.GET)
    public void folderConceptRegime(@PathVariable
    Integer idFolder, @PathVariable
    Integer idStudy, @PathVariable
    String regimeView, @PathVariable
    Integer idRegime, Model model) {
	// Tree tree = mopetService.checkId(idRegime);
	// if (null == tree)
	// return "redirect:/";
	readRegime(idFolder, idStudy, regimeView, idRegime, model);
	// return "/f=" + idFolder + "/s=" + idStudy + "/cere-" + regimeView + "=" + idRegime;
    }

    public static HttpServletRequest getRequest() {
	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
		.getRequest();
	return request;
    }

    @RequestMapping(value = "/doc-cere-ed={idRegime}", method = RequestMethod.GET)
    public void cereEd(@PathVariable
    Integer idRegime, Model model) {
	mopetService.readRegimeDocT(idRegime, model);
    }

    @RequestMapping(value = "/chemoregime-{id}", method = RequestMethod.GET)
    public void chemoregime(@PathVariable
    Integer id, Model model) {
	log.debug("1");
	model.addAttribute(id);
    }

    @RequestMapping(value = "/chemoregime_{chemoregimePart}-{id}", method = RequestMethod.GET)
    public void schemaViewRest(@PathVariable
    String chemoregimePart, @PathVariable
    Integer id, Model model) {
	log.debug("1");
	model.addAttribute(chemoregimePart);
	model.addAttribute(id);
    }

    // ChemoRegime END

    // Patient concept
    @RequestMapping(value = "f={idFolder}/p={idPatient}/study-{studyPart}={idStudy}", method = RequestMethod.GET)
    public void folderPatientConcept(@PathVariable
    Integer idFolder, @PathVariable
    Integer idPatient, @PathVariable
    String studyPart, @PathVariable
    Integer idStudy, Model model) {
	Tree patientT = mopetService.readPatientDocShort(idPatient, model);
	lastUsedDocuments(model, patientT);
	readConcept(idFolder, studyPart, idStudy, model);
    }

    // Patient concept END
    // Concept
    @RequestMapping(value = "f={idFolder}/study-{studyPart}={idStudy}", method = RequestMethod.GET)
    public void folderConcept(@PathVariable
    Integer idFolder, @PathVariable
    String studyPart, @PathVariable
    Integer idStudy, Model model) {
	readConcept(idFolder, studyPart, idStudy, model);
    }

    private void readConcept(Integer idFolder, String studyPart, Integer idStudy, Model model) {
	log.debug(1);
	addStudyView(studyPart, model);
	mopetService.readFolderO2doc(idFolder, model);
	Tree conceptT = mopetService.readConceptDocT(idStudy, model);
	lastUsedDocuments(model, conceptT);
	getRequest().getSession().setAttribute("studyPart", studyPart);
	model.addAttribute("docId", idStudy);
	log.debug(getRequest().getSession().getAttribute("studyView"));
    }

    @RequestMapping(value = "/doc-study={id}", method = RequestMethod.GET)
    public void docStudy(@PathVariable
    Integer id, Model model) {
	log.debug("id=" + id);
	model.addAttribute(id);
    }

    // Concept END

    @RequestMapping(value = "f={id}", method = RequestMethod.GET)
    public String toFolder(@PathVariable
    Integer id, Model model) {
	return fromId(id);
    }

    @RequestMapping(value = "f={idf}/p={id}", method = RequestMethod.GET)
    public String toPatient(@PathVariable
    Integer id, Model model) {
	return fromId(id);
    }

    @RequestMapping(value = "f={idf}/p={idp}/s={id}", method = RequestMethod.GET)
    public String toPatientStudy(@PathVariable
    Integer id, Model model) {
	return fromId(id);
    }

    @RequestMapping(value = "f={idf}/s={id}", method = RequestMethod.GET)
    public String toStudy(@PathVariable
    Integer id, Model model) {
	HttpServletRequest request = getRequest();
	log.debug("ContextPath=" + request.getContextPath());
	return fromId(id);
    }

    // Patient
    @RequestMapping(value = "f={idFolder}/patient={idPatient}", method = RequestMethod.GET)
    public void folderPatient(@PathVariable
    Integer idFolder, @PathVariable
    Integer idPatient, Model model) {
	mopetService.readFolderO2doc(idFolder, model);
	Tree patientT = mopetService.readPatientDoc(model, idPatient);
	lastUsedDocuments(model, patientT);
	model.addAttribute("docId", idPatient);
    }

    // Patient END

    // Folder
    @RequestMapping(value = "/folder={idFolder}", method = RequestMethod.GET)
    public void folder(@PathVariable
    Integer idFolder, Model model) {
	mopetService.readFolderO2folder(idFolder, model);
	model.addAttribute("docId", idFolder);
    }

    // Folder END

    @RequestMapping(value = "id={id}", method = RequestMethod.GET)
    public String fromId(@PathVariable
    Integer id) {
	// Integer id, Model model) {
	log.debug(id);
	Tree tree = mopetService.checkId(id);
	log.debug(tree);

	if (null == tree)
	    return "redirect:/";
	int idFolder;
	boolean isFolder = "folder".equals(tree.getTabName());
	if (isFolder) {
	    idFolder = tree.getId();
	    return "redirect:/folder=" + idFolder;
	}
	int idPatient;
	boolean isPatient = "patient".equals(tree.getTabName());
	if (isPatient) {
	    idPatient = tree.getId();
	    idFolder = tree.getParentT().getId();
	    return "redirect:/f=" + idFolder + "/patient=" + idPatient;
	}
	int idConcept;
	boolean isConcept = "protocol".equals(tree.getTabName());
	if (isConcept) {
	    idConcept = tree.getId();
	    String study_Part_Id_Url = "/study-" + getStudyView() + "=" + idConcept;
	    isPatient = "patient".equals(tree.getParentT().getTabName());
	    if (isPatient) {
		idPatient = tree.getParentT().getId();
		idFolder = tree.getParentT().getParentT().getId();
		return "redirect:/f=" + idFolder + "/p=" + idPatient + study_Part_Id_Url;
	    }
	    if (!isPatient) {
		idFolder = tree.getParentT().getId();
		return "redirect:/f=" + idFolder + study_Part_Id_Url;
	    }
	}
	int idRegime;
	boolean isRegime = "task".equals(tree.getTabName());
	if (isRegime) {
	    idRegime = tree.getId();
	    Tree conceptT = tree.getDocT();
	    idConcept = conceptT.getId();
	    String cere_Part_Id_Url = "/cere-" + getRegimeView() + "=" + idRegime;
	    isPatient = "patient".equals(conceptT.getParentT().getTabName());
	    if (isPatient) {
		idPatient = conceptT.getParentT().getId();
		idFolder = conceptT.getParentT().getParentT().getId();
		return "redirect:/f=" + idFolder + "/p=" + idPatient + "/s=" + idConcept + cere_Part_Id_Url;
	    }
	    if (!isPatient) {
		idFolder = conceptT.getParentT().getId();
		return "redirect:/f=" + idFolder + "/s=" + idConcept + cere_Part_Id_Url;
	    }
	}
	return "redirect:/";
    }

    private void addRegimeView(String regimeView, Model model) {
	model.addAttribute(MopetService.regimeView, regimeView);
	getRequest().getSession().setAttribute(MopetService.regimeView, regimeView);
    }

    private String getRegimeView() {
	String regimeView = (String) getRequest().getSession().getAttribute("regimeView");
	if (null == regimeView)
	    regimeView = "ed";
	return regimeView;
    }

    private void addStudyView(String studyView, Model model) {
	model.addAttribute(MopetService.studyView, studyView);
	getRequest().getSession().setAttribute(MopetService.studyView, studyView);
    }

    private String getStudyView() {
	String studyView = (String) getRequest().getSession().getAttribute("studyView");
	if (null == studyView)
	    studyView = "sg";
	return studyView;
    }

}
