function Inventory() {
	this.products = {};
	this.getInventoryLevels = function() {
		$.ajax({
			url:"api/kvs.php?function=GETPRODUCTSLEVELS",
			success:function(data) {
				if (!data.success) {
					bootstrap.alert("There has been an error");
					return;
				}
				$.each(data.products, function(key, product) {
					window.inventory.products[key] = product;
				});
				window.inventory.refreshTable();
			}
		});
	}
	this.showProduct = function(id) {
		alert(id);
	}
	this.init = function() {
		$.ajaxSetup({
			method:"POST",
			dataType:"JSON"
		});
		this.getInventoryLevels();
		$("#inventory-table").on("click", ".product-btn", function() {
			showProduct(this.getAttribute("data-id"));
		});
		$("#inventory-table").on("click", ".order", function() {
			window.orders.orderproduct(this.getAttribute("data-id"));
		});
	}
	this.refreshTable = function() {
		$("#inventory-table").empty();
		$.each(this.products, function(key, product) {
			var row = el("section", {class:"row product"});
			//bin
			var section =  el("section", {class:"col-lg-1 col-md-1 col-sm-1 col-xs-1"});
			var button = el("button", {class:"btn btn-danger btn-lg delete", "data-id":key});
			button.onclick = function() {
				getTransaction().removeProduct($(this).attr("productID"));
			};
			button.appendChild(el("i", {class:"fa fa-times"}));
			section.appendChild(button);
			row.appendChild(section);
			//name
			var section = el("section", {class:"col-lg-6 col-md-6 col-sm-8 col-xs-6"});
			var button = el("button", {class:"btn btn-default btn-lg product-btn", html:product.name, "data-id":key});
			section.appendChild(button);
			row.appendChild(section);
			//max stock
			var section = el("section", {class:"col-lg-2 col-md-2 col-sm-1 col-xs-2"});
			var p = el("h4", {class:"text-default clear-text", "data-id":key, html:product.max_stock});
			section.appendChild(p);
			row.appendChild(section);
			//in stock 
			var section = el("section", {class:"col-lg-2 col-md-2 col-sm-1 col-xs-2"});
			var p = el("h4", {class:"text-default clear-text", "data-id":key, html:product.current_stock});
			section.appendChild(p);
			row.appendChild(section);
			//Order
			var section = el("section", {class:"col-lg-1 col-md-1 col-sm-1 col-xs-1 hidden-xs"});
			var button = el("button", {class:"btn btn-info btn-lg order", html:"Order", "data-id":key});
			section.appendChild(button);
			row.appendChild(section);
			$("#inventory-table").append(row);
		});
	}
}
