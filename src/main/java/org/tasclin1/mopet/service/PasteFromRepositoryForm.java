package org.tasclin1.mopet.service;

import java.io.Serializable;

public class PasteFromRepositoryForm implements Serializable {
    private static final long serialVersionUID = 1L;
    Integer pasteId;

    public Integer getPasteId() {
	return pasteId;
    }

    public void setPasteId(Integer pasteId) {
	this.pasteId = pasteId;
    }

}
