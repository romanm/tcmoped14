package org.tasclin1.mopet.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ChemoregimeController {
    protected final Log log = LogFactory.getLog(getClass());

    @RequestMapping(value = "s={idStudy}/doc-cere-ed={id}", method = RequestMethod.GET)
    public void scereEd(@PathVariable
    Integer id, Model model) {
	log.debug("id=" + id);
	model.addAttribute(id);
    }

    @RequestMapping(value = "/doc-cere-ed={id}", method = RequestMethod.GET)
    public void cereEd(@PathVariable
    Integer id, Model model) {
	log.debug("id=" + id);
	model.addAttribute(id);
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

}
