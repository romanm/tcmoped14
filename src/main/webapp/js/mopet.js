function makeContextMenu(classDoseArray){
	Spring.addDecoration(new Spring.ElementDecoration({
		elementId : "contextMenu",
		widgetType : "dijit.Menu",
		widgetAttrs : {
			style : "display: none;"
			,targetNodeIds : classDoseArray
		}
	}));
}
function init(){
	console.log("init BEGIN");
	// make select only for contextmenu, only by right maus klick, make not select for link maus klick
	dojo.query(".select").connect("oncontextmenu", function(e) {
		var editE=correcturSelectElement(e.target);
		if(dojo.hasClass(editE,"selected")){
			dojo.removeClass(editE, "selected");
		}
		dojo.query(".selected").removeClass("selected");
		dojo.addClass(editE, "selected");
		//move menu item
		var isDrugDose		= hasClass(editE, "drug")||hasClass(editE, "dose");
		var isNewDDNotice	= hasClass(editE, "dose")||hasClass(editE, "day");
		var isMiUpdate		= isDrugDose||isNewDDNotice
		||hasClass(editE, "taskNotice")||hasClass(editE, "schema");
		moveMenuItem(isMiUpdate,				"mi_cmus1");
		moveMenuItem(isDrugDose,				"cmNewAdditionalDrug");
		moveMenuItem(isDrugDose||hasClass(editE, "day"),	"cmNewRule");
		moveMenuItem(isNewDDNotice||isDrugDose,	"cmNewDDNotice");
		moveMenuItem(hasClass(editE, "schema"),	"mi_editCycle");
		moveMenuItem(hasClass(editE, "taskNotice"),	"addNotice");
		moveMenuItem(hasClass(editE, "schema"),		"mi_newDrug");
		moveMenuItem(hasClass(editE, "schema"),		"mi_newSupportDrug");
		moveMenuItem(hasClass(editE, "schema"),		"mi_newOnDemandDrug");
		moveMenuItem(hasClass(editE, "arm"),		"cmArmName");
		moveMenuItem(hasClass(editE, "cmDayDelay"),	"cmDayDelay");
		moveMenuItem(isDrugDose,				"innerTask");
		//move menu item END
	});
	console.log("init END");
}
function hasClass(editE,className){return dojo.hasClass(editE,className);}
/**
 * Move menu item 
 * @param isMyClass
 * @param childId
 */
function moveMenuItem(isMyClass,childId){
	var ctxMenuName="ctxMenuUpdate";
	if(isMyClass){
		ctxMenuName="contextMenu";
	}
	var ctxMenu=dijit.byId(ctxMenuName);
	var childE=dijit.byId(childId);
	if(childE!=null){
		ctxMenu.addChild(childE);
	}
}
function newAdditionalDrug(){idtAction("newAdditionalDrug");}
function newDDNotice()	{idtAction("newNotice");}
function newRule()		{idtAction("newRule");}
function idtAction(action){
	var idcE = dojo.byId('idc');
	console.log(idcE);
	console.log(idcE.value);
	//selfHref(action,'&idt='+idcE.value);
	var url="#";
	var wlsp=window.location.search.split("execution=");
	if(wlsp.length==1){
		url=getUrl1() + '?id='+docId+'&a='+action+'&idt='+idcE.value;
		//url=getUrl1() + '?id='+docId+'&a='+action+'&part='+part+'&idt='+idcE.value;
	}else{
		//url='?execution=' + wlsp[1] + '&_eventId=' + action + addUrlKey;
		dojo.byId('idt').value=idcE.value;
		dojo.byId('action').value=action;
		dojo.byId('editStep').click();
	}
	self.location.href=url;
}
function correcturSelectElement(editE){
	for ( var i = 0; i < 4; i++) {
		if(dojo.hasClass(editE,"select")){
			break;
		}
		editE=editE.parentNode;
	}
	return editE;
}
function drugFormButton(elementId){
	Spring.addDecoration(new Spring.AjaxEventDecoration({
		elementId:elementId,event:'onclick',formId:'regimeDrugForm'
			,params:{fragments:"body"}}));
}
function link2modalDialog(elementId){
	Spring.addDecoration(new Spring.AjaxEventDecoration({
		elementId : elementId,
		event : "onclick",
		popup : true,
		params : {
			fragments : "body",
			mode : "embedded"
		}
	}));
}
function getSelectedE(){return dojo.query(".selected")[0];}
function copy(){
	console.log("copy "+getSelectedE().id);
}
function paste(){
	console.log("paste "+getSelectedE().id);
}
function menuItem(id,classes,click){
	Spring.addDecoration(new Spring.ElementDecoration({
		elementId:id, widgetType:"dijit.MenuItem", widgetModule:"dijit.Menu",
		widgetAttrs : {iconClass:classes, onClick: click}
	}));
}
