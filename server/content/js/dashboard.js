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
function logout() {
	$.ajax({
		url:"api/kvs.php?function=LOGOUT",
		dataType: "JSON",
		success: function(data) {
			if (data.success) {
				window.location = "login.php";
			}
		}
	});
}
function showProduct(id) {
	$.ajax({
		url: "api/kvs.php?function=GETPRODUCT",
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
$(document).ready(function() {
	$.ajaxSetup({
		method:"POST",
		dataType:"JSON"
	});
	$("#logout").click(function() {
		logout();
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
	$("#delete-product").click(function() {
		var id = $("#product-modal").attr("product-id");
		bootbox.confirm("Are you sure you want to delete this product from the system?", function(result) {
			if (!result) {
				return;
			}
			$.ajax({
				url:"api/kvs.php?function=DELETEPRODUCT",
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
	$("#update-product").click(function() {
		var barcode = $("#ProductBarcode").val();
		$.ajax({
			url: "api/kvs.php?function=UPDATEPRODUCT",
			data : {"id":$("#product-modal").attr("product-id"), "cashier":"", "barcode":barcode, "department":$("#ProductDepartment").val(), "name" : $("#ProductName").val(), "cost" : 0.00, "price" : $("#ProductPrice").val()},
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
	window.takings = new Takings();
	window.takings.init();
	window.transactions = new Transactions();
	window.transactions.init();
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
	$(window).resize(function() {
		$('.viewport').height($(window).height() - 107);
	});
	$(window).trigger('resize');
	$.ajax({
		url:"api/kvs.php?function=GETPRODUCT",
		data:{"id":"EBCDB3C0-E71F-4EFC-AB5F-EDBE1119E687"},
		success:function(data) {
			if (!data.success) {
				alert("Could not find product");
				return;
			}
			$("#product-name").html(data.product.name);	
			if (data.product.updated == 0) {
				$("#product-updated").html("Never");
			}
			else {
				$("#product-updated").html(moment(data.product.updated*1000).format("YYYY-MM-DD"));
			}
			getSalesData(0, "EBCDB3C0-E71F-4EFC-AB5F-EDBE1119E687");
		}	
	});
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
});
function percentage(value, total) {
	return Math.floor(value/total*100);
}
function getSalesData(daysToLookBack, id) {
		$.ajax({
			url:"api/kvs.php?function=GETPRODUCTSALES",
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