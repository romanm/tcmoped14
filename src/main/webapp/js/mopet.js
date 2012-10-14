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
