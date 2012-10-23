function titlePane(tpId,isOpened){
	Spring.addDecoration(new Spring.ElementDecoration({
	elementId:tpId, widgetType:"dijit.TitlePane" ,widgetAttrs: {
		title:this.title,open:isOpened
	}
	}));
}

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
/**
 * Move menu item 
 * @param isMyClass
 * @param childId
 */
function moveMenuItem(isMyClass,childId){
	var ctxMenuName="contextMenuContainer";
	if(isMyClass){
		ctxMenuName="contextMenu";
	}
	var ctxMenu=dijit.byId(ctxMenuName);
	var childE=dijit.byId(childId);
	if(childE!=null){
		ctxMenu.addChild(childE);
	}
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
		moveMenuItem(hasClass(editE, "drug"),	"cmiDrugInDrug");
		moveMenuItem(isDrugDose||hasClass(editE, "day"),	"cmiAddRule");
		moveMenuItem(isNewDDNotice||isDrugDose,	"cmiAddDrugDoseNotice");
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
function cmiJaxb(){
	console.log("cmiJaxb "+getSelectedE().id);
	var targetNode = dojo.byId("copyClipboard");
	var jaxbNode = dojo.byId("jaxb");
	var urlRoot = dojo.byId('urlRoot').value;
	var xhrArgs = {
			url: urlRoot+"xml="+getSelectedE().id,
//			url: "http://imsel.imise.uni-leipzig.de:8081/sokrathes/xml=1327794",
			handleAs: "text",
			load: function(data){
				console.log("-data--"+data);
				dojo.require("dojox.xml.parser");
				var dom = dojox.xml.parser.parse(data);
				var jaxbId=dom.documentElement.getAttribute("id");
				targetNode.innerHTML = "<a href='"+urlRoot+"xml=x_"+jaxbId+"'>jaxbId"+jaxbId+"</a>";
				jaxbNode.appendChild(document
						.createTextNode("Jaxb XML: " + dojox.xml.parser
								.innerXML(dom.documentElement)));
//				dojo.require("dojox.xml.DomParser");
//				var jsdom=dojox.xml.DomParser.parse(data);
//				console.log(jsdom);
			},
			error: function(error){
				targetNode.innerHTML = "An unexpected error occurred: " + error;
			}
	}
	// Call the asynchronous xhrGet
	var deferred = dojo.xhrGet(xhrArgs);
	var n1=targetNode.childNodes[0]
	console.log("---"+n1);
//	alert(targetNode.childNodes.length);
}
function cmiCopy(){
	console.log("copy "+getSelectedE().id);
	var targetNode = dojo.byId("copyClipboard");
	var urlRoot = dojo.byId('urlRoot').value;
	var xhrArgs = {
			url: urlRoot+"copy?id="+getSelectedE().id,
			handleAs: "text",
			load: function(data){
				targetNode.innerHTML = data;
			},
			error: function(error){
				targetNode.innerHTML = "An unexpected error occurred: " + error;
			}
	}
	// Call the asynchronous xhrGet
	var deferred = dojo.xhrGet(xhrArgs);
	console.log("copy deferred="+deferred);
}
function cmiPaste(){
	console.log("paste "+getSelectedE().id);
	dojo.byId("pasteId").value=getSelectedE().id;
	dojo.byId("pasteSubmit").click();
}
//cmiDrugInDrug=newAdditionalDrug
function cmiDrugInDrug(){idtAction("cmiDrugInDrug");}
//cmiAddDrugDoseNotice=newDDNotice
//cmiAddDrugDoseNotice=newNotice
function cmiAddDrugDoseNotice()	{idtAction("cmiAddDrugDoseNotice");}
//cmiAddRule=function newRule()		{idtAction("newRule");}
function cmiAddRule()		{idtAction("cmiAddRule");}
function idtAction(action){
	var url="url?";
	var docIdE = dojo.byId('docId');
	url+="id="+docIdE.value;
	var selectedE=getSelectedE();
	url+="&idt="+selectedE.id.split("_")[1];
	url+="&a="+action;
	console.log(url);
	alert(url);
}
function correcturSelectElement(editE){
	for ( var i = 0; i < 4; i++) {
		if(dojo.hasClass(editE,"select"))
			break;
		editE=editE.parentNode;
	}
	return editE;
}
function idtClick(targetId){
	console.log("idt="+dojo.byId('idt').value);
	console.log("targetId="+targetId);
	dojo.byId('idt').value=targetId;
	console.log("idt="+dojo.byId('idt').value);
	dojo.byId('edDay').click();
	console.log("------------");
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
function menuItem(id,classes,click){
	Spring.addDecoration(new Spring.ElementDecoration({
		elementId:id, widgetType:"dijit.MenuItem", widgetModule:"dijit.Menu",
		widgetAttrs : {iconClass:classes, onClick: click}
	}));
}
