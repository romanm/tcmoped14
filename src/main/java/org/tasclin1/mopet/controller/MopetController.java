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
    }

    // copy&paste
    @RequestMapping(value = "/copy", method = RequestMethod.GET)
    public @ResponseBody
    String writeString() {
	return "Wrote a string";
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
    @RequestMapping(value = "/f={idFolder}/p={idPatient}/s={idStudy}/cere-{regimePart}={idRegime}", method = RequestMethod.GET)
    public void folderPatientConceptRegime(@PathVariable
    Integer idFolder, @PathVariable
    Integer idPatient, @PathVariable
    Integer idStudy, @PathVariable
    String regimePart, @PathVariable
    Integer idRegime, Model model) {
	mopetService.readPatientDocShort(idPatient, model);
	readRegime(idFolder, idStudy, regimePart, idRegime, model);
    }

    // Patient ChemoRegime END
    // ChemoRegime
    private void readRegime(Integer idFolder, Integer idStudy, String regimePart, Integer idRegime, Model model) {
	mopetService.readFolderO2doc(idFolder, model);
	mopetService.readConceptT(idStudy, model);
	mopetService.readRegimeDocT(idRegime, model);
	mopetService.initRegimeDocT(model);
	getRequest().getSession().setAttribute("regimePart", regimePart);
    }

    @RequestMapping(value = "/f={idFolder}/s={idStudy}/cere-{regimePart}={idRegime}", method = RequestMethod.GET)
    public void folderConceptRegime(@PathVariable
    Integer idFolder, @PathVariable
    Integer idStudy, @PathVariable
    String regimePart, @PathVariable
    Integer idRegime, Model model) {
	readRegime(idFolder, idStudy, regimePart, idRegime, model);
    }

    private HttpServletRequest getRequest() {
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
	mopetService.setPatientO(idPatient, model);
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
	mopetService.readFolderO2doc(idFolder, model);
	mopetService.readConceptDocT(idStudy, model);
	getRequest().getSession().setAttribute("studyPart", studyPart);
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

    // Patient
    @RequestMapping(value = "f={idFolder}/patient={idPatient}", method = RequestMethod.GET)
    public void folderPatient(@PathVariable
    Integer idFolder, @PathVariable
    Integer idPatient, Model model) {
	mopetService.readFolderO2doc(idFolder, model);
	mopetService.setPatientO(idPatient, model);
    }

    // Patient END

    // Folder
    @RequestMapping(value = "/folder={idFolder}", method = RequestMethod.GET)
    public void folder(@PathVariable
    Integer idFolder, Model model) {
	mopetService.readFolderO2folder(idFolder, model);
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
	    String study_Part_Id_Url = "/study-" + getStudyPart() + "=" + idConcept;
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
	    String cere_Part_Id_Url = "/cere-" + getRegimePart() + "=" + idRegime;
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

    private String getRegimePart() {
	String regimePart = (String) getRequest().getSession().getAttribute("regimePart");
	if (null == regimePart)
	    regimePart = "ed";
	return regimePart;
    }

    private String getStudyPart() {
	String studyPart = (String) getRequest().getSession().getAttribute("studyPart");
	if (null == studyPart)
	    studyPart = "sg";
	return studyPart;
    }
}
