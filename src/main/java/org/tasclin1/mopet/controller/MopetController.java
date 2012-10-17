package org.tasclin1.mopet.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    }

    // ChemoRegime
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
	mopetService.home(model);
	return "home";
    }

    // ChemoRegime
    @RequestMapping(value = "/f={idFolder}/s={idStudy}/cere-week={idRegime}", method = RequestMethod.GET)
    public void scereWeek(@PathVariable
    Integer idFolder, @PathVariable
    Integer idStudy, @PathVariable
    Integer idRegime, Model model) {
	cereVariablen(idFolder, idStudy, idRegime, model);
	getRequest().getSession().setAttribute("regimePart", "week");
    }

    @RequestMapping(value = "/f={idFolder}/s={idStudy}/cere-plan={idRegime}", method = RequestMethod.GET)
    public void scerePlan(@PathVariable
    Integer idFolder, @PathVariable
    Integer idStudy, @PathVariable
    Integer idRegime, Model model) {
	cereVariablen(idFolder, idStudy, idRegime, model);
	log.debug("ContextPath=" + getRequest().getContextPath());
	getRequest().getSession().setAttribute("regimePart", "plan");
    }

    private HttpServletRequest getRequest() {
	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
		.getRequest();
	return request;
    }

    @RequestMapping(value = "/f={idFolder}/s={idStudy}/cere-ed={idRegime}", method = RequestMethod.GET)
    public void scereEd(@PathVariable
    Integer idFolder, @PathVariable
    Integer idStudy, @PathVariable
    Integer idRegime, Model model) {
	cereVariablen(idFolder, idStudy, idRegime, model);
	getRequest().getSession().setAttribute("regimePart", "ed");
    }

    private void cereVariablen(Integer idFolder, Integer idStudy, Integer idRegime, Model model) {
	addIdFolder(idFolder, model);
	addIdStudy(idStudy, model);
	addIdRegime(idRegime, model);
	model.addAttribute("docId", idRegime);
    }

    @RequestMapping(value = "/doc-cere-ed={idRegime}", method = RequestMethod.GET)
    public void cereEd(@PathVariable
    Integer idRegime, Model model) {
	addIdRegime(idRegime, model);
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

    // Study
    @RequestMapping(value = "f={idFolder}/study-{studyPart}={idStudy}", method = RequestMethod.GET)
    public void docFStudy(@PathVariable
    Integer idFolder, @PathVariable
    String studyPart, @PathVariable
    Integer idStudy, Model model) {
	addIdFolder(idFolder, model);
	addIdStudy(idStudy, model);
	getRequest().getSession().setAttribute("studyPart", studyPart);
    }

    @RequestMapping(value = "/doc-study={id}", method = RequestMethod.GET)
    public void docStudy(@PathVariable
    Integer id, Model model) {
	log.debug("id=" + id);
	model.addAttribute(id);
    }

    @RequestMapping(value = "f={id}", method = RequestMethod.GET)
    public String toFolder(@PathVariable
    Integer id, Model model) {
	return fromId(id, model);
    }

    @RequestMapping(value = "f={idf}/p={id}", method = RequestMethod.GET)
    public String toPatient(@PathVariable
    Integer id, Model model) {
	return fromId(id, model);
    }

    @RequestMapping(value = "f={idf}/p={idp}/s={id}", method = RequestMethod.GET)
    public String toPatientStudy(@PathVariable
    Integer id, Model model) {
	return fromId(id, model);
    }

    @RequestMapping(value = "f={idf}/s={id}", method = RequestMethod.GET)
    public String toStudy(@PathVariable
    Integer id, Model model) {
	HttpServletRequest request = getRequest();
	log.debug("ContextPath=" + request.getContextPath());
	return fromId(id, model);
    }

    // Study END

    // Patient
    @RequestMapping(value = "f={idFolder}/patient={idPatient}", method = RequestMethod.GET)
    public void docFPatient(@PathVariable
    Integer idFolder, @PathVariable
    Integer idPatient, Model model) {
	addIdFolder(idFolder, model);
	addIdPatient(idPatient, model);
    }

    // Patient END

    private void addIdPatient(Integer idPatient, Model model) {
	if (!model.asMap().containsValue(idPatient))
	    model.addAttribute(idPatient);
    }

    private void addIdFolder(Integer idFolder, Model model) {
	if (!model.asMap().containsValue(idFolder))
	    model.addAttribute(idFolder);
    }

    private void addIdStudy(Integer idStudy, Model model) {
	if (!model.asMap().containsValue(idStudy))
	    model.addAttribute(idStudy);
    }

    private void addIdRegime(Integer idRegime, Model model) {
	if (!model.asMap().containsValue(idRegime))
	    model.addAttribute(idRegime);
    }

    // Folder
    @RequestMapping(value = "/folder={idFolder}", method = RequestMethod.GET)
    public void folder(@PathVariable
    Integer idFolder, Model model) {
	addIdFolder(idFolder, model);
	mopetService.setFolder(idFolder, model);
	Tree folderT = mopetService.getTree(idFolder);
	model.addAttribute("folderT", folderT);

    }

    // Folder END

    @RequestMapping(value = "id={id}", method = RequestMethod.GET)
    public String fromId(@PathVariable
    Integer id, Model model) {
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
	int idPatient = 2;
	boolean isPatient = id == idPatient;
	if (isPatient) {
	    idFolder = 1;
	    return "redirect:/f=" + idFolder + "/patient=" + idPatient;
	}
	int idConcept = 3;
	boolean isConcept = id == idConcept;
	if (isConcept) {
	    String study_Part_Id_Url = "/study-" + getStudyPart() + "=" + idConcept;
	    if (isPatient) {
		idPatient = 2;
		idFolder = 1;
		return "redirect:/f=" + idFolder + "/p=" + idPatient + study_Part_Id_Url;
	    }
	    if (!isPatient) {
		idFolder = 1;
		return "redirect:/f=" + idFolder + study_Part_Id_Url;
	    }
	}
	int idRegime = 4;
	boolean isRegime = id == idRegime;
	if (isRegime) {
	    idConcept = 3;
	    String cere_Part_Id_Url = "/cere-" + getRegimePart() + "=" + idRegime;
	    if (isPatient) {
		idPatient = 2;
		idFolder = 1;
		return "redirect:/f=" + idFolder + "/p=" + idPatient + "/s=" + idConcept + cere_Part_Id_Url;
	    }
	    if (!isPatient) {
		idFolder = 1;
		return "redirect:/f=" + idFolder + "/s=" + idConcept + cere_Part_Id_Url;
	    }
	}
	return "redirect:/";
    }

    private String getRegimePart() {
	String regimePart = (String) getRequest().getSession().getAttribute("regimePart");
	if (null == regimePart)
	    regimePart = "ed";
	return regimePart;
    }

    private String getStudyPart() {
	log.debug("----------------------- 2 1");
	String studyPart = (String) getRequest().getSession().getAttribute("studyPart");
	log.debug("----------------------- " + studyPart);
	if (null == studyPart)
	    studyPart = "sg";
	return studyPart;
    }
}
