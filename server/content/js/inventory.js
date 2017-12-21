function Inventory() {
	this.inventoryLevelsByDepartment = {}
	this.getInventoryLevels = function() {
		$.ajax({
			url:"api/kvs.php?function=GETPRODUCTSLEVELS",
			success:function(data) {
				if (!data.success) {
					bootstrap.alert("There has been an error");
					return;
				}
				$.each(data.products, function(key, product) {
					window.inventory.inventoryLevelsByDepartment[key] = product;
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
		$.each(this.inventoryLevelsByDepartment, function(departmentName, department) {
			var row = el("section", {class:"row selectable", style:("background:" + department["colour"] + ";")});
			var h4 = el("h4", {class:"italic", html:departmentName});
			var section = el("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12"});
			section.appendChild(h4);
			row.appendChild(section);
			delete department["colour"];
			$("#inventory-table").append(row);
			$.each(department, function(key, product) {
				var row = el("section", {class:"row product"});
				//name
				var section = el("section", {class:"col-lg-5 col-md-5 col-sm-8 col-xs-6"});
				var button = el("button", {class:"btn btn-default btn-lg product-btn", html:product.name, "data-id":key});
				section.appendChild(button);
				row.appendChild(section);
				//max stock
				var section = el("section", {class:"col-lg-3 col-md-3 col-sm-2 col-xs-3"});
				var p = el("h4", {class:"text-default clear-text", "data-id":key, html:product.max_stock});
				section.appendChild(p);
				row.appendChild(section);
				//in stock 
				var section = el("section", {class:"col-lg-3 col-md-3 col-sm-2 col-xs-3"});
				var p = el("h4", {class:"text-default clear-text", "data-id":key, html:product.current_stock});
				section.appendChild(p);
				row.appendChild(section);
				//Order
				var section = el("section", {class:"col-lg-1 col-md-1 hidden-sm hidden-xs"});
				var button = el("button", {class:"btn btn-info btn-lg order", html:"Order", "data-id":key});
				section.appendChild(button);
				row.appendChild(section);
				$("#inventory-table").append(row);
			});
		});
	}
}
