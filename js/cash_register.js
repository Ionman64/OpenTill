const CONTEXT = "api/";
const CASHBACK_DEPARTMENT = "5b830176-7b71-11e7-b34e-426562cc935f";
const NO_CATAGORY_DEPARTMENT = "5b82f89a-7b71-11e7-b34e-426562cc935f";
window.cashiersTransactions = {};
window.Department = null;
window.DepartmentName = "";
window.cache = {};
window.supplierArray = {};
function getTransaction() {
	return window.cashiersTransactions[window.operator] ?  window.cashiersTransactions[window.operator] : false;
}
function getOperator() {
	return window.operator = window.operator ? window.operator : false;
}
function setOperator(id) {
	window.operator = id;
}
function refund() {
	bootbox.confirm("All products will be refunded, are you sure?", function(result) {
		if (!result) {
			return;
		}
		getTransaction().refundItems();
		openDrawer();
	});
}
function printReciept() {
	if (!getTransaction() || getTransaction().numItems() == 0) {
		bootbox.alert("No products added");
		return;
	}
	var json = {};
	var products = [];
	$.each(getTransaction().products, function(key, product) {
		products.push({"name":product.name, "price":formatMoney(product.productCost()).toString(), "totalCost":formatMoney(product.totalCost()).toString(), "quantity":product.quantity});
	});
	json["products"] = products;
	json["total"] = getTransaction().totalCost().toString();
	json["cash"] = formatMoney(getTransaction().moneyGiven).toString();
	json["card"] = formatMoney(getTransaction().cardGiven).toString();
	json["change"] = formatMoney(((getTransaction().totalCostValue() - getTransaction().moneyGiven - getTransaction().cardGiven)*-1)).toString();
	var data = {"json":JSON.stringify(json)};
	url = "http://localhost:8888/receipt" + '?' + $.param(data);
	document.createElement("img").src = url;
}
function showMenu(id) {
	$("#productMenu").modal('show');
	$("#productMenu").attr("data-id", getTransaction().products[id].barcode);
	$("#productMenu #productMenuName").html(getTransaction().products[id].name);
}
function GUID() {
    return 'txxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0,
            v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}
function notify(message, timeout) {
	var timeout = timeout || 0;
	$("#notify").removeClass("hidden");
	$("#notify .notify-message").html(message);
	if (timeout > 0) {
		setTimeout(function() {
			$("#notify").addClass("hidden");
		}, timeout);
	}
	$("#notify").on("click", function() {
		$("#notify").addClass("hidden");
	});
}
function formatMoney(amount, prefix) {
	var prefix = prefix || "";
	return accounting.formatMoney(amount, prefix);
}
function payout(amount) {
	createTransaction();
	$("#supplierList").empty();
	$.each(window.supplierArray, function(key, value) {
		var li = el('li', {class:'btn btn-default', "data-id":value.id, "data-search":value.name, html:value.name});
		$("#supplierList").append(li);
	});
	$("#supplierModal").modal("show");
	$("#supplierModal").attr("data-amount", amount);
	$("#supplierSearch").focus();
	$("#supplierModal li").on("click", function(e) {
		var amount = $("#supplierModal").attr("data-amount");
		getTransaction().total = amount;
		getTransaction().type = "PAYOUT";
		getTransaction().payee = $(this).attr("data-id");
		getTransaction().sync(true);
		e.stopPropagation();
		$("#supplierModal").modal("hide");
		notify($(this).attr("data-search") + " Paid £" + formatMoney(amount));
		openDrawer();
	});
}
function openDrawer() {
	document.createElement("img").src = ("http://localhost:8888/drawer" + '?' + $.param({"open_drawer":true}));
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
	$.each(options, function(key, value) {
		el.setAttribute(key, value);
	});
	return el;
}
function refreshTable() {
	$("#table").empty();
	var count = 0;
	$.each(getTransaction().products, function(key, product) {
		count++;
		var row = (product.id==getTransaction().lastAddedProduct) ? el("section", {class:"row highlighted"}) : el("section", {class:"row"});
		row.style.padding = "5px";
		//comment 
		if (product.updated == 0)
			product.comment = "Product is new or has missing data";
		if ((!product.department) || (product.department == NO_CATAGORY_DEPARTMENT))
			product.comment = "This product has no department";
		if (!product.inDatabase)
			product.comment = "Product is not in database";
		if (product.priceOverride) {
			product.comment = "Price Overide";
		}
		if (product.comment.length > 0) {
			var section = el("section", {class:"col-md-11 col-md-offset-1"});
			var label = el("label", {class:"text-info", html:product.comment});
			section.appendChild(label);
			row.appendChild(section);
			/*if (product.inDatabase) {
				var section = el("section", {class:"col-md-4"});
				var button = el("button", {class:"btn btn-info"});
				button.onclick = function() {showProduct(product.barcode.toString())};
				button.innerHTML = "Update Product";
				section.appendChild(button);
			}
			row.appendChild(section);*/
		}
		//bin
		var section =  el("section", {class:"col-md-1"});
		var button = el("button", {class:"btn btn-danger delete", productID:product.id});
		button.onclick = function() {
			getTransaction().removeProduct($(this).attr("productID"));
		};
		button.appendChild(el("i", {class:"fa fa-times"}));
		section.appendChild(button);
		row.appendChild(section);
		//name
		var section = el("section", {class:"col-md-4"});
		var button = el("button", {class:"btn btn-default", html:product.name});
		if (product.inDatabase)
			button.onclick = function() {showMenu(product.id)};
		else
			button.disabled = "disabled";
		section.appendChild(button);
		row.appendChild(section);
		//cost
		var section = el("section", {class:"col-md-1"});
		var h5 = el("label", {class:"clear-text", html:formatMoney(product.productCost())});
		section.appendChild(h5);
		row.appendChild(section);
		//quantity 
		var section = el("section", {class:"col-md-4"});
		var input = el("input", {class:"form-control", type:"number", productID:product.id, value:product.quantity});
		section.appendChild(input);
		row.appendChild(section);
		//totalCost
		var section = el("section", {class:"col-md-2"});
		var h5 = el("label", {class:"clear-text product-total", "product-id":product.id, html:formatMoney(product.totalCost())});
		section.appendChild(h5);
		row.appendChild(section);
		$("#table").append(row);
		$("input[productID='" + product.id + "']").TouchSpin({
			min: 0,
			max: 100,
			boostat: 5,
			maxboostedstep: 10,
			postfix: 'items',
		});
		$("input[productID='" + product.id + "']").change(function(data) {
			const id = $(this).attr("productID");
			getTransaction().changeQuantity(id, parseInt(data.currentTarget.value));
			$("label.product-total[product-id='" + id + "']").html(formatMoney(getTransaction().products[id].totalCost()));
			refreshTotals();
		});
	});
	if (count == 0) {
		$("#clearTrans").attr("disabled", true);
		$("#no-goods").removeClass("hidden");
		$("#table-holder").addClass("hidden");
		return;
	}
	$('#keypad button[data-function=pay-out]').attr("disabled", true); //cannot do payouts and transactions at the same time
	$("#refund").attr("disabled", false);
	$("#clearTrans").attr("disabled", false);
	$("#no-goods").addClass("hidden");
	$("#table-holder").removeClass("hidden");
	refreshTotals();
	$('#table-holder').jScrollPane({
		verticalGutter:-16,
		animateScroll: true
	});
	$("#table-holder").data("jsp").scrollToY(99999); 
}
function refreshTotals() {
	$("#total-items").html("(" + getTransaction().numItems() + ")");
	$("#total-quantity").html("(" + getTransaction().totalQuantity() + ")");
	$("#total-cost").html("(" + getTransaction().totalCost() + ")");
	$("#costText").val(formatMoney(getTransaction().totalCost(), "£"));
	$("#totalCost").html("Total : " + formatMoney(getTransaction().totalCost(), "£"));
}
function clearTransactionCheck() {
	bootbox.confirm("Are you sure you want to clear the current transaction?", function(result) {
		if (!result) {
			return;
		}
		getTransaction().clearTransaction();
	});
}
function clearTransactionTable() {
	refreshTable();
	$('#keypad button[data-function=pay-out]').attr("disabled", false);
	$("#refund").attr("disabled", true);
	$("#total-items").html("");
	$("#total-quantity").html("");
	$("#total-cost").html("");
	$("#totalCost").html("Total : £0.00");
	$("#clearTrans").attr("disabled", true);
	$("#barcode").val("");
	clearDepartment();
}
function showProduct(brcode) {
	$.ajax({
		url: CONTEXT + "kvs.php?function=BARCODE",
		data : {number : brcode},
		dataType: "JSON",
		success: function(product) {
			$("#product-modal").attr("product-id", product.id);
			$("#ProductBarcode").val(product.barcode);
			$("#ProductName").val(product.name);
			$("#ProductDepartment").val(product.department)
			$("#ProductCost").val(product.cost);
			$("#ProductPrice").val(product.price);
			$("#Name").html(product.name);
			if (product.labelPrinted == "1") {
				$("#PrintLabel").attr("disabled", true);
			}
			else {
				$("#PrintLabel").attr("disabled", false);
			}
			$("#productMenu").modal('hide');
			$("#product-modal").modal("show");
		}
	});
}
function addProduct(data) {
	//Data should be JSON 
	if (data.isCase) {
		//Do nothing
	}
	if (data.name == "Error") {
		bootbox.alert("Invalid product");
		return;
	}
	var tempProduct = new Product();
	tempProduct.id = data.id;
	tempProduct.cost = data.price;
	tempProduct.updated = parseInt(data.updated);
	tempProduct.inDatabase = true;
	tempProduct.barcode = data.barcode;
	tempProduct.department = data.department;
	tempProduct.name = data.name;
	tempProduct.inDatabase = true;
	getTransaction().addProduct(tempProduct);
	if (data.isNew) {
		showNewProductModal(data.id);
	}
	$("#clearTrans").attr("disabled", false);
}
function isCurrency(currency) {
	var pattern = /^\d+(?:\.\d{0,2})$/ ;
	if (pattern.test(currency)) {
		return true;
	} 
	return false;
}
function isOperatorLoggingIn(code) {
	$("#barcode").val("");
	$("#item-search").addClass("hidden");
	$.ajax({
		url: CONTEXT + "kvs.php?function=OPERATORLOGON",
		data : {code : code},
		success: function(data) {
			if (!data.success) {
				bootbox.alert("Error logging on, please contact support");
				return;
			}
			setOperator(data.id); 
			if (getTransaction()) {
				refreshTable();
			}
			else {
				clearTransactionTable();
				createTransaction();
			}
			$(".wide-dialog").remove();
			notify("Logged in as: " + data.name, 5000);
			$("#operator-name").html(data.name);
		}
	});
}
function departmentSelect(obj) {
	$("#department button").each(function() {
		$(this).removeClass("btn-info");
		$(this).addClass("btn-default");
	});
	$(obj).addClass("btn-info");
	window.Department = $(obj).attr("productDepartment");
	window.DepartmentName = $(obj).html();
	if ($("#barcode").val() !== "") {
		$("#barcodeform").submit();
	}
}
function clearDepartment() {
	$("#department button").each(function(item) {
		$(this).removeClass("btn-info");
		$(this).addClass("btn-default");
	});
	window.Department = null;
	window.DepartmentName = "";
}
function createTransaction() {
	if (!getTransaction()) {
		var trans = new Transaction();
		window.cashiersTransactions[window.operator] = trans;
		trans.startTransaction();
	}
}
function showNewProductModal(id) {
	$("#newCost").val("");
	$("#newName").val("");
	$("#newProduct").attr("product-id", id);
	$("#newProduct").modal("show");
	setTimeout(function() {
		$("#newName").focus();
	}, 200);
}
function clearChange() {
	$('#moneyText').val('0.00');
	$("#changeText").val('0.00');
	getTransaction().moneyGiven=0.00;
	getTransaction().cardGiven=0.00;
}
function loadContacts() {
	$.ajax({
		url:CONTEXT + "kvs.php?function=GETALLOPERATORS",
		success:function(data) {
			if (!data.success) {
				bootbox.alert("Error loading contacts");
				return;
			}
			var option = el("option", {html:"All"});
			$("#chat-contact").append(option).attr("selected", "all");
			$.each(data.operators, function(key, value) {
				if (value.id == getOperator()) {
					return;
				}
				var option = el("option", {"value":value.id, html:value.name});
				$("#chat-contact").append(option);
			});
		}
	});
}
function sendMessage(message, to) {
	var message = message || null;
	var to = to || null;
	$.ajax({
		url:CONTEXT + "kvs.php?function=SENDMESSAGE",
		data: {"from":getOperator(), "to":to, "message":message},
		success:function(data) {
			if (!data.success) {
				bootbox.alert("Could not send message");
				return;
			}
		}
	});
}
function getMessage() {
	$.ajax({
		url:CONTEXT + "kvs.php?function=GETMESSAGES",
		data: {"operator":getOperator()},
		success:function(data) {
			if (!data.success) {
				bootbox.alert("Error loading contacts");
				return;
			}
			if (!data.messages) {
				return;
			}
			$("#notification-counter").html(data.messages.length);
			if (data.messages.length == 0) {
				return;
			}
			$("#chat-window-inner").empty();
			$.each(data.messages, function(key, value) {
				var section;
				if (value.senderId == getOperator()) {
					section = el("section", {class:"message-block pull-right"});
				}
				else {
					section = el("section", {class:"message-block pull-left"});
				}
				var messageHeader =  el("section", {class:"message-header"});
				var p = el("p", {class:"message-text", html:(value.senderName + " to " + value.recipientName)});
				messageHeader.appendChild(p);
				section.appendChild(messageHeader);
				var messageBody = el("section", {class:"message-body"});
				var p = el("p", {class:"message-text", html:value.message});
				messageBody.appendChild(p);
				section.appendChild(messageBody);
				var dateSection = el("section", {class:"message-footer"});
				var p = el("p", {class:"message-text", html:moment(value.created*1000).calendar()});
				dateSection.appendChild(p);
				section.appendChild(dateSection);
				$("#chat-window-inner").append(section);
			});
		},
		complete:function() {
			$('#chat-window').jScrollPane({
				verticalGutter:-16,
				animateScroll: true
			});
			$("#chat-window").data("jsp").scrollToY(99999);
			setTimeout(function() {
				if ($("#chat-modal").is(':visible')) {
					getMessage();
				}
	        }, 3000);
		}
	});
}
$(document).ready( function() {
	$.ajaxSetup({
		method:"POST",
		dataType:"JSON"
	});
	dialogAlert("Please scan your operator id to continue");
	window.offlineStorage = new OfflineStorage();
	window.addEventListener("online", function() {$("#offline-banner").addClass("hidden");});
	window.addEventListener("offline", function() {$("#offline-banner").removeClass("hidden");});
	$("#change .money-given-button").on("click", function() {
		getTransaction().moneyGiven += parseFloat($(this).attr("moneyvalue"));
		$("#moneyText").val(formatMoney(getTransaction().moneyGiven));
		var change = ((getTransaction().totalCostValue() - getTransaction().moneyGiven)*-1);
		$("#changeText").val(formatMoney(change));
	});
	$("#cash-out-keypad").on("click", "button", function() {
		getTransaction().moneyGiven = parseInt($("#moneyText").val());
		var change = ((getTransaction().totalCostValue() - getTransaction().moneyGiven)*-1);
		console.log(change);
		$("#changeText").val(formatMoney(change));
	});
	$("#delete-product").click(function() {
		var id = $("#product-modal").attr("product-id");
		bootbox.confirm("Are you sure you want to delete this product from the system?", function(result) {
			if (!result) {
				return;
			}
			//NOT IMPLEMENTED
		});
	});
	$("#completeCard").click(function() {
		getTransaction().cardGiven = getTransaction().totalCostValue();
		printReciept();
		if (getTransaction().getCashback() > 0.00) {
			notify(("Cashback Due: " + formatMoney(getTransaction().getCashback()), "£"));
			openDrawer();
		}
		else {
			notify("£" + formatMoney(getTransaction().totalCostValue()) + " Paid With Card");
		}
		$("#CashOut").modal("hide");
		getTransaction().sync(true);
		clearTransactionTable();
	});
	$("#chat-button").click(function() {
		if (getOperator() == null) {
			return;
		}
		getMessage();
		$("#chat-modal").modal("show");
	});
	loadContacts();
	$("#chat-form").on("submit", function(e) {
		sendMessage($("#chat-message").val(), $("#chat-contact").val());
		$("#chat-message").val("");
		e.preventDefault();
	});
	$("#completeCash").click(function() {
		if (getTransaction().moneyGiven == 0.00) {
			getTransaction().moneyGiven = getTransaction().totalCostValue(); //if they give you exact amount
		}
		var change = ((getTransaction().totalCostValue() - getTransaction().moneyGiven)*-1);
		printReciept();
		$("#CashOut").modal("hide");
		notify("Change Given: £" + formatMoney(change));
		openDrawer();	
		getTransaction().completeTransaction();
		clearTransactionTable();
	});
	$("#CashOut").on('shown.bs.modal', function(event){
		$("#completeCash").attr("disabled", false);
		$("#completeCard").attr("disabled", false);
		if (getTransaction().getCashback() > 0) {
			$("#completeCash").attr("disabled", true);
		}
		clearChange();
		$("#costText").val(getTransaction().totalCost());
	});
	$("#productModalNav").on("click", "a", function() {
		$("#productModalNav li").removeClass("active");
		$(this).parent().addClass("active");
		$("#product-modal .page").addClass("hidden");
		$("section[data-id=" + $(this).attr("data-page") + "]").removeClass("hidden");
		if ($(this).attr("data-page") == "stock") {
			chartTest($("#product-modal").attr("product-id"));
		}
	});
	$("#currentLevel").TouchSpin({
		min: 0,
		max: 100,
		boostat: 5,
		maxboostedstep: 10,
	});
	$("#graphReorderLevel").TouchSpin({
		min: 0,
		max: 100,
		boostat: 5,
		maxboostedstep: 10,
	});
	$("#graphMaxLevel").TouchSpin({
		min: 0,
		max: 100,
		boostat: 5,
		maxboostedstep: 10,
	});
	$("#graphDisplayLevel").TouchSpin({
		min: 0,
		max: 100,
		boostat: 5,
		maxboostedstep: 10
	});
	$("#clearProductName").on("click", function() {
		$('#ProductName').val('').focus();
	});
	$("#clearChange").on("click", function() {
		clearChange();
	});
	$("#showPriceOverride").on("click", function() {
		$('#priceOverrideModal').modal('show');
		$('#productMenu').modal('hide');
	});
	$("#showProductInfo").on("click", function() {
		showProduct($('#productMenu').attr('data-id'));
	});
	$("#closeProductMenu").on("click", function() {
		$('#productMenu').modal('hide');
	});
	$("#clearNewProductName").on("click", function() {
		$('#newName').val('').focus();
	});
	$("#menu-button").click(function() {
		$("#menu").hasClass("hidden") ? $("#menu").removeClass("hidden") : $("#menu").addClass("hidden");
	});
	$("#cancelSearch").click(function() {
		$("#item-search").addClass("hidden");
		$("#barcode").val("");
	});
	$("#cancelMenu").click(function() {
		$("#menu").addClass("hidden");
	});
	$("#clearTrans").click(function() {
		clearTransactionCheck();
	});
	$.ajax({
		url: CONTEXT + "kvs.php?function=GETALLSUPPLIERS",
		success: function(data) {
			if (!data.success) {
				bootbox.alert("There was an getting the suppliers");
				return;
			}
			window.supplierArray = [];
			data.suppliers.forEach(function(item) {
				window.supplierArray.push({name:item.name, id:item.id});
			});
		}
	});
	$("#supplierSearch").keyup(function() {
		var text = $("#supplierSearch").val();
		if (text.length == 0) {
			$("#supplierList li").removeClass("hidden");	
		}
		$("#supplierList li").each(function() {
			if ($(this).attr("data-search").toLowerCase().indexOf(text.toLowerCase()) == -1) {
				$(this).addClass("hidden");
			} 
		});
	});
	$("#printReceipt").click(function() {
		printReciept();
	});
	var delay = (function(){
	  var timer = 0;
	  return function(callback, ms){
	  clearTimeout (timer);
	  timer = setTimeout(callback, ms);
	 };
	})();
	$("#barcode").keyup(function() {
		var text = $("#barcode").val();
		if (text.length == 0) {
			$("#item-search").addClass("hidden");
			return;
		}
		if (/^\d+$/.test(text) == false) {
			delay(function() {showSearches(text)}, 200);
		}
	});
	$("#item-search-list").on("click", "li", function() {
		if (!getOperator()) {
			dialogAlert("Please scan your operator id to continue");
			return;
		}
		if (!getTransaction()) {
			createTransaction()
		}
		addProduct(window.cache[$(this).attr("product-data")]);
		$("#item-search").addClass("hidden");
		$("#barcode").val("");
	});
	function showSearches(searchString) {
		if ((searchString.indexOf("[") > -1) || (searchString.indexOf("]") > -1) || (searchString.length == 0)) {
			var holder = $("#item-search-list")[0];
			$(holder).empty();
			$("#item-search").addClass("hidden");
			return;
		}
		$.ajax({
			url: CONTEXT + "kvs.php?function=SEARCH",
			data : {"search" : searchString},
			success : function(data) {
				var holder = $("#item-search-list")[0];
				$(holder).empty();
				$("#item-search").removeClass("hidden");
				$.each(data, function(key, item) {
					if ($("#barcode").val().length == 0) {
						$("#item-search").addClass("hidden");
						return;
					}
					var li = el("li", {class: "btn btn-default"});
					$(li).attr("product-data", item.barcode);
					window.cache[item.barcode.toString()] = item;
					var span1 = el("span", {html:(item.name + "<br>@" + formatMoney(item.price, "£"))});
					li.appendChild(span1);
					holder.appendChild(li);	
				});
			}	
		});
	}
	$("#barcodeform").submit(function() {
		var brcode = $("#barcode").val();
		$("#barcode").val("");
		if (brcode.indexOf("[") > -1) {
			isOperatorLoggingIn(brcode.slice(1, -1).split(":")[1]);
			return;
		}
		if (!getOperator()) {
			dialogAlert("Please scan your operator id to continue");
			return;
		}
		if (brcode.length == 0) {
			return;
		}
		createTransaction();
		if (isCurrency(brcode)) {
			if (window.Department == null) {
				$("#department").effect("pulsate", { times:3 }, 2000);
				return;
			}
			var tempProduct = new Product();
			tempProduct.id = GUID();
			tempProduct.cost = parseFloat(brcode);
			tempProduct.name = window.DepartmentName;
			tempProduct.inDatabase = false;
			tempProduct.department = window.Department;
			getTransaction().addProduct(tempProduct);
			clearDepartment();
			$("#clearTrans").attr("disabled", false);
			refreshTable();
			return;
		}
		if (window.cache[brcode.toString()]) {
			var tempProduct = window.cache[brcode.toString()];
			if (!tempProduct.isNew) {
				addProduct(tempProduct);
				return;
			}
		}
		$.ajax({
			url: CONTEXT + "kvs.php?function=BARCODE",
			data : {number : brcode},
			success : function(data) {
				window.cache[data.barcode.toString()] = data;
				addProduct(data);
			}
		});
	});
	$('.keypad').on("click", "button", function() {
		var focus = $(this).parents(".keypad").first().attr("data-focus");
		var attr = $(this).attr("data-function"); 
		if ($(focus).prop("tagName") !== "INPUT") {
			return;
		}
		var unit = $(focus).val().length;
		var currentValue = unit > 1 ? parseFloat($(focus).val()) : 0.00;
		switch (attr) {
			case "no-sale":
				openDrawer();
				return;
			case "pay-out":
				payout(currentValue);
				return;
			case "clear-button":
				$(focus).val("");
				return;
			case "refund":
				refund();
				return;
		}
		if ($(this).html() == "00") {
			currentValue = currentValue*10; //if double 00 do it twice
		}
		currentValue = currentValue*10;
		var addedValue = parseFloat($(this).html())/100;
		var newValue = currentValue + addedValue; 
		$(focus).val(formatMoney(newValue));
		$(focus).focus();
	});
	$("#update-product").click(function() {
		var barcode = $("#ProductBarcode").val();
		delete window.cache[barcode];
		$.ajax({
			url: CONTEXT + "kvs.php?function=UPDATEPRODUCT",
			data : {"id":$("#product-modal").attr("product-id"), "cashier":getOperator(), "barcode":barcode, "department":$("#ProductDepartment").val(), "name" : $("#ProductName").val(), "cost" : 0.00, "price" : $("#ProductPrice").val()},
			success: function(data) {
				if (!data.success) {
					bootbox.alert("Product Not Updated");
					return;
				}
				var id = $("#product-modal").attr("product-id");
				getTransaction().products[id].cost = $("#ProductPrice").val();
				getTransaction().products[id].price = $("#ProductPrice").val();
				getTransaction().products[id].department = parseFloat($("#ProductDepartment").val());
				getTransaction().products[id].name = $("#ProductName").val();
				getTransaction().sync();
				$("#product-modal").modal("hide");
				refreshTable();
			}
		});
	});
	$("#newProductSave").click(function() {
		$.ajax({
			url: CONTEXT + "kvs.php?function=UPDATEPRODUCT",
			data : {"id":$("#newProduct").attr("product-id"), "name" : $("#newName").val(), "cashier":getOperator(), "department": $("#newProductDepartment").val(), "cost" : $("#newCost").val(), "price" : $("#newCost").val()},
			success: function(data) {
				if (!data.success) {
					bootbox.alert("Product Not Updated");
					return;
				}
				var id = $("#newProduct").attr("product-id");
				getTransaction().products[id].cost = $("#newCost").val();
				getTransaction().products[id].department = $("#newProductDepartment").val();
				getTransaction().products[id].price = $("#newCost").val();
				getTransaction().products[id].name = $("#newName").val();
				$("#newProduct").modal("hide");
				delete window.cache[barcode];
				refreshTable();
			}
		});
	});

	$("#cashOutMainButton").click(function() {
		$('#moneyText').val('0.00');
		if (getTransaction() != 0) {
			getTransaction().moneyGiven=0.00;	
		}
		$('#CashOut').modal('show');
	});
	$("#PrintLabel").click(function() {
		$.ajax({
			url: CONTEXT + "kvs.php?function=PRINTLABEL",
			data : {"id":$("#product-modal").attr("product-id")},
			success: function(data) {
				if (data.success) {
					$("#PrintLabel").attr("disabled", true);
					return;
				}
				bootbox.alert("There was an error");
			}
		});
	});
	$("#department").on("click", "button", function() {
		departmentSelect(this);
	});
	$("#department").html("");
	$.ajax({
		url : CONTEXT + "kvs.php?function=DEPARTMENTS",
		success : function(data) {
			var code = "";
			code += "<section class='row'>";
			data.forEach(function(item) {
				code += "<section class='col-md-6 col-sm-6 col-xs-6 col-lg-4'>";
				var style = "border-right:5px solid " + item.colour + ";border-left:5px solid " + item.colour;
				code += "<button class='btn btn-default' productDepartment='" + item.id + "' style='" + style + "'>" + item.shorthand + "</button>";
				code += "</section>";
				var option = el("option", {html:item.name, value:item.id});
				$("#ProductDepartment").append(option);
				var option = el("option", {html:item.name, value:item.id});
				$("#newProductDepartment").append(option);
			});
			code += "</section>";
			$("#department").append(code);
		}
	});
	setInterval(function() { 
		var modalShown = false;
		$('.modal').each(function() {
			modalShown = modalShown ? modalShown : $(this).is(':visible');
		});
		if (!modalShown) {
			$("#barcode").focus();
		}
	},200);
});
function chartTest(id, data) {
	if (!data) {
		$.ajax({
			url:"api/kvs.php?function=GETPRODUCTSALES",
			data: {"id":id, "end":moment(moment().format("YYYY-MM-DD")).unix(), "start":moment(moment().subtract(1, "months").format("YYYY-MM-DD")).unix()},
			success:function(data) {
				if (!data.success) {
					return;
				}
				console.log(data);
			}
		});
		$.ajax({
			url:"api/kvs.php?function=GETPRODUCTLEVELS",
			data: {"id":id},
			success: function(data) {
				if (!data.success) {
					return;
				}
				$("#currentLevel").val((parseInt(data.product.current_stock)+parseInt(data.product.current_display)));
				$("#graphReorderLevel").val(data.product.lowest_reorder);
				$("#graphMaxLevel").val(data.product.max_stock);
				$("#graphDisplayLevel").val(data.product.current_display);
				chartTest(id, data.product);
			}
		});	
		return;
	}
	var MONTHS = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
	var chartColors = {
		red: 'rgb(255, 99, 132)',
		orange: 'rgb(255, 159, 64)',
		yellow: 'rgb(255, 205, 86)',
		green: 'rgb(75, 192, 192)',
		blue: 'rgb(54, 162, 235)',
		purple: 'rgb(153, 102, 255)',
		grey: 'rgb(201, 203, 207)'
	};
	var product = getTransaction().products[$("#product-modal").attr("product-id")];
	var intervals = [];
	var dataPoints = [];
	var maxStock = [];
	var reorderLevel = []; //re-order level
	var onDisplay = [];
	var date = moment();
	var currentMonth = date.month();
	date.subtract(date.date()-1, "days");
	while (currentMonth==date.month()) {
		intervals.push(date.format("DD"));
		date.add(1, "days");
	}
	console.log(data);
	for (var i=0;i<intervals.length;i++) {
		dataPoints.push(0);
		onDisplay.push(data.max_display);
		reorderLevel.push(data.lowest_reorder);
		maxStock.push(data.max_stock);
	}
	var config = {
		type: 'line',
		data: {
		    labels: intervals,
		    datasets: [{
		        label: "Re-order level",
		        backgroundColor: chartColors.red,
		        borderColor: chartColors.red,
		        data: reorderLevel,
		        fill: false,
		    },
		    {
		        label: "To Display",
		        fill: false,
		        backgroundColor: chartColors.grey,
		        borderColor: chartColors.grey,
		        data: onDisplay,
		    },
		    {
		        label: "Max Volume",
		        fill: false,
		        backgroundColor: chartColors.blue,
		        borderColor: chartColors.blue,
		        data: maxStock,
		    },
		    {
		        label: "Estimated Stock",
		        fill: false,
		        backgroundColor: chartColors.blue,
		        borderColor: chartColors.blue,
		        data: dataPoints,
		    }]
		},
		options: {
		    responsive: true,
		    title:{
		        display:false,
		        text:('Estimated Stock for ' + product.name)
		    },
		    tooltips: {
		        mode: 'index',
		        intersect: false,
		    },
		    hover: {
		        mode: 'nearest',
		        intersect: true
		    },
		    scales: {
		        xAxes: [{
		            display: true,
		            scaleLabel: {
		                display: true,
		                labelString: moment().format("MMMM")
		            }
		        }],
		        yAxes: [{
		            display: true,
		            scaleLabel: {
		                display: true,
		                labelString: '# of stock'
		            }
		        }]
		    }
		}
	};
	var ctx = document.getElementById("canvas").getContext("2d");
	window.myLine = new Chart(ctx, config);
	var colorNames = Object.keys(chartColors);
}
function OfflineStorage() {
	var transactionsKey = "transStorageKey";
	this.put = function(key, value) {
		if (key !== transactionsKey) {
			console.error("Incorrect key");
			return;
		}
		window.localStorage.setItem(key, JSON.stringify(value));
	}
	this.get = function(key) {
		return JSON.parse(window.localStorage.getItem(key));
	}
	this.remove = function(key) {
		window.localStorage.removeItem(key);
	}
	this.putTransaction = function(transaction) {
		var transactions = this.get(transactionsKey);
		transactions[transaction.id] = transaction;
		this.put(transactionsKey, transactions);
	}
	this.getTransaction = function(id) {
		var transactions = this.get(transactionsKey);
		return transactions[id];
	}
	this.removeTransaction = function(id) {
		var transactions = this.get(transactionsKey);
		delete transactions[transaction.id];
		this.put(transactionsKey, transactions);
	}
	this.getAllTransactions = function() {
		return this.get(transactionsKey);
	}
	this.put(transactionsKey, {});
}
function dialogAlert(title, callback, dialogId) {
	$(".wide-dialog").remove();
	var callback = callback || false;
	/*
	<section class="wide-dialog">
		<section class="wide-dialog-body">
			<h3>Are you sure?</h3>
			<p class="italic">This will make a change</p>
			<section class="wide-dialog-buttons">
				<button class="btn btn-success btn-lg">Yes</button>
				<button class="btn btn-default btn-lg">No</button>
			</section>
		</section>
	</section>
	*/
	var dialogId = dialogId || GUID();
	var dialog = el("section", {class:"wide-dialog", id:dialogId});
	var body = el("section", {class:"wide-dialog-body"});
	var h3 = el("h3", {html:title, class:"text-center"});
	body.appendChild(h3);
	/*var p = el("p", {class:"italic", html:message});
	body.appendChild(p);*/
	var buttons = el("section", {class:"wide-dialog-buttons"});
	var ok = el("button", {class:"btn btn-success btn-lg", html:"Ok", "data-id":dialogId});
	//var no = el("button", {class:"btn btn-default btn-lg", html:"No"});
	ok.onclick = function() {
		var elem = document.getElementById(this.getAttribute("data-id"));
		elem.parentNode.removeChild(elem);
	}
	//no.onclick = function() {
	//	alert("no");
	//}
	buttons.appendChild(ok);
	//buttons.appendChild(no);
	body.appendChild(buttons);
	dialog.appendChild(body);
	document.body.appendChild(dialog);
}

			

