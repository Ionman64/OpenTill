function Inventory() {
	this.showProduct = function(id) {
		alert(id);
	}
	this.init = function() {
		$.ajaxSetup({
			method:"POST",
			dataType:"JSON"
		});
		this.refreshTable();
		$("#inventory-table").on("click", ".product-btn", function() {
			showProduct(this.getAttribute("data-id"));
		});
		$("#inventory-table").on("click", ".order", function() {
			window.orders.orderproduct(this.getAttribute("data-id"));
		});
		$("#inventory-export-btn").click(function() {
			var holder = document.getElementById("inventory-departments-export");
			$(holder).empty();
			$.each(window.dashboard_data.departments, function(id, department) {
				var li = el("li");
				var label = el("label", {"for":"inventory-checkbox-" + id});
				var input = el("input", {"id":"inventory-checkbox-" + id, type:"checkbox", checked:true, "data-id":id});
				label.appendChild(input);
				label.appendChild(document.createTextNode(department));
				li.appendChild(label);
				holder.appendChild(li);
			});
			$("#inventory-export-success").addClass("hidden");
			$("#inventory-export-failure").addClass("hidden");
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
				url:"api/kvs.jsp?function=GENERATEINVENTORYREPORT",
				dataType: "JSON",
				data:{"export-type":$("#inventory-export-type").val(), "departments":selectedDepartments},
				beforeSend: function() {
					$("#inventory-export-progress").removeClass("hidden");
				},
				success: function(data) {
					if (!data.success) {
						$("#inventory-export-failure").removeClass("hidden");
						return;
					}
					$("#inventory-export-success").removeClass("hidden");
					$("#inventory-export-alt-download").attr("href", data.file);
					window.open(data.file, 'Download');  
				},
				error: function() {
					$("#inventory-export-failure").removeClass("hidden");
				},
				complete: function() {
					$("#inventory-export-progress").addClass("hidden");
				}
			});
		});
	}
	this.refreshTable = function() {
		$("#inventory-table").empty();
		$.each(window.dashboard_data.inventory, function(departmentKey, department) {
			var row = el("section", {class:"row selectable inventory-department", style:("border-left:15px solid " + department["colour"] + ";"), "data-id":departmentKey});
			var h4 = el("h4", {class:"italic", html:window.dashboard_data.departments[departmentKey]});
			var section = el("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12"});
			section.appendChild(h4);
			row.appendChild(section);
			delete department["colour"];
			$("#inventory-table").append(row);
			$.each(department, function(productKey, product) {
				var row = el("section", {class:"row product hidden", "data-id":departmentKey});
				//name
				var section = el("section", {class:"col-lg-5 col-md-5 col-sm-8 col-xs-6"});
				var button = el("button", {class:"btn btn-default btn-lg product-btn", text:product.name, "data-id":productKey});
				section.appendChild(button);
				row.appendChild(section);
				//max stock
				var section = el("section", {class:"col-lg-2 col-md-2 col-sm-2 col-xs-2"});
				var p = el("h4", {class:"text-default clear-text", text:product.max_stock});
				section.appendChild(p);
				row.appendChild(section);
				//in stock 
				var section = el("section", {class:"col-lg-2 col-md-2 col-sm-2 col-xs-2"});
				var p = el("h4", {class:"text-default clear-text", text:product.current_stock});
				section.appendChild(p);
				row.appendChild(section);
				//order amount
				var section = el("section", {class:"col-lg-2 col-md-2 col-sm-2 col-xs-2"});
				var p = el("h4", {class:"text-default clear-text", text:(product.max_stock-product.current_stock)});
				section.appendChild(p);
				row.appendChild(section);
				//Order
				var section = el("section", {class:"col-lg-1 col-md-1 hidden-sm hidden-xs"});
				var button = el("button", {class:"btn btn-info btn-lg order-btn", text:"Order", "data-id":productKey});
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
		$("#inventory-table").on("click", ".order-btn", function() {
			$.ajax({
				url:"api/kvs.jsp?function=ADDPRODUCTTOORDER",
				data:{id:this.getAttribute("data-id")},
				dataType: "JSON",
				success: function(data) {
					if (!data.success) {
						alert("Error adding product to order");
						return;
					}
				}
			});
		});
	}
}
