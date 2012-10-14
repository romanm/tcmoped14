package org.tasclin1.mopet.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.tasclin1.mopet.regime.DrugDayForm;

@Service("regimedrugService")
@Repository
public class RegimeDrugService {
    protected final Log log = LogFactory.getLog(getClass());

    public DrugDayForm getDrugDayForm() {
	return new DrugDayForm();
    }

}