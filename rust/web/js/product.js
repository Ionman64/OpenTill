function getParam(name) {
  	var params = {};
	if (location.search) {
		var parts = location.search.substring(1).split('&');
		for (var i = 0; i < parts.length; i++) {
			var nv = parts[i].split('=');
			if (!nv[0]) continue;
			params[nv[0]] = nv[1] || true;
		}
	}
	return (params[name] ? params[name] : null);	
}
var delay = (function(){
  var timer = 0;
  return function(callback, ms){
  clearTimeout (timer);
  timer = setTimeout(callback, ms);
 };
})();
function showSearches(searchString) {
	if ((searchString.indexOf("[") > -1) || (searchString.indexOf("]") > -1) || (searchString.length == 0)) {
		var holder = $("#item-search-list")[0];
		$(holder).empty();
		$("#item-search").addClass("hidden");
		return;
	}
	$.ajax({
		url: "api/kvs.jsp?function=SEARCH",
		data : {"search" : searchString},
		success : function(data) {
			var holder = $("#item-search-list")[0];
			$(holder).empty();
			$("#item-search").removeClass("hidden");
			$.each(data, function(key, item) {
				if ($("#search-for-product").val().length == 0) {
					$("#item-search").addClass("hidden");
					return;
				}
				var li = document.createElement("li");
				li.className = "btn btn-default";
				$(li).attr("product-data", key);
				var span1 = document.createElement("span");
				span1.innerHTML = item.name + "<br>@" + formatMoney(item.price, "Â£");
				li.appendChild(span1);
				holder.appendChild(li);	
			});
		}	
	});
}
function formatMoney(amount, prefix) {
	var prefix = prefix || "";
	return accounting.formatMoney(amount, prefix);
}
$(document).ready(function() {
	$.ajaxSetup({
		dataType:"JSON",
		method:"POST"
	});
	$("#generate-label").click(function() {
		$.ajax({
			url:"api/kvs.jsp?function=GENERATELBXLABEL",
			dataType: "JSON",
			data:{"id":getParam("id")},
			success: function(data) {
				if (!data.success) {
					alert("Error Creating Label");
					return;
				}
				window.open(data.file, 'Download');  
			},
			error: function() {
				alert("Error Creating Label");
			}
		});
	});
	$("#search-for-product").keyup(function() {
		var text = $(this).val();
		if (text.length == 0) {
			$("#item-search").addClass("hidden");
			return;
		}
		if (/^\d+$/.test(text) == false) {
			delay(function() {showSearches(text)}, 200);
		}
	});
	$("#item-search-list").on("click", "li", function() {
		/*if (!getOperator()) {
			bootbox.alert("Please scan your operator id to continue");
			return;
		}
		if (!getTransaction()) {
			createTransaction()
		}*/
		location.href = "product.jsp?id=" + $(this).attr("product-data");
		$("#item-search").addClass("hidden");
		$("#search-for-product").val("");
	});
	$.ajax({
		url:"api/kvs.jsp?function=GETPRODUCT",
		data:{"id":getParam("id")},
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
			getSalesData(14, getParam("id"));
		}	
	});
});
function getSalesData(daysToLookBack, id) {
	$.ajax({
		url:"api/kvs.jsp?function=GETPRODUCTSALES",
		data:{"start":(moment(moment().subtract(daysToLookBack-1, "days").format("YYYY-MM-DD")).format("x")/1000), "end":(moment(moment().add(1, "days").format("YYYY-MM-DD")).format("x")/1000)-1, "id":id},
		success:function(data) {
			if (!data.success) {
				alert("There has been an error");
				return;
			}
			var date = moment();
			date.subtract(daysToLookBack, "days");
			var SevenDaysAgoTimestamp = moment(moment().format("YYYY-MM-DD")).subtract(7, "days").format("x");
			var salesData = {};
			var productSales = 0;
			for (var i=0;i!==daysToLookBack;i++) {
				salesData[date.add(1, "days").format("dd Do MMM")] = 0;
			}
			$.each(data.sales, function(key, item) {
				salesData[moment(item.created*1000).format("dd Do MMM")]++;
				if (moment(item.created*1000).format("x") > SevenDaysAgoTimestamp) {
					productSales++;	
				}
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
	        backgroundColor: "#3333ff",
	        borderColor: "#3333ff",
	        borderWidth: 1,
	        data: data
	    }]
	};
	var ctx = document.getElementById("canvas").getContext("2d");
	window.myBar = new Chart(ctx, {
		type: 'bar',
		data: barChartData,
		options: {
			responsive: true,
			legend: {
				position: 'top',
			},
			title: {
				display: true,
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