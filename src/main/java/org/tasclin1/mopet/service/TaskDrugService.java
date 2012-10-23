package org.tasclin1.mopet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tasclin1.mopet.domain.Tree;
import org.tasclin1.mopet.regime.TaskDrugForm;

@Service("taskDrugService")
public class TaskDrugService {

    private MopetService mopetService;

    @Autowired
    public TaskDrugService(MopetService mopetService) {
	this.mopetService = mopetService;
    }

    public TaskDrugForm onStart(Tree drugT, Integer idt) {
	TaskDrugForm drugDayForm = new TaskDrugForm(drugT, idt);
	return drugDayForm;
    }

}
