function Orders() {
	this.orders = {};
	this.orderId = null;
	this.getCurrentOrderId = function() {
		return this.orderId;
	}
	this.setCurrentOrderId = function(id) {
		this.orderId = id;
	}
	this.getCurrentOrder = function() {
		return this.orders[this.getCurrentOrderId()];
	}
	this.setCurrentOrder = function(order) {
		this.orders[this.getCurrentOrderId()] = order;
	}
	this.addProductToCurrentOrder = function(product) {
		this.getCurrentOrder().products[product.id] = product;
		this.refreshTable();
	}
	this.getOrders = function() {
		$.ajax({
			url:"api/kvs.php?function=GETORDERS",
			success:function(data) {
				if (!data.success) {
					bootstrap.alert("There has been an error getting the orders");
					return;
				}
				window.orders.orders = data.orders;
				window.orders.refreshTable();
			}
		});
	}
	this.createOrder = function(supplier) {
		var supplier = supplier || null;
		if (supplier == null) {
			$("#supplierList").empty();
			$.each(window.suppliers.suppliers, function(key, value) {
				var li = el('li', {class:'btn btn-default', "data-id":key, "data-search":value, html:value});
				$("#supplierList").append(li);
			});
			$("#supplierList li").on("click", function(e) {
				window.orders.createOrder($(this).attr("data-id"));
				e.stopPropagation();
			});
			$("#supplierModal").modal("show");
			return;
		}
		$.ajax({
			url:"api/kvs.php?function=CREATEORDER",
			data:{"supplier":supplier},
			success:function(data) {
				if (!data.success) {
					alert("There has been an error creating the order");
					return;
				}
				$("#supplierModal").modal("hide");
				window.orders.getOrders();
			}
		});
	}
	this.showProduct = function(id) {
		alert(id);
	}
	this.getProductsInOrder = function() {
		$.ajax({
			url:"api/kvs.php?function=GETORDER",
			data:{"id":$("#orderModal").attr("data-id")},
			dataType: "JSON",
			method:"POST",
			success: function(data) {
				if (!data.success) {
					alert("Error retreiving details");
					return;
				}
				var holder = $("#order-product-list")[0];
				$(holder).empty();
				$.each(data.products, function(key, item) {
					var row = el("section", {class:"row selectable", "data-id":key});
					//quantity
					var col = el("section", {class:"col-md-2 col-xs-2 col-sm-2"});
					var label = el("label");
					label.innerHTML = item.quantity;
					col.appendChild(label);
					row.appendChild(col);
					//name
					var col = el("section", {class:"col-md-6 col-xs-6 col-sm-6"});
					var label = el("label", {class:item.id ? "text-info" : ""});
					label.innerHTML = item.name;
					col.appendChild(label);
					row.appendChild(col);
					
					holder.appendChild(row);
				});
				$("#orderModal").modal("show");
			}
		});
	}
	this.init = function() {
		$.ajaxSetup({
			method:"POST",
			dataType:"JSON"
		});
		this.orders = {};
		this.refreshTable();
		$("#orders-table").on("click", ".selectable", function() {
			$("#orderModal").attr("data-id", this.getAttribute("data-id"));
			window.orders.getProductsInOrder();
		});
		$("#order-table").on("click", ".order", function() {
			window.orders.orderproduct(this.getAttribute("data-id"));
		});
		$("#create-order").on("click", function() {
			window.orders.createOrder();
		});
		$("#product-modal-barcode-form").on("submit", function(e) {
			e.preventDefault();
			$.ajax({
				url:"api/kvs.php?function=ADDPRODUCTTOORDER",
				data:{order:$("#orderModal").attr("data-id"), "productBarcode":$("#product-modal-barcode").val()},
				dataType: "JSON",
				success: function(data) {
					if (!data.success) {
						alert("Error adding product to order");
						return;
					}
					$("#product-modal-barcode").val("");
					window.orders.getProductsInOrder();
				}
			});
		});
	}
	this.refreshTable = function() {
		var holder = document.getElementById("orders-table");
		$(holder).empty();
		if (Object.keys(window.dashboard_data.orders).length == 0) {
			var h3 = el("h3");
			h3.innerHTML = "No Orders Yet";
			holder.appendChild(h3);
			return;
		}
		$.each(window.dashboard_data.orders, function(key, item) {
			var row = el("section", {class:"row selectable", "data-id":key});
			//Date
			var col = el("section", {class:"col-lg-11 col-md-11 col-sm-10 col-xs-10"});
			var label = el("h4", {html:item});
			col.appendChild(label);
			row.appendChild(col);
			
			var col = el("section", {class:"col-lg-1 col-md-1 col-xs-2 col-sm-2"});
			var btn = el("button", {class:"btn btn-success btn-block order-complete pull-left", text:"Complete", "data-id":key});
			col.appendChild(btn);
			row.appendChild(col);
			
			holder.appendChild(row);
		});	
		$(".order-complete").on("click", function(e) {
			e.stopPropagation();
			$.ajax({
				url:"api/kvs.php?function=COMPLETEORDER",
				data:{"id":this.getAttribute("data-id")},
				dataType: "JSON",
				method:"POST",
				success: function(data) {
					if (!data.success) {
						alert("Error completing order");
						return;
					}
					window.orders.getOrders();
				}
			});
		});
	}
}
