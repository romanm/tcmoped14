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
public class StudyController {
    protected final Log log = LogFactory.getLog(getClass());

    @RequestMapping(value = "/doc-study={id}", method = RequestMethod.GET)
    public void docStudy(@PathVariable
    Integer id, Model model) {
	log.debug("id=" + id);
	model.addAttribute(id);
    }

    @RequestMapping(value = "/s={id}", method = RequestMethod.GET)
    public String toStudy(@PathVariable
    Integer id, Model model) {
	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
		.getRequest();
	log.debug("ContextPath=" + request.getContextPath());
	log.debug("id=" + id);
	return "redirect:/doc-study=" + id;
	// return "redirect:doc-study=" + id;
    }

}
