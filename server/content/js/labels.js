var selectedProducts = [];
const CONTEXT = "api/";
$(document).ready(function() {
    
    $('ul.setup-panel li a').on("click", function(e) {
    	$(".setup-content").addClass("hidden");
    	$("#" + $(this).attr("data-id")).removeClass("hidden");
    	$('ul.setup-panel li').removeClass('active');
        $(this).closest("li").addClass("active");
    });
    
    $('#activate-step-3').on('click', function(e) {
    	$("ul.setup-panel li a[data-id='step-3']").click();
    	generatePDF();
    })    
});
function getProductFromServer() {
	var barcode = $("#barcode").val();
	$("#barcode").val("");
	if (isProductSelected(barcode)){
		$(".offline-banner").removeClass("hidden");
		return;
	}
	$(".offline-banner").addClass("hidden");
	$.ajax({
		url: CONTEXT + "kvs.jsp?function=BARCODE",
		data : {number : barcode},
		dataType: "JSON",
		success: function(data) {
			if (data.isCase) {
				alert("This appears to be a case barcode");
			}
			var tempProduct = new Product();
			tempProduct.id = data.id;
			tempProduct.cost = parseFloat(data.price);
			tempProduct.updated = parseInt(data.updated);
			tempProduct.inDatabase = true;
			tempProduct.barcode = data.barcode;
			tempProduct.department = data.department;
			tempProduct.name = data.name;
			tempProduct.inDatabase = true;
			tempProduct.maxStock = parseInt(data.max_stock);
			tempProduct.currentStock = parseInt(data.current_stock);
			addProduct(tempProduct);
		}
	});
	return false;
}
function generatePDF() {
	$.ajax({
		url: CONTEXT + "kvs.jsp?function=GENERATELABELSPDF",
		data : {"products[]" : getSelectedProductIds(), "includeLabelled":$("#include-labelled-products").is(":checked")},
		method:"POST",
		dataType: "JSON",
		success: function(data) {
			if (!data.success) {
				$("#downloading-pdf-error-panel").removeClass("hidden");
				return;
			}
			$("#downloading-pdf-success-panel").removeClass("hidden");
			$("#labels-alt-download").attr("href", data.file);
			window.open(data.file, 'Download');
		},
		complete: function() {
			$("#downloading-pdf-panel").addClass("hidden");
		}
	});
}
function getSelectedProductIds() {
	var ids = [];
	$.each(getSelectedProducts(), function(key, value) {
		if (isUndefined(value)) {
			return;
		}
		ids.push(value.id);
	});
	return ids;
}
function isProductSelected(barcode) {
	var exists = false;
	$.each(selectedProducts, function(key, value) {
		if (isUndefined(value)) {
			return;
		}
		if (value.barcode == barcode) {
			exists = true;
		}
	});
	return exists;
}
function addProduct(product) {
	selectedProducts.push(product);
	displaySelectedProducts();
}
function getSelectedProducts() {
	return selectedProducts;
}
function removeProduct(id) {
	$.each(selectedProducts, function(key, value) {
		if (isUndefined(value)) {
			return;
		}
		if (value.id == id) {
			selectedProducts[key] = undefined;
			displaySelectedProducts();
		}
	});
	$(".offline-banner").addClass("hidden");
}
function isUndefined(m) {
	return m == undefined;
}
function displaySelectedProducts() {
	var holder = $("#table");
	$(holder).empty();
	var count = 0;
	$.each(getSelectedProducts(), function(key, product) {
		if (isUndefined(product)) {
			return;
		}
		count++;
		var row = el("section", {class:"row"});
		row.style.padding = "5px";
		//bin
		var section =  el("section", {class:"col-lg-1 col-md-1 col-sm-1 col-xs-1"});
		var button = el("button", {class:"btn btn-danger delete", productID:product.id});
		button.onclick = function() {
			removeProduct($(this).attr("productID"));
		};
		button.appendChild(el("i", {class:"fa fa-times"}));
		section.appendChild(button);
		row.appendChild(section);
		//name
		var section = el("section", {class:"col-lg-10 col-md-10 col-sm-10 col-xs-10"});
		var button = el("button", {class:"btn btn-default product-button pull-left", html:product.name, "data-id":product.id});
		if (!product.inDatabase)
			button.disabled = "disabled";
		section.appendChild(button);
		row.appendChild(section);
		$("#table").append(row);
	});
	if (count == 0) {
		$("#no-goods").removeClass("hidden");
		$("#table-holder").addClass("hidden");
		return;
	}
	$("#no-goods").addClass("hidden");
	$("#table-holder").removeClass("hidden");
	$("#table").on("click", ".product-button", function() {
		showMenu($(this).attr("data-id"));
	});
	$('#table-holder').jScrollPane({
		verticalGutter:-16,
		animateScroll: true
	});
	$("#table-holder").data("jsp").scrollToY(99999); 
}
function el(tagName, options) {
	if (tagName.length == 0) {
		return;
	}
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
	if (options.html) {
		el.innerHTML = options.html;
		delete options.html;
	}
	if (options.text) {
		el.appendChild(document.createTextNode(options.text));
		delete options.text;
	}
	$.each(options, function(key, value) {
		el.setAttribute(key, value);
	});
	return el;
}