package org.tasclin1.mopet.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tasclin1.mopet.domain.Tree;
import org.tasclin1.mopet.jaxb.Appx;
import org.tasclin1.mopet.jaxb.Dayx;
import org.tasclin1.mopet.jaxb.Dosex;
import org.tasclin1.mopet.jaxb.Drugx;
import org.tasclin1.mopet.jaxb.Taskx;
import org.tasclin1.mopet.jaxb.Timesx;
import org.tasclin1.mopet.jaxb.Treex;
import org.tasclin1.mopet.service.MopetService;

@Controller
public class JaxbController {
    protected final Log log = LogFactory.getLog(getClass());
    @Autowired
    private MopetService mopetService;

    @RequestMapping(value = "/jaxb_drug2", method = RequestMethod.POST)
    public @ResponseBody
    String readXml2(@RequestBody
    String drugXml) {
	log.debug(drugXml);
	return "Read from XML: " + drugXml;
    }

    @RequestMapping(value = "/jaxb_drug", method = RequestMethod.POST)
    public @ResponseBody
    String readXml(@RequestBody
    Drugx drugXml) {
	log.debug(1);
	System.out.println("-------------------");
	System.out.println(drugXml);
	System.out.println(drugXml.getId());
	// System.out.println(drugXml.getDrug());
	// ArrayList<Dayx> day = drugXml.getDay();
	// for (Dayx dayx : day) {
	// System.out.println(dayx);
	// System.out.println(dayx.getAbs());
	// }
	// Dosex dose = drugXml.getDose();
	// System.out.println(dose);
	// System.out.println(dose.getId());
	// System.out.println(dose.getValue());
	// System.out.println(dose.getApp());
	return "Read from XML: " + drugXml;
    }

    @RequestMapping(value = "/xml={htmlId}", method = RequestMethod.GET, produces = "application/xml")
    public @ResponseBody
    Treex xml(@PathVariable
    String htmlId, Model model) {
	log.debug(1);
	Integer id = mopetService.getIdFromHtmlId(htmlId);
	log.debug(id);
	Tree t0 = mopetService.readNodes4(id, model);
	Treex mtlX = null;
	if (t0.isTask()) {
	    mtlX = regimeTaskx(t0);
	} else if (t0.isDrug()) {
	    mtlX = regimeDrugx(t0);
	} else if (t0.isDose()) {
	    mtlX = new Dosex(t0);
	} else if (t0.isDay()) {
	    mtlX = regimeDrugDay(t0);
	} else if (t0.isTimes()) {
	    mtlX = new Timesx(t0);
	} else {
	    log.info("TODO!");
	}
	return mtlX;
    }

    private Taskx regimeTaskx(Tree taskT) {
	Taskx taskx = new Taskx(taskT);
	for (Tree t1 : taskT.getChildTs())
	    if (t1.isDrug()) {
		taskx.getDrug().add(regimeDrugx(t1));
	    } else if (t1.isTask()) {
		taskx.getTask().add(regimeTaskx(t1));
	    }
	return taskx;
    }

    private Drugx regimeDrugx(Tree drugT) {
	Drugx drugx = new Drugx(drugT);
	for (Tree t1 : drugT.getChildTs())
	    if (t1.isDose()) {
		drugx.setDose(new Dosex(t1));
	    } else if (t1.isApp()) {
		drugx.setApp(new Appx(t1));
	    } else if (t1.isDay()) {
		drugx.getDay().add(regimeDrugDay(t1));
	    }
	return drugx;
    }

    private Dayx regimeDrugDay(Tree t1) {
	Dayx dayx = new Dayx(t1);
	for (Tree t2 : t1.getChildTs()) {
	    if (t2.isTimes()) {
		dayx.setTimes(new Timesx(t2));
	    }
	}
	return dayx;
    }

}
