function clear_labels() {
	if (!confirm("Are you sure you want to clear the labels?")) {
		return;
	}
	clear_labels_ajax();
}
function clear_labels_ajax() {
	var CONTEXT = "https://www.goldstandardresearch.co.uk/kvs/api/";
	$.ajax({
		url:CONTEXT + "kvs.php?function=CLEARLABELS",
		method:"POST",
		dataType:"JSON",
		success:function(data) {
			if (!data.success) {
				alert("Error removing labels");
				return;
			}
			location.reload();
		}
	});
}
function generatePropertiesTable(style) {
	var holder = $("#properties tbody")[0];
	$(holder).empty();
	$.each(style, function(selector, attributes) {
		var tr = el("tr", {class:"tr-bold", "data-id":selector});
		var td = el("td", {colspan:"2"});
		var label = el("label");
		label.appendChild(document.createTextNode(selector.replace(".", "")));
		td.appendChild(label);
		td.appendChild(el("i", {class:"fa fa-chevron-down pull-right"}));
		tr.appendChild(td);
		holder.appendChild(tr);
		$.each(attributes, function(attribute, value) {
			var tr = el("tr", {"data-id":selector, class:"hidden"});
			var td = el("td");
			td.innerHTML = attribute;
			tr.appendChild(td);
			var td = el("td");
			var input_group = el("section", {class:"input-group"});
			var input = el("input", {type:"text", class:"form-control attribute-value", id:attribute, value:value});
			if (css_template[attribute]) {
				if (Array.isArray(css_template[attribute])) {
					var select = el("select", {class:"form-control attribute-value", id:attribute, value:value});
					$.each(css_template[attribute], function(key, value) {
						var option = el("option");
						option.innerHTML = value;
						option.value = key;
						select.appendChild(option);
					});
					input = select;
				}
				else {
					if (css_template[attribute]["type"]) {
						input = el("input", {type:css_template[attribute]["type"], step:"any", class:"form-control attribute-value", id:attribute, value:value});
					}
				}
			}
			input_group.appendChild(input);
			if ((css_template[attribute]) && (css_template[attribute]["unit"])) {
				var span = el("span", {class:"input-group-addon"});
				span.innerHTML = css_template[attribute]["unit"];
				input_group.appendChild(span);
			}
			td.appendChild(input_group);
			tr.appendChild(td);
			holder.appendChild(tr);
		});
	});
	$("#properties tbody .tr-bold").on("click", function() {
		var id = $(this).attr("data-id");
		$("tr[data-id='" + id + "']:not(.tr-bold)").toggleClass("hidden");
	});
}
function print_document() {
	window.print();
}
function exportAsPdf() {
	$.ajax({
		url:"export_pdf.php",
		method:"POST",
		dataType:"JSON",
		data: {html:$("#viewport").html()},
		success:function(data) {
			if (!data.success) {
				return;
			}
			download("pdf/" + data.guid + ".pdf");
		}
	});
}
function download(file_path) {
	var a = document.createElement('A');
	a.href = file_path;
	a.download = file_path.substr(file_path.lastIndexOf('/') + 1);
	document.body.appendChild(a);
	a.click();
	document.body.removeChild(a);
}
function back_to_till() {
	window.location = "index.php";
}
function el(tagName, options) {
	var options = options || {};
	var el = document.createElement(tagName);
	if (options.id) {
		el.id = options.id;
		delete options.id;
	}
	if (options.class) {
		el.className = options.class;
		delete options.class;
	}
	$.each(options, function(key, value) {
		el.setAttribute(key, value);
	});
	return el;
}
function populate_style() {
	var holder = $("#label-style-select")[0];
	$(holder).empty();
	$.each(data, function(key, value) {
		var option = el("option");
		option.innerHTML = key;
		holder.appendChild(option);
	});
}
function formatMoney(amount, prefix) {
	var prefix = prefix || "";
	return accounting.formatMoney(amount, prefix);
}
function truncate(m, length) {
	return m.substring(0, length);
}
var css_template = {
	"padding":{"type":"number", "unit":"mm", "default":"0"},
	"padding-right":{"type":"number", "unit":"mm", "default":"0"},
	"padding-left":{"type":"number", "unit":"mm", "default":"0"},
	"padding-top":{"type":"number", "unit":"mm", "default":"0"},
	"padding-bottom":{"type":"number", "unit":"mm", "default":"0"},
	"float":["center", "left", "right"],
	"top":{"type":"number", "unit":"mm", "default":"0"},
	"bottom":{"type":"number", "unit":"mm", "default":"0"},
	"left":{"type":"number", "unit":"mm","default":"0"},
	"right":{"type":"number", "unit":"mm", "default":"0"},
	"display":["block"],
	"font-size":{"type":"number", "unit":"px", "default":"0"},
	"bottom":{"type":"number", "unit":"mm", "default":"0"},
	"margin":{"type":"number", "unit":"mm", "default":"0"},
	"margin-right":{"type":"number", "unit":"mm", "default":"0"},
	"margin-left":{"type":"number", "unit":"mm", "default":"0"},
	"margin-top":{"type":"number", "unit":"mm", "default":"0"},
	"margin-bottom":{"type":"number", "unit":"mm", "default":"0"},
	"height":{"type":"number", "unit":"mm", "default":"0"},
	"width":{"type":"number", "unit":"mm", "default":"0"},
	"text-align":["center", "left", "right"],
	"font-style":["bold", "italic", "normal"],
	"white-space":["no-wrap"],
	"position":["relative", "absolute", "fixed"],
	"overflow":["scroll", "hidden", "fixed", "none"],
	"color":{"type":"text", "default":"#000"},
	"border-width":{"type":"number", "unit":"px", "default":"0"},
	"border-color":{"type":"text"},
	"border-style":["solid", "dashed"],
	"max-height":{"type":"number", "unit":"mm", "default":"0"}
};
var data = {
	"Custom":{
		"page":{"labels":21, "barcode":true, "expiry":false, "departments":[]}, 
		".sheet":{"padding":"5", "padding-left":"10"}, 
		".product-label":{"float":"left", "top":"0", "text-align":"center", "left":"0", "max-height":"37", "width":"60", "margin-right":"3", "margin-bottom":"1", "border-width":"0.5", "border-color":"blue", "border-style":"solid", "height":"37"},
		".product-name":{"font-size":"18", "left":"0", "text-align":"center", "padding-left":"2", "padding-right":"2", "color": "#000", "display": "block", "font-family": "serif", "font-size": "20", "font-style": "normal", "font-weight": "normal", "left": "5", "margin-left": "auto", "margin-right": "auto", "overflow": "hidden", "position": "relative", "text-align": "center", "top": "10", "white-space": "nowrap"}, 
		".product-price":{"font-size":"64", "bottom":"3","display":"block","text-align":"center","margin-top":"10","margin-left":"auto","margin-right":"auto","font-size":"72","font-family":"serif","font-style":"normal","color":"#000","font-weight":"normal"},
		".barcode":{"width":"62.5", "margin-left":"auto", "margin-right":"auto", "height":"12.5", "text-align":"center"}, 
	}, 
	"Avery L7160":{
		"page":{"labels":21, "barcode":true, "expiry":true, "departments":['5b82fdc8-7b71-11e7-b34e-426562cc935f', '5b82ffeb-7b71-11e7-b34e-426562cc935f']}, 
		".sheet":{"padding-left":"8", "padding-right":"8", "padding-bottom":"17", "padding-top":"15"}, 
		".sheet-inner":{"padding-left":"0"},
		".product-label":{"float":"left", "width":"63.5", "height":"38.1", "border-radius":"5", "border-width":"0.1", "border-color":"black", "border-style":"solid", "margin-right":"2.5", "text-align":"center"}, 
		".product-name":{"font-size":"16", "left":"0", "padding-left":"2", "padding-right":"2", "color": "#000", "display": "block", "font-family": "serif", "font-size": "20", "font-style": "normal", "font-weight": "normal", "left": "0.5", "margin-left": "auto", "margin-right": "auto", "overflow": "hidden", "position": "relative", "text-align": "center", "top": "10", "white-space": "nowrap"}, 
		".product-price":{"font-size":"16", "bottom":"3","display":"block","text-align":"center","margin-top":"1","margin-left":"auto","margin-right":"auto","font-family":"serif","font-style":"normal","color":"#000","font-weight":"normal"}, 
		".barcode":{"width":"62.5", "margin-left":"auto", "margin-right":"auto", "height":"12.5", "text-align":"center"}, 
		".product-expiry":{"width":"100", "text-align":"center", "font-size":"14", "margin-left":"0", "margin-right":"auto", "margin-bottom":"0"}
	}
};
$(document).ready(function() {
	populate_style();
	$.ajaxSetup({
		dataType:"JSON", 
		method:"POST"
	});
	$("#label-style-select").on("change", function() {
		var style = data[$(this).val()];
		get_labels(style);
	});
	$("input.attribute-value").on("change", function() {
		alert($(this).val());
	});
	get_labels(data["Custom"]);
});
function get_labels(style) {
	generatePropertiesTable(style);
	$.ajax({
		url:"api/temp/labels.php",
		dataType:"JSON", 
		method:"POST",
		data:{"json":JSON.stringify({"departments":style.page.departments})},
		success:function(data) {
			$("#viewport").empty();
			var holder = $("#viewport")[0];
			var numOfResults = data.length;
			var newPage = true;
			var endPage = false;
			var i = 0;
			var page;
			var page_article;
			var page_inner;
			var labelsPerPage = style.page.labels;
			if (numOfResults == 0) {
				alert("No Products added");
			}
			$.each(data, function(key, item) {
				if (newPage) {
					page = el("section", {class:"sheet"});
					page_article = el("article", {class:"sheet-inner"});
					newPage = false;
					i = 0;
				}
				var productLabel = el("section", {class:'product-label'});
				var productName = el("span", {class:"product-name"});
				var format;
				if (item["barcode"].length == 13) {
					format = "EAN13";
				}
				else {
					format = "EAN8";
				}
				var productExpiry = el("label", {class:"product-expiry"});
				productExpiry.innerHTML = "Use By ________________";
				var barcode = el("img", {class:"barcode", "jsbarcode-format":"CODE39", "jsbarcode-value":item["barcode"], "jsbarcode-textmargin":"0"});
				productName.innerHTML = truncate(item['name'], 18);
				var productPrice = el("section", {class:"product-price"});
				productPrice.innerHTML = formatMoney(item['price'],"£");
				productLabel.appendChild(productName);
				productLabel.appendChild(productPrice);
				if (style.page.expiry) {
					productLabel.appendChild(productExpiry);
				}
				productLabel.appendChild(barcode);
				page_article.appendChild(productLabel);
				page.appendChild(page_article);
				if ((++i % labelsPerPage == 0) || (i === numOfResults)) {
					endPage = true;
				}
				if (endPage) {
					holder.appendChild(page);
					endPage = false;
					newPage = true;
				}
			});
		},
		complete:function() {
			$.each(style, function(css_selector, value) {
				$.each(value, function(attribute, value) {
					if (css_selector.indexOf(".") > -1) {
						if ((css_template[attribute]) && (css_template[attribute]["unit"])) {
							$(css_selector).css(attribute, value + css_template[attribute]["unit"]);
						}
						else {
							$(css_selector).css(attribute, value);
						}	
					}
				});
			});
			if (style.page.barcode) {
				JsBarcode(".barcode").init();
			}
		}
	});
}
