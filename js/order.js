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
	$.each(options, function(key, value) {
		el.setAttribute(key, value);
	});
	return el;
}
function getCurrentOrderId() {
	return window.orderId;
}
function setCurrentOrderId(id) {
	window.orderId = id;
}
function getCurrentOrder() {
	return window.orders[getCurrentOrderId()];
}
function setCurrentOrder(order) {
	window.orders[getCurrentOrderId()] = order;
}
function addProductToCurrentOrder(product) {
	getCurrentOrder().products[product.id] = product;
	refreshTable();
}
function getOrders() {
	$.ajax({
		url:"api/kvs.php?function=GETPRODUCTSLEVELS",
		success:function(data) {
			if (!data.success) {
				bootstrap.alert("There has been an error");
				return;
			}
			$.each(data.products, function(key, product) {
				console.log(product);
				addProductToCurrentOrder(product);
			});
			refreshTable();
		}
	});
}
$(document).ready(function() {
	$.ajaxSetup({
		method:"POST",
		dataType:"JSON"
	});
	window.orders = {};
	setCurrentOrderId("alpha");
	setCurrentOrder({"products":{}});
	getOrders();
});
function refreshTable() {
	$("#table").empty();
	$.each(getCurrentOrder().products, function(key, product) {
		var row = el("section", {class:"row product"});
		row.style.padding = "5px";
		//bin
		var section =  el("section", {class:"col-md-1"});
		var button = el("button", {class:"btn btn-danger btn-lg delete", "data-id":key});
		button.onclick = function() {
			getTransaction().removeProduct($(this).attr("productID"));
		};
		button.appendChild(el("i", {class:"fa fa-times"}));
		section.appendChild(button);
		row.appendChild(section);
		//name
		var section = el("section", {class:"col-md-5"});
		var button = el("button", {class:"btn btn-default btn-lg", html:product.name});
		section.appendChild(button);
		row.appendChild(section);
		//max stock
		var section = el("section", {class:"col-md-1"});
		var p = el("h4", {class:"text-default clear-text", "data-id":key, html:product.max_stock});
		section.appendChild(p);
		row.appendChild(section);
		//in stock 
		var section = el("section", {class:"col-md-1"});
		var p = el("h4", {class:"text-default clear-text", "data-id":key, html:product.current_stock});
		section.appendChild(p);
		row.appendChild(section);
		//Order
		var section = el("section", {class:"col-md-1"});
		var button = el("button", {class:"btn btn-info btn-lg", html:"Order"});
		section.appendChild(button);
		row.appendChild(section);
		//max display
		var section = el("section", {class:"col-md-1"});
		var p = el("h4", {class:"text-default clear-text", "data-id":key, html:product.max_display});
		section.appendChild(p);
		row.appendChild(section);
		//on display 
		var section = el("section", {class:"col-md-1"});
		var p = el("h4", {class:"text-default clear-text", "data-id":key, html:product.current_display});
		section.appendChild(p);
		row.appendChild(section);
		$("#table").append(row);
		//Refill Display
		var section = el("section", {class:"col-md-1"});
		var button = el("button", {class:"btn btn-success btn-lg", html:"Refilled"});
		section.appendChild(button);
		row.appendChild(section);
	});
}
