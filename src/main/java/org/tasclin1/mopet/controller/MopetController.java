package org.tasclin1.mopet.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
public class MopetController {

    protected final Log log = LogFactory.getLog(getClass());

    // ChemoRegime
    @RequestMapping(value = "/f={idFolder}/s={idStudy}/cere-ed={idRegime}", method = RequestMethod.GET)
    public void scereEd(@PathVariable
    Integer idRegime, @PathVariable
    Integer idStudy, @PathVariable
    Integer idFolder, Model model) {
	log.debug("idFolder=" + idFolder);
	model.addAttribute(idFolder);
	log.debug("idStudy=" + idStudy);
	model.addAttribute(idStudy);
	log.debug("idRegime=" + idRegime);
	model.addAttribute(idRegime);
    }

    @RequestMapping(value = "/doc-cere-ed={idRegime}", method = RequestMethod.GET)
    public void cereEd(@PathVariable
    Integer idRegime, Model model) {
	log.debug("idRegime=" + idRegime);
	model.addAttribute(idRegime);
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
    @RequestMapping(value = "f={idFolder}/study={idStudy}", method = RequestMethod.GET)
    public void docFStudy(@PathVariable
    Integer idFolder, @PathVariable
    Integer idStudy, Model model) {
	log.debug("idFolder=" + idFolder);
	model.addAttribute(idFolder);
	log.debug("idStudy=" + idStudy);
	model.addAttribute(idStudy);
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
	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
		.getRequest();
	log.debug("ContextPath=" + request.getContextPath());
	log.debug("id=" + id);
	return fromId(id, model);
    }

    // Study END

    // Patient
    @RequestMapping(value = "f={idFolder}/patient={idPatient}", method = RequestMethod.GET)
    public void docFPatient(@PathVariable
    Integer idFolder, @PathVariable
    Integer idPatient, Model model) {
	log.debug("idFolder=" + idFolder);
	model.addAttribute(idFolder);
	log.debug("idPatient=" + idPatient);
	model.addAttribute(idPatient);
    }

    // Patient END

    // Folder
    @RequestMapping(value = "/folder={idFolder}", method = RequestMethod.GET)
    public void folder(@PathVariable
    Integer idFolder, Model model) {
	log.debug("idFolder=" + idFolder);
	model.addAttribute(idFolder);
    }

    // Folder END
    @RequestMapping(value = "id={id}", method = RequestMethod.GET)
    public String fromId(@PathVariable
    Integer id, Model model) {
	int idFolder = 1;
	boolean isFolder = id == idFolder;
	if (isFolder) {
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
	    if (isPatient) {
		idPatient = 2;
		idFolder = 1;
		return "redirect:/f=" + idFolder + "/p=" + idPatient + "/study=" + idConcept;
	    }
	    if (!isPatient) {
		idFolder = 1;
		return "redirect:/f=" + idFolder + "/study=" + idConcept;
	    }
	}
	int idRegime = 4;
	boolean isRegime = id == idRegime;
	if (isRegime) {
	    idConcept = 3;
	    if (isPatient) {
		idPatient = 2;
		idFolder = 1;
		return "redirect:/f=" + idFolder + "/p=" + idPatient + "/s=" + idConcept + "/cere-ed=" + idRegime;
	    }
	    if (!isPatient) {
		idFolder = 1;
		return "redirect:/f=" + idFolder + "/s=" + idConcept + "/cere-ed=" + idRegime;
	    }
	}
	return "redirect:/";
    }
}