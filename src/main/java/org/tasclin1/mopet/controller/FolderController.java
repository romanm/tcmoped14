package org.tasclin1.mopet.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class FolderController {
    protected final Log log = LogFactory.getLog(getClass());

    @RequestMapping(value = "/folder={id}", method = RequestMethod.GET)
    public void folder(@PathVariable
    Integer id, Model model) {
	log.debug("id=" + id);
	model.addAttribute(id);
    }

}
