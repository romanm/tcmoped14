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
function menuSeparator(id){
	Spring.addDecoration(new Spring.ElementDecoration({
		elementId:id, widgetType:"dijit.MenuSeparator", widgetModule:"dijit.Menu"
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
function cmiJaxbPaste(){
//	console.log("cmiJaxbPaste "+getSelectedE().id);
	console.log("cmiJaxbPaste ");
	var targetNode = dojo.byId("copyClipboard");
	var xhrArgs = {
//		form: dojo.byId("readXml"),
		//type: "POST",
//		url: urlRoot+"jaxb_drug",
		url: "/tcm14/jaxb_drug",
		//url: "/tcm14/jaxb_drug2",
		postData:"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
		"<drug drug=\"Avastin\" did=\"1327794\" id=\"1339827\" idclass=\"305778\"" +
		" sort=\"1274940692641\">" +
		"<day abs=\"1,8\" newtype=\"a\" did=\"1339827\" id=\"1339836\"" +
		" idclass=\"59538\" sort=\"-1328725113655\">" +
		"<times did=\"1339836\" id=\"1339837\" sort=\"1274940692649\"/></day>" +
		"<dose app=\"i.v.\" pro=\"\" type=\"p\" unit=\"mg/kg\" value=\"15.0\"" +
		" did=\"1339827\" id=\"1339828\" idclass=\"332290\" sort=\"1274940692646\"/>" +
		"</drug>",
		contentType: "application/xml", dataType: "text",
		load: function(data){
			console.log("-data--"+data);
		},
		error: function(error){
			targetNode.innerHTML 
			= "An unexpected error occurred: " + error;
		}
	}
	var deferred = dojo.xhrPost(xhrArgs);
	var n1=targetNode.childNodes[0]
	console.log("---"+n1);
}
function testPasteFromRepository(){
	console.log("testPasteFromRepository");
	var pasteId = dojo.byId("pasteId");
	console.log(pasteId);
	console.log(pasteId.value);
}
function cmiPasteFromRepository(){
	console.log("cmiPasteFromRepository");
	var node_aPasteFromRepository = dojo.byId("aPasteFromRepository");
	var selectedE = getSelectedE();
	var href=node_aPasteFromRepository.getAttribute("href")
	+"idt="+selectedE.id.split("_")[1];
	console.log("href "+href);
	node_aPasteFromRepository.setAttribute("href",href);
	node_aPasteFromRepository.click();
}
function cmiCopyIdForLocal(){
	var selectedE = getSelectedE();
	alert("Id for paste local server: "+selectedE.id);
//	alert("Id for paste local server: "+selectedE.id.split("_")[1]);
}
function cmiJaxb(){
	console.log("cmiJaxb "+getSelectedE().id);
	var copyIdForLocalNode = dojo.byId("copyIdForLocal");
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
				copyIdForLocalNode.innerHTML =""+getSelectedE().id;
				dojo.addClass(copyIdForLocalNode, "notice");
				targetNode.innerHTML = "<a href='"+urlRoot+"xml=x_"+jaxbId+"'>jaxbId"+jaxbId+"</a>";
				jaxbNode.appendChild(document
						.createTextNode("Jaxb XML: " + dojox.xml.parser
								.innerXML(dom.documentElement)));
//				dojo.require("dojox.xml.DomParser");
//				var jsdom=dojox.xml.DomParser.parse(data);
//				console.log(jsdom);
			},
			error: function(error){
				targetNode.innerHTML 
				= "An unexpected error occurred: " + error;
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
function cmiDown(){
	dojo.byId("up_down").value="down";
	cmiFormClick("order");
}
function cmiUp(){
	dojo.byId("up_down").value="up";
	cmiFormClick("order");
}
function cmiPaste(){
	cmiFormClick("paste");
}
function cmiDelete(){
	cmiFormClick("delete");
}
function cmiFormClick(formName){
	console.log(formName+" "+getSelectedE().id);
	dojo.byId(formName+"Id").value=getSelectedE().id;
	dojo.byId(formName+"Submit").click();
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
function openDay(targetId){
	dojo.byId('idt').value=targetId;
	dojo.byId('edDay').click();
	console.log("------------");
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
