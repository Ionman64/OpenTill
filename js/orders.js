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
					bootstrap.alert("There has been an error");
					return;
				}
				$.each(data.products, function(key, product) {
					window.orders.addProductToCurrentOrder(product);
				});
				window.orders.refreshTable();
			}
		});
	}
	this.createOrder = function(supplier) {
		var supplier = supplier || null;
		if (supplier == null) {
			$("#supplierList").empty();
			$.each(window.suppliers.suppliers, function(key, value) {
				var li = el('li', {class:'btn btn-default', "data-id":key, "data-search":value.name, html:value.name});
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
					alert("There has been an error");
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
	this.init = function() {
		$.ajaxSetup({
			method:"POST",
			dataType:"JSON"
		});
		this.orders = {};
		this.setCurrentOrderId("alpha");
		this.setCurrentOrder({"products":{}});
		this.getOrders();
		$("#order-table").on("click", ".product-btn", function() {
			showProduct(this.getAttribute("data-id"));
		});
		$("#order-table").on("click", ".order", function() {
			window.orders.orderproduct(this.getAttribute("data-id"));
		});
		$("#create-order").on("click", function() {
			window.orders.createOrder();
		});
	}
	this.refreshTable = function() {
		$("#orders-table").empty();
		$.each(this.getCurrentOrder().products, function(key, product) {
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
			var section = el("section", {class:"col-lg-11 col-md-11 col-sm-11 col-xs-11"});
			var button = el("button", {class:"btn btn-default btn-lg product-btn", html:product.name, "data-id":key});
			section.appendChild(button);
			row.appendChild(section);
			$("#orders-table").append(row);
		});
	}
}
