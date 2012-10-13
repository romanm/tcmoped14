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
