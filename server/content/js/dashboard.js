const TAKINGS_INTERVAL_HOUR = "HOUR";
const TAKINGS_INTERVAL_DAY = "DAY";
const TAKINGS_INTERVAL_WEEK = "WEEK";
const TAKINGS_INTERVAL_MONTH = "MONTH";

var CHARTS = [undefined, undefined];
var TAKINGS_CHART = 0;
var TAKINGS_BY_DEPARTMENT_CHART = 1;

var OVERVIEW_CURRENT_DAY = undefined;

var OVERVIEW_AJAX_REQUESTS = [undefined, undefined, undefined, undefined, undefined, undefined];
var GET_BEST_SELLING_PRODUCTS = 0;
var GET_OPERATOR_TAKINGS_TOTAL = 1;
var GET_TAKINGS_BY_DEPARTMENTS = 2;
var GET_DASHBOARD_TOTALS = 3;
var GET_TAKINGS_CHART = 4;
var GET_TAKINGS = 5;

var DEPARTMENTS = {};

var promiseDepartments = new Promise(function(resolve, reject) {
	const xhr = new XMLHttpRequest();
    xhr.open("POST", "api/kvs.php?function=GETALLDEPARTMENTS");
    xhr.onload = () => resolve(xhr.responseText);
    xhr.onerror = () => reject(xhr.statusText);
    xhr.send();
});

var promiseOrders = new Promise(function(resolve, reject) {
	const xhr = new XMLHttpRequest();
    xhr.open("POST", "api/kvs.php?function=GETORDERS");
    xhr.onload = () => resolve(xhr.responseText);
    xhr.onerror = () => reject(xhr.statusText);
    xhr.send();
});

var promiseUsers = new Promise(function(resolve, reject) {
	const xhr = new XMLHttpRequest();
    xhr.open("POST", "api/kvs.php?function=GETALLOPERATORS");
    xhr.onload = () => resolve(xhr.responseText);
    xhr.onerror = () => reject(xhr.statusText);
    xhr.send();
});

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

function isZero(m) {
	if (m == 0) {
		return true;
	}
	return false;
}

function getAllDepartments() {
	$.ajax({
		url:"api/kvs.php?function=GETALLDEPARTMENTS",
		success:function(data) {
			if (isZero(data.length)) {
				bootbox.alert("There has been an error");
				return;
			}
			$.each(data, function(key, value) {
				DEPARTMENTS[value.id] = value.name;
				var option = el("option", {value:value.id});
				option.innerHTML = value.name;
				$("#ProductDepartment").append(option);
			});
		}
	});
}

function isUndefined(m) {
	return m == undefined ? true : false;
}

function getTakings() {
	OVERVIEW_AJAX_REQUESTS[GET_TAKINGS] = $.ajax({
		url:"api/kvs.jsp?function=TAKINGS",
		data:{"end":moment(OVERVIEW_CURRENT_DAY).startOf('day').add(1, "days").subtract(1, "seconds").unix(), "start":moment(OVERVIEW_CURRENT_DAY).startOf('day').subtract(6, "days").unix()},		
		dataType: "JSON",
		success: function(data) {
			var holder = $("#takings-container")[0];
			$(holder).empty();
			var runningDate = moment(OVERVIEW_CURRENT_DAY).subtract(6, "days").startOf('day');
			while (runningDate.add(1, "days").isBefore(moment(OVERVIEW_CURRENT_DAY).add(1, "seconds"))) {
					var date = runningDate.format("YYYY-MM-DD");
					var values = data[date];
					var col;
					if (!runningDate.isSame(OVERVIEW_CURRENT_DAY)) {
						col = el("section", {class:"col-lg-2 col-md-2 hidden-sm hidden-xs"});
					}
					else {
						col = el("section", {class:"col-lg-2 col-md-2 col-sm-12 col-xs-12"});
					}
					var panel = el("section", {class:"panel panel-primary"});
					if (runningDate.isSame(OVERVIEW_CURRENT_DAY)) {
						panel = el("section", {class:"panel panel-primary highlight-border"});
					}
					
					var panelBody = el("section", {class:"panel-body"});
					var table = el("table", {class:"table"});
					var tbody = el("tbody");
					
					var total = 0.0;
					
					$.each(DEPARTMENTS, function(key, department) {
						var tr = el("tr");
						var td = el("td", {html:department});
						tr.appendChild(td);
						total += !isUndefined(data[date]) ? (!isUndefined(data[date][key]) ? data[date][key] : 0.0) : 0.0;
						var td = el("td", {class:"text-right", html:formatMoney(!isUndefined(data[date]) ? (!isUndefined(data[date][key]) ? data[date][key] : 0.0) : 0.0)});
						tr.appendChild(td);
						tbody.appendChild(tr);
					});
					
					var panelHeading = el("section", {class:"panel-heading text-center"});
					var dateH4 = el("h4", {html:runningDate.format("ddd Do MMM YY")});
					var yearH2 = el("h2", {html:formatMoney(total)});
					panelHeading.appendChild(dateH4);
					panelHeading.appendChild(yearH2);
					panel.appendChild(panelHeading);
					
					
					table.appendChild(tbody);
					panelBody.appendChild(table);
					panel.appendChild(panelBody);
					
					col.appendChild(panel);
					holder.appendChild(col);
			}
		}
	});
}


function logout() {
	$.ajax({
		url:"api/kvs.jsp?function=LOGOUT",
		dataType: "JSON",
		success: function(data) {
			if (data.success) {
				window.location = "login.jsp";
			}
		}
	});
}
function getTakingsChart(takings_interval) {
	$.ajax({
		url:"api/kvs.jsp?function=GENERATETAKINGSGRAPH",
		data:{"time_interval":takings_interval, "end":moment(OVERVIEW_CURRENT_DAY).startOf('day').add(1, "days").subtract(1, "seconds").unix(), "start":moment(OVERVIEW_CURRENT_DAY).startOf('day').unix()},		
		dataType: "JSON",
		success: function(data) {
			if (CHARTS[TAKINGS_CHART] != undefined) {
				CHARTS[TAKINGS_CHART].destroy();
			}
			var ctx = document.getElementById("overview-takings-graph").getContext("2d");
			CHARTS[TAKINGS_CHART] = new Chart(ctx, data);
		}
	});
}

function showProduct(id) {
	$.ajax({
		url: "api/kvs.jsp?function=GETPRODUCT",
		data : {"id":id},
		dataType: "JSON",
		success: function(data) {
			if (!data.success) {
				return;
			}
			var product = data.product;
			$("#product-modal").attr("product-id", product.id);
			$("#ProductBarcode").val(product.barcode);
			$("#ProductName").val(product.name);
			$("#ProductDepartment").val(product.department)
			$("#ProductCost").val(product.cost);
			$("#ProductPrice").val(product.price);
			$("#Name").html(product.name);
			$("#maxStockLevel").val(product.max_stock);
			$("#currentLevel").val(product.current_stock);
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
function getBestSellingProducts() {
	var takings_interval = "HOUR";
	OVERVIEW_AJAX_REQUESTS[GET_BEST_SELLING_PRODUCTS] = $.ajax({
		url: "api/kvs.jsp?function=TOPPRODUCTSALES",
		data:{"time_interval":takings_interval, "end":moment(OVERVIEW_CURRENT_DAY).startOf('day').add(1, "days").subtract(1, "seconds").unix(), "start":moment(OVERVIEW_CURRENT_DAY).startOf('day').unix()},		
		dataType: "JSON",
		success: function(data) {
			var table = $("#best-selling-products")[0];
			$(table).empty();
			$.each(data, function(key, item) {
				var tr = el("tr");
				var td = el("td", {class:"bold", html:key+1});
				tr.appendChild(td);
				
				var td = el("td");
				var btn = el("button", {class:"btn btn-default btn-lg product-btn", html:item.name, "data-id":item.id});
				td.appendChild(btn);
				tr.appendChild(td);
				
				var td = el("td", {html:item.value});
				tr.appendChild(td);
				table.appendChild(tr);
			});
			$("button.product-btn").on("click", function() {
				showProduct($(this).attr("data-id"));
			});
		}
	});
}
function getDashboardTotals() {
	var takings_interval = "HOUR";
	OVERVIEW_AJAX_REQUESTS[GET_DASHBOARD_TOTALS] = $.ajax({
		url:"api/kvs.jsp?function=GETOVERVIEWTOTALS",
		data:{"time_interval":takings_interval, "end":moment(OVERVIEW_CURRENT_DAY).startOf('day').add(1, "days").subtract(1, "seconds").unix(), "start":moment(OVERVIEW_CURRENT_DAY).startOf('day').unix()},
		dataType: "JSON",
		success: function(data) {
			if (data.success == false) {
				return;
			}
			$("#revenue-total").html(formatMoney(data.revenue));
			$("#transactions-count").html(data.number);
			$("#payouts-total").html(formatMoney(data.payouts));
			$("#cash-in-drawer").html(formatMoney(data.cashInDrawer));
		}
	});
}
function getTakingsByDepartments() {
	var takings_interval = "HOUR";
	OVERVIEW_AJAX_REQUESTS[GET_TAKINGS_BY_DEPARTMENTS] = $.ajax({
		url:"api/kvs.jsp?function=GETTAKINGSBYDEPARTMENT",
		data:{"data-only":false, "time_interval":takings_interval, "end":moment(OVERVIEW_CURRENT_DAY).startOf('day').add(1, "days").subtract(1, "seconds").unix(), "start":moment(OVERVIEW_CURRENT_DAY).startOf('day').unix()},		
		dataType: "JSON",
		dataType: "JSON",
		success: function(data) {
			if (data.success == false) {
				$("#departments-takings-graph").html("No Data");
				return;
			}
			if (CHARTS[TAKINGS_BY_DEPARTMENT_CHART] != undefined) {
				CHARTS[TAKINGS_BY_DEPARTMENT_CHART].destroy();
			}
			var ctx = document.getElementById("departments-takings-graph").getContext("2d");
			CHARTS[TAKINGS_BY_DEPARTMENT_CHART] = new Chart(ctx, data);
		}
	});
	$.ajax({
		url:"api/kvs.jsp?function=GETTAKINGSBYDEPARTMENT",
		data:{"data-only":true, "time_interval":takings_interval, "end":moment(OVERVIEW_CURRENT_DAY).startOf('day').add(1, "days").subtract(1, "seconds").unix(), "start":moment(OVERVIEW_CURRENT_DAY).startOf('day').unix()},		
		dataType: "JSON",
		success: function(data) {
			if (data.success == false) {
				$("#department-totals").html("No Data");
				return;
			}
			var table = $("#department-totals")[0];
			$(table).empty();
			$.each(data, function(key, item) {
				var tr = el("tr");
				
				var td = el("td", {html:item.name});
				tr.appendChild(td);
				
				var td = el("td", {html:formatMoney(item.value)});
				tr.appendChild(td);
				table.appendChild(tr);
			});
		}
	});
}
function getOperatorTakingsTotal() {
	var takings_interval = "HOUR";
	OVERVIEW_AJAX_REQUESTS[GET_OPERATOR_TAKINGS_TOTAL] = $.ajax({
		url: "api/kvs.jsp?function=GETOPERATORTOTALS",
		data:{"time_interval":takings_interval, "end":moment(OVERVIEW_CURRENT_DAY).startOf('day').add(1, "days").subtract(1, "seconds").unix(), "start":moment(OVERVIEW_CURRENT_DAY).startOf('day').unix()},
		dataType: "JSON",
		success: function(data) {
			var table = $("#operators-totals")[0];
			$(table).empty();
			$.each(data, function(key, item) {
				var tr = el("tr");
				
				var td = el("td", {html:item.name});
				tr.appendChild(td);
				
				var td = el("td", {html:item.value});
				tr.appendChild(td);
				table.appendChild(tr);
			});
		}
	});
}
function loadModals() {
	window.modals = $(".import-modal").length;
	if (window.modals == 0) {
		loadDashboard();
	}
	$(".import-modal").each(function(key, el) {
		$(el).load(this.getAttribute("page"), function() {
			$(el).replaceWith(function() { return $(this).contents(); });
			if (--window.modals == 0) {
				loadModals();
			}
		});
	});
}
$(document).ready( function() {
	$.ajaxSetup({
		method:"POST",
		dataType:"json"
	});
	loadModals();
});
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
function loadOverview() {
	$.each(OVERVIEW_AJAX_REQUESTS, function(key, value) {
		if (value != undefined) {
			value.abort();
		}
	});
	getBestSellingProducts();
	getOperatorTakingsTotal();
	getTakingsByDepartments();
	getDashboardTotals();
	getTakingsChart(TAKINGS_INTERVAL_HOUR);
	$("#overview-select-day").val(moment(OVERVIEW_CURRENT_DAY).format("YYYY-MM-DD"));
	getTakings();
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

function getTransaction() {
	$.ajax({
		url:"api/kvs.php?function=GETTRANSACTIONS",
		data:{"end":moment(OVERVIEW_CURRENT_DAY).startOf('day').add(1, "days").subtract(1, "seconds").unix(), "start":moment(OVERVIEW_CURRENT_DAY).startOf('day').unix()},
		dataType: "JSON",
		method:"POST",
		success: function(data) {
			var holder = document.getElementById("transactions-viewport");
			$(holder).empty();
			if (isZero(data.length)) {
				var h3 = el("h3");
				h3.innerHTML = "No Transactions Yet";
				holder.appendChild(h3);
				return;
			}
			$.each(data, function(key, item) {
				var className = "";
				if (item.type) {
					if (item.type == "PAYOUT") {
						className = "row selectable payout";
					}
					else {
						className = "row selectable payin";
					}
				}
				var row = el("section", {class:className});
				if (item.id) {
					row.setAttribute("data-id", item.id);
				}
				//Date
				var col = el("section", {class:"col-lg-2 col-md-3 col-sm-3 col-xs-6"});
				var label = el("h4", {html:moment(item.ended*1000).format("hh:mm:ss a")});
				col.appendChild(label);
				row.appendChild(col);
				//Cashier
				var col = el("section", {class:"col-lg-2 col-md-3 col-sm-1 col-xs-3"});
				var label = el("h4", {html:item.cashier});
				col.appendChild(label);
				row.appendChild(col);
				//Products
				var col = el("section", {class:"col-lg-2 col-md-1 col-sm-1 hidden-xs"});
				var label = el("h4", {html:item["#Products"]});
				col.appendChild(label);
				row.appendChild(col);
				//Card
				var col = el("section", {class:"col-lg-2 col-md-1 col-sm-1 hidden-xs"});
				var label = el("h4", {html:item.card});
				col.appendChild(label);
				row.appendChild(col);
				//Cashback
				var col = el("section", {class:"col-lg-1 col-md-1 col-sm-1 hidden-xs"});
				var label = el("h4", {html:item.cashback});
				col.appendChild(label);
				row.appendChild(col);
				//money_given
				var col = el("section", {class:"col-lg-1 col-md-1 col-sm-3 hidden-xs"});
				var label = el("h4", {html:item.money_given});
				col.appendChild(label);
				row.appendChild(col);
				//type 
				var col = el("section", {class:"col-lg-1 col-md-1 col-sm-1 hidden-xs"});
				var label = el("h4", {html:item.type});
				col.appendChild(label);
				row.appendChild(col);
				//total
				var col = el("section", {class:"col-lg-1 col-md-1 col-sm-1 col-xs-3"});
				var label = el("h4", {html:item.total});
				col.appendChild(label);
				row.appendChild(col);
				holder.appendChild(row);
			});		
			var row=el("section", {class:"row"});
			var col = el("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12"});
			var label = el("label", {class:"text-center", style:"width:100%;"});
			var i = el("i", {class:"fa fa-spinner fa-spin fa-3x"});
			label.appendChild(i);
			label.appendChild(document.createTextNode("Loading"));
			col.appendChild(label);
			row.appendChild(col);
			//holder.appendChild(row);
		}
	});
}

function showDepartment(id) {
	$.ajax({
		url:"api/kvs.jsp?function=GETDEPARTMENT",
		data:{"id":id},
		success:function(data) {
			if (!data.success) {
				bootbox.alert("There was an error");
				return;
			}
			var department = data.department;
			$("#department-name").val("");
			$("#department-colour").val("");
			$("#department-shorthand").val("");
			$("#department-comments").val("");
			
			$("#department-name").val(department.name);
			$("#department-shorthand").val(department.shorthand);
			$("#department-colour").val(department.colour);
			$("#department-comments").val(department.comments);
			$("#departmentInfo").attr("data-id", department.id).modal("show");
		}
	});
}

function saveDepartment(id) {
	var name = $("#new-department-name").val();
	var shorthand = $("#new-department-shorthand").val();
	var colour = $("#new-department-colour").val();
	var comments = $("#new-department-comments").val();
	if (id == null) {
		$.ajax({
			url:"api/kvs.jsp?function=ADDDEPARTMENT",
			data:{"name":name, "shorthand":shorthand, "comments":comments, "colour":colour},
			success:function(data) {
				if (!data.success) {
					bootbox.alert("There has been an error");
				}
				$("#create-department-modal").modal("hide");
				getDepartments();
			}
		});
		return;
	}	
	var name = $("#department-name").val();
	var shorthand = $("#department-shorthand").val();
	var colour = $("#department-colour").val();
	var comments = $("#department-comments").val();
	$.ajax({
		url:"api/kvs.jsp?function=UPDATEDEPARTMENT",
		data:{"id":id, "name":name, "shorthand":shorthand, "comments":comments, "colour":colour},
		success:function(data) {
			if (!data.success) {
				bootbox.alert("There has been an error");
			}
			$("#departmentInfo").attr("data-id", null).modal("hide");
			getDepartments();
		}
	});
}

function loadDashboard() {
	OVERVIEW_CURRENT_DAY = moment().startOf("day");
	setupAutoPricing();
	promiseDepartments.then(JSON.parse).then(function(data){
		var holder = $("#departments-viewport")[0];
		$(holder).empty();
		
		$.each(data, function(key, value) {
			DEPARTMENTS[value.id] = value.name;
			var option = el("option", {value:value.id});
			option.innerHTML = value.name;
			$("#ProductDepartment").append(option);
			
			var col = el("section", {class:"col-md-2"});
			var departmentSection = el("section", {class:"department-block", "data-id":value.id});
			var name = el("h3", {html:value.name});
			var h4 = el("h4", {class:"italic", html:value["n_products"] + " Products"});
			departmentSection.appendChild(name);
			departmentSection.appendChild(h4);
			col.appendChild(departmentSection);
			holder.appendChild(col);
		});
		
		var col = el("section", {class:"col-md-2"});
		var departmentSection = el("section", {style:"background:#eeeeee", id:"add-department-btn", class:"department-block", "data-id":null});
		var name = el("h4", {html:"Add New Department"});
		departmentSection.appendChild(name);
		col.appendChild(departmentSection);
		holder.appendChild(col);
	}).catch(function() {bootbox.alert("There has been an error")});
	promiseOrders.then(JSON.parse).then(function(data){
		var holder = $("#orders-table")[0];
		$(holder).empty();
		console.log(data);
		$.each(data, function(key, value) {
			DEPARTMENTS[value.id] = value.name;
			var option = el("option", {value:value.id});
			option.innerHTML = value.name;
			$("#ProductDepartment").append(option);
			
			var col = el("section", {class:"col-md-2"});
			var departmentSection = el("section", {class:"department-block", "data-id":value.id});
			var name = el("h3", {html:value.name});
			var h4 = el("h4", {class:"italic", html:value["n_products"] + " Products"});
			departmentSection.appendChild(name);
			departmentSection.appendChild(h4);
			col.appendChild(departmentSection);
			holder.appendChild(col);
		});
		
		var col = el("section", {class:"col-md-2"});
		var departmentSection = el("section", {style:"background:#eeeeee", id:"add-department-btn", class:"department-block", "data-id":null});
		var name = el("h4", {html:"Add New Order"});
		departmentSection.appendChild(name);
		col.appendChild(departmentSection);
		holder.appendChild(col);
	}).catch(function() {bootbox.alert("There has been an error with orders")});
	promiseUsers.then(JSON.parse).then(function(data){
		var holder = $("#operators-table")[0];
		$(holder).empty();
		console.log("Users");
		console.log(data);
		$.each(data, function(key, value) {
			var col = el("section", {class:"col-md-2"});
			var departmentSection = el("section", {class:"department-block", "data-id":value.id});
			var name = el("h3", {html:value.name});
			var h4 = el("h4", {class:"italic", html:value.type});
			departmentSection.appendChild(name);
			departmentSection.appendChild(h4);
			col.appendChild(departmentSection);
			holder.appendChild(col);
		});
		
		var col = el("section", {class:"col-md-2"});
		var departmentSection = el("section", {style:"background:#eeeeee", id:"add-user-btn", class:"department-block", "data-id":null});
		var name = el("h4", {html:"Add New User"});
		departmentSection.appendChild(name);
		col.appendChild(departmentSection);
		holder.appendChild(col);
	}).catch(function() {bootbox.alert("There has been an error with users")});
	$("#departments-viewport").on("click", ".department-block:not(#add-department-btn)", function() {
		showDepartment($(this).attr("data-id"));
	});
	$("#departments-viewport").on("click", "#add-department-btn", function() {
		$("#create-department-modal").modal("show");
	});
	loadOverview();
	$("#overview-prev-day").click(function() {
		OVERVIEW_CURRENT_DAY.subtract(1, "days");
		$("#overview-next-day").attr("disabled", false);
		loadOverview();
	});
	$("#overview-next-day").click(function() {
		OVERVIEW_CURRENT_DAY.add(1, "days");
		if (moment(OVERVIEW_CURRENT_DAY).add(1, "days").isAfter(moment())) {
			$(this).attr("disabled", true);
		}
		loadOverview();
	});
	$("#autoupdateoverview").bootstrapToggle({
		"on":"Auto Update",
		"off":"Manual",
		size:"large"
	}).on("change", function() {
		//alert("");
	});
	
	$("#overview-show-transactions").on("click", function() {
		$("#transactions-modal").modal("show");
	});
	
	$("#update-department").on("click", function() {
		window.departments.saveDepartment($("#departmentInfo").attr("data-id"));
	});
	$("#add-department").on("click", function() {
		window.departments.createDepartment();
	});
	$("#delete-department").on("click", function() {
		window.departments.deleteDepartment();
	});
	$("#departments-viewport").on("click", ".selectable", function() {
		window.departments.showDepartment($(this).attr("data-id"));
	});
	$("#add-department-btn").click(function() {
		$("#create-department-modal").modal("show");
	});
	$("#create-department").on("click", function() {
		saveDepartment();
	});
	
	
	var calendar = new CalendarView(document.getElementById("overview-select-day"), false, function() {
		OVERVIEW_CURRENT_DAY = moment($("#overview-select-day").val(), "YYYY-MM-DD");
		if (moment(OVERVIEW_CURRENT_DAY).add(1, "days").isAfter(moment())) {
			$("#overview-next-day").attr("disabled", true);
		}
		else {
			$("#overview-next-day").attr("disabled", false);
		}
		loadOverview();
	});
	calendar.init();
	$("#takings-export-btn").click(function() {
		$("#takings-date-start").val(moment(OVERVIEW_CURRENT_DAY).format("YYYY-MM-DD"));
		$("#takings-date-end").val(moment(OVERVIEW_CURRENT_DAY).format("YYYY-MM-DD"));
		var holder = document.getElementById("takings-departments-export");
		$(holder).empty();
		$.each(DEPARTMENTS, function(id, department) {
			var option = el("option", {value:id, html:department});
			holder.appendChild(option);
		});
		$("#takings-export-success").addClass("hidden");
		$("#takings-export-failure").addClass("hidden");
		$("#export-takings").modal("show");
	});
	$("#takings-interval button").on("click", function() {
		$("#takings-interval button").removeClass("active");
		$(this).addClass("active");
		var takings_interval = $(this).attr("data-id");
		switch (takings_interval) {
			case "day":
				getTakingsChart(TAKINGS_INTERVAL_HOUR);
				break;
			case "week":
				getTakingsChart(TAKINGS_INTERVAL_DAY);
				break;
			case "month":
				getTakingsChart(TAKINGS_INTERVAL_WEEK);
				break;
			case "year":
				getTakingsChart(TAKINGS_INTERVAL_MONTH);
				break;
			default:
				getTakingsChart(TAKINGS_INTERVAL_HOUR);
				break;
		}
	});
	$("#takings-departments-export").on("change", function() {
		if ($("#takings-departments-export option:checked").length == 0) {
			$("#takings-export").attr("disabled", true);
		}
		else {
			$("#takings-export").attr("disabled", false);
		}
	});
	$("#takings-export").click(function() {
		var selectedDepartments = [];
		$("#takings-departments-export option:checked").each(function() {
			selectedDepartments.push(this.value);
		});
		$.ajax({
			url:"api/kvs.jsp?function=GENERATETAKINGSREPORT",
			dataType: "JSON",
			data:{"takings-export-type":$("#takings-export-type").val(), "start":moment($("#takings-date-start").val(), "YYYY-MM-DD").format("x"), "end":moment($("#takings-date-end").val(),  "YYYY-MM-DD").format("x"), "departments":selectedDepartments},
			beforeSend: function() {
				$("#takings-export-progress").removeClass("hidden");
			},
			success: function(data) {
				if (!data.success) {
					$("#takings-export-failure").removeClass("hidden");
					return;
				}
				$("#takings-export-success").removeClass("hidden");
				$("#takings-export-alt-download").attr("href", data.file);
				window.open(data.file, 'Download');  
			},
			error: function() {
				$("#inventory-export-failure").removeClass("hidden");
			},
			complete: function() {
				$("#takings-export-progress").addClass("hidden");
			}
		});
	});
	$(".custom-navigation").removeClass("hidden").addClass("animated fadeInDown");
	$("#main-navigation li:not(:first-child)").click(function() {
		$("#main-navigation li").removeClass("active");
		$(this).addClass("active");
		if (!$(this).attr("data-page")) {
			return;
		}
		$(".tab").addClass("hidden");
		$("#" + $(this).attr("data-page")).removeClass("hidden");
		$("#page-name").html($("#" + $(this).attr("data-page")).attr("data-page-name"));
	});
	requirejs(["js/transactions", "js/operators", "js/suppliers", "js/inventory", "js/departments", "js/orders"], function() {
		console.log("Loaded Scripts");
	});
	$("#logout").click(function() {
		logout();
	});
	$.ajax({
		url:"api/kvs.jsp?function=GETUSERINFO",
		success: function(data) {
			$("#user-name").html(data.name);
			$("#user-role").html(data.type);
			
		}
	});
	$(".menu-buttons").on("click", ".menu-button", function() {
		$("#menu").addClass("hidden");
		if (!$(this).attr("data-page")) {
			return;
		}
		$(".tab").addClass("hidden");
		$("#" + $(this).attr("data-page")).removeClass("hidden");
		$("#page-name").html($("#" + $(this).attr("data-page")).attr("data-page-name"));
		$("#page-dropdown-menu").empty();
		if ($(this).attr("data-page").toLowerCase()) {
			$.each(window.operators.menuItems, function(key, value) {
				var li = el("li");
				var a = el("a", {html:key, "data-id":key, class:"context-menu-btn"});
				li.appendChild(a);
				$("#page-dropdown-menu").append(li);
			});
		}
		$(".context-menu-btn").on("click", function() {
			window.operators.createOperator();
		});
	});
	$("#productModalNav").on("click", "a", function() {
		$("#productModalNav li").removeClass("active");
		$(this).parent().addClass("active");
		$("#product-modal .page").addClass("hidden");
		$("section[data-id=" + $(this).attr("data-page") + "]").removeClass("hidden");
	});
	$("#delete-product").click(function() {
		var id = $("#product-modal").attr("product-id");
		bootbox.confirm("Are you sure you want to delete this product from the system?", function(result) {
			if (!result) {
				return;
			}
			$.ajax({
				url:"api/kvs.jsp?function=DELETEPRODUCT",
				data:{"id":id},
				success: function(data) {
					if (!data.success) {
						bootbox.alert("Could not remove product");
						return;
					}
					$("#product-modal").modal("hide");
					$("button[data-id='" + id + "']").parents(".row").first().remove()
				}
			});
		});
	});
	//var calendar = new CalendarView($("#takings-date-start")[0]);
	//calendar.init();
	//var calendar2 = new CalendarView($("#takings-date-end")[0]);
	//calendar2.init();
	$("#update-product").click(function() {
		var barcode = $("#ProductBarcode").val();
		$.ajax({
			url: "api/kvs.jsp?function=UPDATEPRODUCT",
			data : {"id":$("#product-modal").attr("product-id"), "cashier":"", "barcode":barcode, "current_stock":$("#currentLevel").val(),"max_stock":$("#maxStockLevel").val(), "department":$("#ProductDepartment").val(), "name" : $("#ProductName").val(), "cost" : 0.00, "price" : $("#ProductPrice").val()},
			success: function(data) {
				if (!data.success) {
					bootbox.alert("Product Not Updated");
					return;
				}
				$("#product-modal").modal("hide");
			}
		});
	});
	$("#menu-button").click(function() {
		$("#menu").hasClass("hidden") ? $("#menu").removeClass("hidden") : $("#menu").addClass("hidden");
	});
	$(window).resize(function() {
		$('.viewport').height($(window).height() - 107);
	});
	$(window).trigger('resize');
	setInterval(function() {
		$("#timeline").empty();
		var currentPercentage = percentage((moment().hour()*60)+moment().minute(), 24*60);
		var div = el("div", {class:"progress-bar progress-bar-success", style:"width:" + currentPercentage + "%"});
		var span = el("span", {class:"sr-only"});
		div.appendChild(span);
		$("#timeline").append(div);
		var div = el("div", {class:"progress-bar progress-bar-default", style:"width:" + (100-currentPercentage) + "%"});
		var span = el("span", {class:"sr-only"});
		div.appendChild(span);
		$("#timeline").append(div);
	}, 1000);
}

function truncateOnWord(str, limit) {
    var trimmable = '\u0009\u000A\u000B\u000C\u000D\u0020\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u2028\u2029\u3000\uFEFF';
    var reg = new RegExp('(?=[' + trimmable + '])');
    var words = str.split(reg);
    var count = 0;
    var result = words.filter(function(word) {
        count += word.length;
        return count <= limit;
    }).join('');
    if (result == str) {
    	return result;
    }
    return result + "...";
}

function formatMoney(amount, prefix) {
	var prefix = prefix || "";
	return accounting.formatMoney(amount, prefix);
}

function percentage(value, total) {
	return Math.floor(value/total*100);
}
function getSalesData(daysToLookBack, id) {
	$.ajax({
		url:"api/kvs.jsp?function=GETPRODUCTSALES",
		data:{"start":(moment(moment().subtract(daysToLookBack-1, "days").format("YYYY-MM-DD")).format("x")/1000), "end":(moment(moment().add(1, "days").format("YYYY-MM-DD")).format("x")/1000)-1, "id":id},
		success:function(data) {
			if (!data.success) {
				alert("There has been an error");
				return;
			}
			//var date = moment();
			var date = moment(moment().format("YYYY-MM-DD"));
			date.subtract(1, "hours");
			var salesData = {};
			var productSales = 0;
			for (var i=0;i<24;i++) {
				productSales = productSales + 10;
				salesData[date.add(1, "hours").format("HH:mm")] = productSales;
			}
			$.each(data.sales, function(key, item) {
				productSales = productSales + 10;
				salesData[moment(item.created*1000).format("HH:mm")] = productSales;
			});
			$("#product-sales").html(productSales);
			renderGraph(salesData);
		}	
	});
}
