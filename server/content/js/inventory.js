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
		$("#inventory-export-btn").click(function() {
			var holder = document.getElementById("inventory-departments-export");
			$(holder).empty();
			$.each(window.departmentNames, function(id, department) {
				var li = el("li");
				var label = el("label", {"for":"inventory-checkbox-" + id});
				var input = el("input", {"id":"inventory-checkbox-" + id, type:"checkbox", checked:true, "data-id":department.id});
				label.appendChild(input);
				label.appendChild(document.createTextNode(department.name));
				li.appendChild(label);
				holder.appendChild(li);
			});
			$("#export-inventory").modal("show");
		});
		$("#inventory-departments-export").on("change", "input[type=checkbox]", function() {
			if ($("#inventory-departments-export input[type=checkbox]:checked").length == 0) {
				$("#inventory-export").attr("disabled", true);
			}
			else {
				$("#inventory-export").attr("disabled", false);
			}
		});
		$("#inventory-export").click(function() {
			var selectedDepartments = [];
			$("#inventory-departments-export input[type=checkbox]:checked").each(function() {
				selectedDepartments.push(this.getAttribute("data-id"));
			});
			$.ajax({
				url:"api/kvs.php?function=GENERATEINVENTORYREPORT",
				dataType: "JSON",
				data:{"export-type":$("#inventory-export-type").val(), "departments":selectedDepartments},
				success: function(data) {
					if (!data.success) {
						alert("Error exporting inventory");
						return;
					}
				}
			});
		});
	}
	this.refreshTable = function() {
		$("#inventory-table").empty();
		$.each(this.inventoryLevelsByDepartment, function(departmentKey, department) {
			var row = el("section", {class:"row selectable inventory-department", style:("border-left:15px solid " + department["colour"] + ";"), "data-id":departmentKey});
			var h4 = el("h4", {class:"italic", html:window.departments.departmentsList[departmentKey]});
			var section = el("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12"});
			section.appendChild(h4);
			row.appendChild(section);
			delete department["colour"];
			$("#inventory-table").append(row);
			$.each(department, function(productKey, product) {
				var row = el("section", {class:"row product hidden", "data-id":departmentKey});
				//name
				var section = el("section", {class:"col-lg-5 col-md-5 col-sm-8 col-xs-6"});
				var button = el("button", {class:"btn btn-default btn-lg product-btn", html:product.name, "data-id":productKey});
				section.appendChild(button);
				row.appendChild(section);
				//max stock
				var section = el("section", {class:"col-lg-3 col-md-3 col-sm-2 col-xs-3"});
				var p = el("h4", {class:"text-default clear-text", html:product.max_stock});
				section.appendChild(p);
				row.appendChild(section);
				//in stock 
				var section = el("section", {class:"col-lg-3 col-md-3 col-sm-2 col-xs-3"});
				var p = el("h4", {class:"text-default clear-text", html:product.current_stock});
				section.appendChild(p);
				row.appendChild(section);
				//Order
				var section = el("section", {class:"col-lg-1 col-md-1 hidden-sm hidden-xs"});
				var button = el("button", {class:"btn btn-info btn-lg order", html:"Order", "data-id":productKey});
				section.appendChild(button);
				row.appendChild(section);
				$("#inventory-table").append(row);
			});
			$("#inventory-table").animate({
			    height : $("#inventory-table")[0].scrollHeight
			},500);
		});
		$("#inventory-table").on("click", ".inventory-department", function() {
			$(".product[data-id='" + $(this).attr("data-id") + "']").toggleClass("hidden");
		});
	}
}
