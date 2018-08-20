const TAKINGS_INTERVAL_HOUR = "HOUR";
const TAKINGS_INTERVAL_DAY = "DAY";
const TAKINGS_INTERVAL_WEEK = "WEEK";
const TAKINGS_INTERVAL_MONTH = "MONTH";

var CHARTS = [];
var TAKINGS_CHART = 0;
var TAKINGS_BY_DEPARTMENT_CHART = 1;

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
		data:{"time_interval":takings_interval, "end":moment(moment().subtract(1, "months").format("YYYY-MM-DD")).unix(), "start":moment(moment().subtract(1, "months").subtract(1, "days").format("YYYY-MM-DD")).unix()},
		dataType: "JSON",
		success: function(data) {
			if (CHARTS[TAKINGS_CHART] != undefined) {
				CHARTS[TAKINGS_CHART].destroy();
			}
			var ctx = document.getElementById("takings-graph").getContext("2d");
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
	$.ajax({
		url: "api/kvs.jsp?function=TOPPRODUCTSALES",
		data:{"time_interval":takings_interval, "end":moment(moment().subtract(1, "months").format("YYYY-MM-DD")).unix(), "start":moment(moment().subtract(1, "months").subtract(1, "days").format("YYYY-MM-DD")).unix()},
		dataType: "JSON",
		success: function(data) {
			var table = $("#best-selling-products")[0];
			$(table).empty();
			$.each(data, function(key, item) {
				var tr = el("tr");
				var td = el("td", {class:"bold", html:key+1});
				tr.appendChild(td);
				
				var td = el("td");
				var btn = el("button", {class:"btn btn default", html:"item.name", "data-id":item.id});
				td.appendChild(btn);
				tr.appendChild(td);
				
				var td = el("td", {html:item.value});
				tr.appendChild(td);
				table.appendChild(tr);
			});
		}
	});
}
function getDashboardTotals() {
	var takings_interval = "HOUR";
	$.ajax({
		url:"api/kvs.jsp?function=GETOVERVIEWTOTALS",
		data:{"time_interval":takings_interval, "end":moment(moment().subtract(1, "months").format("YYYY-MM-DD")).unix(), "start":moment(moment().subtract(1, "months").subtract(1, "days").format("YYYY-MM-DD")).unix()},
		dataType: "JSON",
		success: function(data) {
			if (data.success == false) {
				return;
			}
			$("#revenue-total").html(formatMoney(data.revenue));
			$("#transactions-count").html(data.number);
			$("#payouts-total").html(formatMoney(data.payouts));
			$("#cash-in-drawer").html(formatMoney(data.cashInDrawer));
			$("#date-of-overview").html(moment().subtract(1, "months").format("YYYY-MM-DD"));
		}
	});
}
function getTakingsByDepartments() {
	var takings_interval = "HOUR";
	$.ajax({
		url:"api/kvs.jsp?function=GETTAKINGSBYDEPARTMENT",
		data:{"data-only":false, "time_interval":takings_interval, "end":moment(moment().subtract(1, "months").format("YYYY-MM-DD")).unix(), "start":moment(moment().subtract(1, "months").subtract(1, "days").format("YYYY-MM-DD")).unix()},
		dataType: "JSON",
		success: function(data) {
			if (CHARTS[TAKINGS_BY_DEPARTMENT_CHART] != undefined) {
				CHARTS[TAKINGS_BY_DEPARTMENT_CHART].destroy();
			}
			var ctx = document.getElementById("departments-takings-graph").getContext("2d");
			CHARTS[TAKINGS_BY_DEPARTMENT_CHART] = new Chart(ctx, data);
		}
	});
	$.ajax({
		url:"api/kvs.jsp?function=GETTAKINGSBYDEPARTMENT",
		data:{"data-only":true, "time_interval":takings_interval, "end":moment(moment().subtract(1, "months").format("YYYY-MM-DD")).unix(), "start":moment(moment().subtract(1, "months").subtract(1, "days").format("YYYY-MM-DD")).unix()},
		dataType: "JSON",
		success: function(data) {
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
	$.ajax({
		url: "api/kvs.jsp?function=GETOPERATORTOTALS",
		data:{"time_interval":takings_interval, "end":moment(moment().subtract(1, "months").format("YYYY-MM-DD")).unix(), "start":moment(moment().subtract(1, "months").subtract(1, "days").format("YYYY-MM-DD")).unix()},
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
function loadDashboard() {
	getBestSellingProducts();
	getOperatorTakingsTotal();
	getTakingsByDepartments();
	getDashboardTotals();
	getTakingsChart(TAKINGS_INTERVAL_HOUR);
	$("#autoupdateoverview").bootstrapToggle({
		"on":"Auto Update",
		"off":"Manual",
		size:"large"
	}).on("change", function() {
		//alert("");
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
	requirejs(["js/takings", "js/transactions", "js/operators", "js/suppliers", "js/inventory", "js/departments", "js/orders"], function() {
		console.log("Loaded Scripts");
	});
	requirejs(["js/takings", "js/transactions", "js/operators", "js/suppliers", "js/inventory", "js/departments", "js/orders"], function() {
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
	$.ajax({
		url:"api/kvs.jsp?function=DASHBOARD",
		success:function(data) {
			window.dashboard_data = data;
			window.takings = new Takings();
			window.takings.init();
//			window.transactions = new Transactions();
//			window.transactions.init();
			window.operators = new Operators();
			window.operators.init();
			window.suppliers = new Suppliers();
			window.suppliers.init();
			window.inventory = new Inventory();
			window.inventory.init();
			window.departments = new Departments();
			window.departments.init();
			window.orders = new Orders();
			window.orders.init();
		}
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