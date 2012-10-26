package org.tasclin1.mopet.controller;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/newfolder")
@SessionAttributes("formBean")
public class NewFolderController {
    protected final Log log = LogFactory.getLog(getClass());

    @ModelAttribute("formBean")
    public NewFolderBean createFormBean() {
	return new NewFolderBean();
    }

    @RequestMapping(method = RequestMethod.GET)
    public void form() {
	log.debug(1);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@Valid
    NewFolderBean formBean, BindingResult result, Model model, RedirectAttributes redirectAttrs) {
	log.debug(formBean);
	log.debug("formBean.getName()=" + formBean.getName());
	log.debug(result.hasErrors());
	for (ObjectError objectError : result.getAllErrors()) {
	    log.debug(objectError);
	}
	if (result.hasErrors()) {
	    return null;
	    // return "newfolder";
	}
	return "redirect:/newfolder";
    }

}
