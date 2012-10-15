function init(){
	console.log("init BEGIN");
	// make select only for contextmenu, only by right maus klick, make not select for link maus klick
	dojo.query(".select").connect("oncontextmenu", function(e) {
		var editE=correcturSelectElement(e.target);
		if(dojo.hasClass(editE,"selected")){
			dojo.removeClass(editE, "selected");
			return;
		}
		dojo.query(".selected").removeClass("selected");
		dojo.addClass(editE, "selected");
	});
	console.log("init END");
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
function copy(){
	console.log(1);
}
function paste(){
	console.log(2);
}
function menuItem(id,classes,click){
	Spring.addDecoration(new Spring.ElementDecoration({
		elementId:id, widgetType:"dijit.MenuItem", widgetModule:"dijit.Menu",
		widgetAttrs : {iconClass:classes, onClick: click}
	}));
}
