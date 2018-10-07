const CONTEXT = "api/";
const CASHBACK_DEPARTMENT = "5b830176-7b71-11e7-b34e-426562cc935f";
const NO_CATAGORY_DEPARTMENT = "5b82f89a-7b71-11e7-b34e-426562cc935f";
var LAST_MESSAGE_UPDATE = 0;
window.cashiersTransactions = {};
window.Department = null;
window.DepartmentName = "";
window.cache = {};
window.supplierArray = {};
window.shouldPrintReciept = false;

var non_product_barcodes = [];
var ai2 = 0;
non_product_barcodes[ai2++] = {"id":"absf2", "name":"Tomatoes", "img":"img/products/tomato.jpg"};
non_product_barcodes[ai2++] = {"id":"ab", "name":"Jacket Potatoes", "img":"img/products/potatoes.jpg"};
non_product_barcodes[ai2++] = {"id":"ab", "name":"Sausages", "img":"img/products/sausage.jpg"};
non_product_barcodes[ai2++] = {"id":"ab", "name":"Smoked Bacon", "img":"img/products/bacon.jpg"};
delete ai2;

function isZero(m) {
	if (m == 0) {
		return true;
	}
	return false;
}

function isUndefined(m) {
	if (m == undefined) {
		return true;
	}
	return false;
}

function detectOffers() {
	//Send Request to Server looking for offers
	var offers = {
		"off1": {
			"name":"BOGOF",
			"reduction":0.30,
			"products":{
				"d3321af2-bc41-4c08-a7df-e3665aed3c97":2
			}
		}
	};
	getTransaction().appliedOffers = {};
	$.each(offers, function(offerKey, offer) {
		$.each(getTransaction().products, function(productKey, product) {
			if (isUndefined(offer.products[productKey])) {
				return;
			}
			var offerQuantity = offer.products[productKey];
			var itemQuantity = product.quantity;
			while (itemQuantity >= offerQuantity) {
				if (isUndefined(getTransaction().appliedOffers[offerKey])) {
					getTransaction().appliedOffers[offerKey] = 1;
				}
				else {
					getTransaction().appliedOffers[offerKey]++;
				}
				itemQuantity -= offerQuantity;
				console.log("Offer Applied");
			}
		});
	});
}


function loadNonBarcodeProducts() {
	var holder = $("#non-barcode-container")[0];
	$(holder).empty();
	var row = el("section", {class:"row"});
	var count = 0;
	for (var i = 0;i<1;i++) {
		$.each(non_product_barcodes, function(key, value) {
			if (count++ % 12 == 0) {
				holder.appendChild(row);
				row = el("section", {class:"row"});
			}
			
			var col = el("section", {class:"col-lg-1 col-md-1 col-sm-2 col-xs-3"});
			
			var panel = el("section", {class:"panel panel-default no-product-barcode", "data-id":value.id});
			var panelBody = el("section", {class:"panel-body no-padding"});
			var img = el("img", {class:"img img-responsive", "src":value.img});
			
			panelBody.appendChild(img);
			panel.appendChild(panelBody);
			
			var panelFooter = el("section", {class:"panel-footer text-center clearfix", html:value.name})
			panel.appendChild(panelFooter);
			
			col.appendChild(panel);
			row.appendChild(col);
		});
	}
	if (count % 12 != 0) {
		holder.appendChild(row);
	}
}


function getTransaction() {
	return window.cashiersTransactions[window.operator] ?  window.cashiersTransactions[window.operator] : false;
}
function getOperator() {
	return window.operator = window.operator ? window.operator : false;
}
function setOperator(id) {
	window.operator = id;
	$("#chat-window-inner").empty();
	LAST_MESSAGE_UPDATE = 0;
	getMessage();
}
function logout() {
	delete window.operator;
	refreshTable();
	$("#operator-name").html('');
	dialogAlert("Please scan your operator id to continue");
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
	if (!window.shouldPrintReciept) {
		return;
	}
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
	console.log(id);
	$("#productMenu").attr("data-id", id);
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
		var li = el('li', {class:'btn btn-default', "data-id":key, "data-search":value, html:value});
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
		getTransaction().completeTransaction();
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
		var comment = "";
		if (isZero(product.updated)) {
			comment = "Product is new or has missing data";
		}
		else if (product.department == NO_CATAGORY_DEPARTMENT) {
			comment = "This product has no department";
		}
		else if (!product.department) {
			comment = "This product has no department";
		}
		else if (!product.inDatabase) {
			comment = "Product is not in database";
		}
		else if (product.priceOverride) {
			comment = "Price Overide";
		}
		else if (isZero(product.maxStock)) {
			comment = "This product has no max stock set";
		}
		else {
			comment = " (Stock Level: " + product.currentStock + "/" + product.maxStock + ")";
		}
		if (!isZero(comment.length)) {
			var section = el("section", {class:"col-lg-11 col-md-11 col-sm-11 col-xs-11 col-lg-offset-1 col-md-offset-1 col-sm-offset-1 col-xs-offset-1"});
			var label = el("label", {class:"text-info", html:comment});
			section.appendChild(label);
			row.appendChild(section);
		}
		//bin
		var section =  el("section", {class:"col-lg-1 col-md-1 col-sm-1 col-xs-1"});
		var button = el("button", {class:"btn btn-danger delete", productID:product.id});
		button.onclick = function() {
			getTransaction().removeProduct($(this).attr("productID"));
		};
		button.appendChild(el("i", {class:"fa fa-times"}));
		section.appendChild(button);
		row.appendChild(section);
		//name
		var section = el("section", {class:"col-lg-4 col-md-4 col-sm-4 col-xs-4"});
		var button = el("button", {class:"btn btn-default", html:product.name});
		if (product.inDatabase)
			button.onclick = function() {showMenu(product.id)};
		else
			button.disabled = "disabled";
		section.appendChild(button);
		row.appendChild(section);
		//cost
		var section = el("section", {class:"col-lg-1 col-md-1 col-sm-1 col-xs-1"});
		var h5 = el("label", {class:"clear-text", html:formatMoney(product.productCost())});
		section.appendChild(h5);
		row.appendChild(section);
		//quantity 
		var section = el("section", {class:"col-lg-4 col-md-4 col-sm-4 col-xs-4"});
		var input = el("input", {class:"form-control", type:"number", productID:product.id, value:product.quantity});
		section.appendChild(input);
		row.appendChild(section);
		//totalCost
		var section = el("section", {class:"col-lg-2 col-md-2 col-sm-2 col-xs-2"});
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
	if (isZero(count)) {
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
function isZero(m) {
	if (m == 0) {
		return true;
	}
	return false;
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
			if (product.supplier != undefined) {
				$("#ProductSupplier").val(product.supplier);
			}
			else {
				$("#ProductSupplier").val("Unknown");
			}	
			$("#Name").html(product.name);
			$("#maxStockLevel").val(product.max_stock);
			$("#currentLevel").val(product.current_stock);
			if (product.labelPrinted == "1") {
				$("#PrintLabel").attr("disabled", true);
			}
			else {
				$("#PrintLabel").attr("disabled", false);
			}
			$("#auto-pricing-enabled").attr("checked", product.autoPricingUpdateEnabled);
			$("#SupplierPrice").val(product.supplierPrice);
			$("#unitsInCase").val(product.unitsInCase);
			$("#includesVAT").attr("checked", product.includesVAT);
			$("#VATamount").val(product.VATamount);
			$("#targetPercentage").attr("checked", product.targetPercentage);
			$("#targetProfitMargin").val(product.targetProfitMargin);
			$("#productMenu").modal('hide');
			$("#product-modal").modal("show");
		}
	});
}
function addProduct(data) {
	//Data should be JSON 
	if (data.isCase) {
		$("#case-modal").modal("show");
		return;
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
		url: CONTEXT + "kvs.jsp?function=OPERATORLOGON",
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
		url:CONTEXT + "kvs.jsp?function=GETALLOPERATORS",
		success:function(data) {
			var option = el("option", {html:"All"});
			$("#chat-contact").append(option).attr("selected", "all");
			$("#chat-contact-list").empty();
			$.each(data, function(index, value) {
				if (value.id == getOperator()) {
					return;
				}
				var li = el("li", {class:"list-group-item", "data-id":value.id, html:value.name});
				var badge = el("span", {"data-id":value.id, class:"badge hidden", html:"0"});
				
				li.appendChild(badge);
				$("#chat-contact-list").append(li);
			});
			$("#chat-contact-list").on("click", "li", function() {
				$("#chat-contact-list li").removeClass("active");
				$(this).addClass("active");
				$(this).children(".badge").addClass("hidden");
				showMessagesFromSender($(this).attr("data-id"));
			});
		}
	});
}
function sendMessage(message, to) {
	var message = message || null;
	var to = to || null;
	$.ajax({
		url:CONTEXT + "kvs.jsp?function=SENDMESSAGE",
		data: {"from":getOperator(), "to":to, "message":message},
		success:function(data) {
			if (!data.success) {
				bootbox.alert("Could not send message");
				return;
			}
		}
	});
}

function showMessagesFromSender(senderId) {
	$("#chat-window-inner .message").addClass("hidden");
	$("#chat-window-inner .message[data-sender-id='" + senderId + "']").removeClass("hidden");
	$("#chat-window-inner .message[data-sender-id='" + getOperator() + "']").removeClass("hidden");
	$("#chat-window").animate({ scrollTop: $("#chat-window-inner").height() }, 1000);
	$("#chat-window-inner").attr("data-sender-id", senderId);
}

function getMessage() {
	if (!getOperator()) {
		return;
	};
	$.ajax({
		url:CONTEXT + "kvs.jsp?function=GETMESSAGES",
		data: {"operator":getOperator(), "time":LAST_MESSAGE_UPDATE},
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
			var newMessagesFromContacts = {};
			var holder = el("section");
			$.each(data.messages, function(key, value) {
				if (value.senderId != getOperator()) {
					newMessagesFromContacts[value.senderId] = 0;
					if ((0 < LAST_MESSAGE_UPDATE) && (LAST_MESSAGE_UPDATE < moment().unix())) {
						if (!$("#chat-modal").is(':visible')) {
							$("#newMessages").removeClass("hidden");
						}
						$("#chat-contact-list .badge[data-id='" + value.senderId + "']").html(++newMessagesFromContacts[value.senderId]).removeClass("hidden");
					}
				}
				var section = el("section", {class:"row message", "data-sender-id":value.senderId});
				if (!isUndefined($("#chat-window-inner").attr("data-sender-id"))) {
					if (value.senderId != $("#chat-window-inner").attr("data-sender-id")) {
						section = el("section", {class:"row message hidden", "data-sender-id":value.senderId});
					}
				}
				var messageBody = el("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12 message-body wordwrap"});
				var p = el("h4", {class:"message-text", html:"<b>" + value.senderName + "</b>: " + value.message});
				messageBody.appendChild(p);
				section.appendChild(messageBody);
				var dateSection = el("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12 message-footer"});
				var p = el("label", {class:"message-text", html:moment(value.created*1000).calendar()});
				dateSection.appendChild(p);
				section.appendChild(dateSection);
				holder.appendChild(section);
			});
			$("#chat-window-inner").append(holder);
			LAST_MESSAGE_UPDATE = moment().unix();
			if (isZero($("#chat-contact-list li.active").length)) {
				$("#chat-contact-list li").first().click();
			}
			if ($("#chat-contact-list li.active").attr("data-id") == $("#chat-window-inner").attr("data-sender-id")) {
				if (!isZero(newMessagesFromContacts[$("#chat-window-inner").attr("data-sender-id")]) || isZero(LAST_MESSAGE_UPDATE)) {
					$("#chat-window").animate({ scrollTop: $("#chat-window-inner").height() }, 1000);
				}
			}
		},
		complete:function() {
			var delay = $("#chat-modal").is(':visible') == true ? 2000 : 10000;
			if (typeof window.messageTimeout != undefined) {
				clearTimeout(window.messageTimeout);
			}
			window.messageTimeout = setTimeout(function() {
					getMessage();
			}, delay);
		}
	});
}
function loadModals() {
	window.modals = $("modal").length;
	$("modal").each(function(key, el) {
		$.get(this.getAttribute("data-page"), function(data) {
			$("body").append(data);
			if (--window.modals == 0) {
				setupAutoPricing();
				loadRegister();
			}
		});
		this.parentNode.removeChild(this);
	});
}
function setupAutoPricing() {
	$("#auto-pricing-enabled").bootstrapToggle({size:"large"}).on("change", function() {
		if (!$(this).is(":checked")) {
			$("#auto-price-update-container").addClass("well-disabled");
			$("#auto-pricing-enabled-message").html("Auto Pricing Disabled").addClass("text-danger").removeClass("text-success");
			return;
		}
		$("#auto-price-update-container").removeClass("well-disabled");
		$("#auto-pricing-enabled-message").html("Auto Pricing Enabled").removeClass("text-danger").addClass("text-success");
		calcAutoPriceUpdate();
	});
	$("#includesVAT").bootstrapToggle({size:"large"}).on("change", function() {
		$("#autopriceupdate-modal").addClass("hidden");
		calcAutoPriceUpdate();
		if ($(this).is(":checked")) {
			$("#VATamount").attr("disabled", false);
			return;
		}
		$("#VATamount").attr("disabled", true);
	});
	$("#targetPercentage").bootstrapToggle({size:"large"}).on("change", function() {
		$("#autopriceupdate-modal").addClass("hidden");
		calcAutoPriceUpdate();
		if ($(this).is(":checked")) {
			$("#targetProfitMargin").attr("disabled", false);
			return;
		}
		$("#targetProfitMargin").attr("disabled", true);
	});
	$("#unitsInCase").TouchSpin({
		min: 0,
		max: 1000,
		boostat: 5,
		maxboostedstep: 10,
		postfix: 'Units',
	});
	$("#targetProfitMargin").on("keyup", function() {
		calcAutoPriceUpdate();
	});
	$("#VATamount").on("keyup", function() {
		calcAutoPriceUpdate();
	});
	$("#unitsInCase").change(function() {
		calcAutoPriceUpdate();
	});
	$("#SupplierPrice").on("keyup", function() {
		calcAutoPriceUpdate();
	});	
}
function calcAutoPriceUpdate() {
	$("#autopriceupdate-modal").addClass("hidden");
	$("#por-over-100").addClass("hidden");
	var supplierPrice = 0.0;
	var newProductPrice = 0.0;
	var targetPOR = 0.0;
	var vatPrice = 0.0;
	var productPrice = getTransaction().products["d070d9de-6bd2-11e7-b34e-426562cc935f"].cost;
	if (($("#targetProfitMargin").val().length != 0) && ($("#targetPercentage").is(":checked"))) {
		targetPOR = parseFloat($("#targetProfitMargin").val());
	}
	if (targetPOR >= 100) {
		$("#por-over-100").removeClass("hidden");
		return;
	}
	if (($("#VATamount").val().length != 0) && ($("#includesVAT").is(":checked"))) {
		vatPrice = parseFloat($("#VATamount").val());
	}
	if ($("#SupplierPrice").val().length != 0) {
		supplierPrice = parseFloat($("#SupplierPrice").val());
		var unitsInCase = 0;
		if ($("#unitsInCase").val().length != 0) {
			unitsInCase = parseInt($("#unitsInCase").val());
		}
		supplierPrice += supplierPrice*(vatPrice/100);
		var percentagePaid = Math.abs(100 - targetPOR);
		var grossReturn = (supplierPrice * 100) / percentagePaid;
		var pricePerUnit = (grossReturn/unitsInCase);
		newProductPrice = pricePerUnit;
	}
	if (newProductPrice > productPrice) {
		$("#autopriceupdate").html("£" + formatMoney(newProductPrice));
		$("#autopriceupdate-modal").removeClass("hidden");
	}
}
$(document).ready( function() {
	loadModals();
	$.ajaxSetup({
		method:"POST",
		dataType:"json"
	});
});
function loadRegister() {
	$("#printReceipt").click(function() {
		if (window.shouldPrintReciept) {
			$(this).addClass("btn-danger").removeClass("btn-default");
			window.shouldPrintReciept = false;
		}
		else {
			$(this).removeClass("btn-danger").addClass("btn-default");
			window.shouldPrintReciept = true;
			bootbox.confirm("Do you want to print a reciept now?", function(result) {
				if (result) {
					printReciept();
				} 
			});
		}
	});
	$(".notify").on("click", function() {
		$("#chat-modal").modal("show");
		$("#chat-window").animate({ scrollTop: $("#chat-window-inner").height() }, 1000);
		getMessage();
		$(this).addClass("hidden");
	});
	$("#non-barcode-btn").click(function() {
		$("#non-barcode-container").toggleClass("hidden");
	});
	$("#non-barcode-container").on("click", ".no-product-barcode ", function() {
		$("#weightModal").modal("show");
	});
	dialogAlert("Please scan your operator id to continue");
	window.offlineStorage = new OfflineStorage();
	window.addEventListener("online", function() {$("#offline-banner").addClass("hidden");});
	window.addEventListener("offline", function() {$("#offline-banner").removeClass("hidden");});
	$(".menu-buttons").on("click", ".menu-button", function() {
		$("#menu").addClass("hidden");
	});
	$("#signout").on("click", function() {
		logout();
	});
	$("#change .money-given-button").on("click", function() {
		getTransaction().moneyGiven += parseFloat($(this).attr("data-moneyvalue"));
		$("#moneyText").val(formatMoney(getTransaction().moneyGiven));
		var change = ((getTransaction().totalCostValue() - getTransaction().moneyGiven)*-1);
		$("#changeText").val(formatMoney(change));
	});
	$("#moneyText").on("change", function() {
		getTransaction().moneyGiven = parseFloat($("#moneyText").val());
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
			$.ajax({
				url:CONTEXT + "kvs.jsp?function=DELETEPRODUCT",
				data:{"id":id},
				success: function(data) {
					if (!data.success) {
						bootbox.alert("Could not remove product");
						return;
					}
					$("#product-modal").modal("hide");
					getTransaction().removeProduct(id);
				}
			});
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
		getTransaction().completeTransaction();
		$("#CashOut").modal("hide");
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
		sendMessage($("#chat-message").val(), $("#chat-contact-list li.active").first().attr("data-id"));
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
	});
	$("#currentLevel").TouchSpin({
		min: 0,
		max: 100,
		boostat: 5,
		maxboostedstep: 10,
	});
	$("#maxStockLevel").TouchSpin({
		min: 0,
		max: 100,
		boostat: 5,
		maxboostedstep: 10,
	});
	$("#clearProductName").on("click", function() {
		$('#ProductName').val('').focus();
	});
	$("#clearChange").on("click", function() {
		clearChange();
	});
	$("#override-submit-button").click(function() {
		var newPrice = $("#overide-price").val();
		var item = getTransaction().products[$('#productMenu').attr('data-id')];
		item.overridePrice(newPrice);
		$('#priceOverrideModal').modal('hide').removeAttr("data-id");
	});
	$("#showPriceOverride").on("click", function() {
		var item = getTransaction().products[$('#productMenu').attr('data-id')];
		$("#original-price").val(formatMoney(item.cost));
		$("#overide-price").attr("placeholder", formatMoney(item.cost)).focus();
		$("#overrideModalProductName").html(item.name)
		$('#priceOverrideModal').modal('show').attr("data-id", $('#productMenu').attr('data-id'));
		$('#productMenu').modal('hide');
	});
	$("#showProductInfo").on("click", function() {
		showProduct(getTransaction().products[$('#productMenu').attr('data-id')].barcode);
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
		url: CONTEXT + "kvs.jsp?function=GETALLSUPPLIERS",
		success: function(data) {
			$.each(data.suppliers, function(key, item) {
				window.supplierArray[item.id] = item;
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
			url: CONTEXT + "kvs.jsp?function=SEARCH",
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
					item.id = key;
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
			url: CONTEXT + "kvs.jsp?function=BARCODE",
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
		$(focus).val(formatMoney(newValue)).trigger("change").focus();
	});
	$("#update-product").click(function() {
		var barcode = $("#ProductBarcode").val();
		delete window.cache[barcode];
		$.ajax({
			url: CONTEXT + "kvs.jsp?function=UPDATEPRODUCT",
			data : {
				"id":$("#product-modal").attr("product-id"), 
				"cashier":"", "barcode":barcode, 
				"current_stock":$("#currentLevel").val(),
				"max_stock":$("#maxStockLevel").val(), 
				"department":$("#ProductDepartment").val(), 
				"name" : $("#ProductName").val(), 
				"cost" : 0.00, 
				"price" : $("#ProductPrice").val(), 
				"supplier":$("#ProductSupplier").val(),
				"autoPricingUpdateEnabled": $("#auto-pricing-enabled").is(":checked"),
				"supplierPrice": $("#SupplierPrice").val(),
				"unitsInCase": $("#unitsInCase").val(),
				"includesVAT": $("#includesVAT").is(":checked"),
				"VATamount":$("#VATamount").val(),
				"targetPercentage":$("#targetPercentage").is(":checked"),
				"targetProfitMargin":$("#targetProfitMargin").val()
			},
			success: function(data) {
				if (!data.success) {
					bootbox.alert("Product Not Updated");
					return;
				}
				var id = $("#product-modal").attr("product-id");
				getTransaction().products[id].cost = $("#ProductPrice").val();
				getTransaction().products[id].price = $("#ProductPrice").val();
				getTransaction().products[id].department = $("#ProductDepartment").val();
				getTransaction().products[id].name = $("#ProductName").val();
				$("#product-modal").modal("hide");
				refreshTable();
			}
		});
	});
	$("#newProductSave").click(function() {
		$.ajax({
			url: CONTEXT + "kvs.jsp?function=UPDATEPRODUCT",
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
			url: CONTEXT + "kvs.jsp?function=PRINTLABEL",
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
		url : CONTEXT + "kvs.jsp?function=DEPARTMENTS",
		success : function(data) {
			var holder = document.getElementById("department");
			$(holder).empty();
			var row = el("section", {class:"row"});
			var sortedData = data.sort(function(a, b) {
				if (a.name > b.name) {
					return 1;
				} 
				if (a.name < b.name) {
					return -1;
				}
				return 0;
			});
			$.each(sortedData, function(key, item) {
				if ((!isUndefined(item.deleted)) && (!isZero(item.deleted))) {
					return;
				}
				var col = el("section", {class:"col-md-6 col-sm-6 col-xs-6 col-lg-4"});
				var btn = el("button", {class:"btn btn-default", productDepartment:item.id, style:"border-right:5px solid " + item.colour + ";border-left:5px solid " + item.colour, html:item.shorthand});
				col.appendChild(btn);
				row.appendChild(col);
				var option = el("option", {html:item.name, value:item.id});
				$("#ProductDepartment").append(option);
				var option = el("option", {html:item.name, value:item.id});
				$("#newProductDepartment").append(option);
			});
			holder.appendChild(row);
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

			

