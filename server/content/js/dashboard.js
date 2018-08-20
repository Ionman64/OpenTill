const TAKINGS_INTERVAL_HOUR = "HOUR";
const TAKINGS_INTERVAL_DAY = "DAY";
const TAKINGS_INTERVAL_WEEK = "WEEK";
const TAKINGS_INTERVAL_MONTH = "MONTH";

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
			var ctx = document.getElementById("takings-graph").getContext("2d");
			new Chart(ctx, data);
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
function chartTest(id, data) {
	if (!data) {
		$.ajax({
			url:"api/kvs.jsp?function=GETPRODUCTSALES",
			data: {"id":id, "end":moment(moment().format("YYYY-MM-DD")).unix(), "start":moment(moment().subtract(1, "months").format("YYYY-MM-DD")).unix()},
			success:function(data) {
				if (!data.success) {
					return;
				}
				console.log(data);
			}
		});
		$.ajax({
			url:"api/kvs.jsp?function=GETPRODUCTLEVELS",
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
	getTakingsChart(TAKINGS_INTERVAL_HOUR);
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
	function renderGraph(salesData) {
		data = [];
		labels = [];
		$.each(salesData, function(key, value) {
			labels.push(key);
			data.push(value);
		});
		var barChartData = {
			labels: labels,
			datasets: [{
				label: 'Units Sold',
				borderColor: "#3333ff",
				borderWidth: 1,
				data: data
			}]
		};
		var ctx = document.getElementById("canvas").getContext("2d");
		window.myBar = new Chart(ctx, {
			type: 'line',
			data: barChartData,
			options: {
				responsive: true,
				legend: {
					position: 'bottom',
				},
				title: {
					display: false,
					text: 'Sales'
				},
				scales: {
					yAxes: [{
						ticks: {
							beginAtZero:true
						}
					}]
				}
			}
		});
	}