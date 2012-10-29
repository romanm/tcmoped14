package org.tasclin1.mopet.service;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

public class PasteFromRepositoryForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty
    String pasteId;

    public String getPasteId() {
	return pasteId;
    }

    public void setPasteId(String pasteId) {
	this.pasteId = pasteId;
    }

}
