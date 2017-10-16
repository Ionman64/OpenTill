$(document).ready(function() {
	get_transactions();
	$("#viewport").on("click", "tr", function() {
		if (!$(this).attr("data-id")) {
			return;
		}
		showTransaction($(this).attr("data-id"));
	});
	$("#logout").click(function() {
		logout();
	});
});
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
function el(tagName, options) {
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
	$.each(options, function(key, value) {
		el.setAttribute(key, value);
	});
	return el;
}
function get_transactions() {
	var date = moment();
	var start = moment(date.format("YYYY-MM-DD")).format("x")/1000;
	var end = (moment(date.add(1, "days").format("YYYY-MM-DD")).format("x")/1000)-1;
	$.ajax({
		url:"api/kvs.php?function=GETTRANSACTIONS",
		data:{"start":start, "end":end},
		dataType: "JSON",
		method:"POST",
		success: function(data) {
			if (!data.success) {
				alert("Error collecting transactions");
			}
			$("#dateFrom").html(moment(data.start*1000).format("YYYY-MM-DD"));
			$("#timeFrom").html(moment(data.start*1000).format("HH:mm:ss"));
			$("#dateTo").html(moment(data.end*1000).format("YYYY-MM-DD"));
			$("#timeTo").html(moment(data.end*1000).format("HH:mm:ss"));
			populate_table(data.transactions);
		}
	});
}
function populate_table(data) {
	var holder = document.getElementById("viewport");
	$(holder).empty();
	if (data.length == 0) {
		var h3 = el("h3");
		h3.innerHTML = "No Transactions Yet";
		holder.appendChild(h3);
		return;
	}
	var ignoreColumns = ["ended", "payee", "id"];
	var rowCount = 0;
	var countRows = true;
	var section = el("section");
	var table = el("table");
	table.id = "table";
	table.className = "table";
	var thead = el("thead");
	var tr = el("tr");
	if (countRows) {
		var th = el("th");
		th.innerHTML = "#";
		tr.appendChild(th);
	}
	var th = el("th");
	th.innerHTML = "Date";
	tr.appendChild(th);
	var columns = Object.keys(data[0]);
	var departments = window.departments;
	for (var i=0;i<columns.length;i++) {
		if (ignoreColumns.indexOf(columns[i]) > -1) {
			continue;
		}	
		var th = el("th");
		th.className = "head";
		th.innerHTML = columns[i];
		tr.appendChild(th);
	}
	thead.appendChild(tr);
	table.appendChild(thead);
	var tbody = el("tbody");
	$.each(data, function(key, item) {
		var tr = el("tr");
		if (item.type) {
			if (item.type == "PAYOUT") {
				tr.className = "payout";
			}
			else {
				tr.className = "payin";
			}
		}
		if (item.id) {
			tr.setAttribute("data-id", item.id);
		}
		if (countRows) {
			var td = el("th");
			td.innerHTML = ++rowCount;
			tr.appendChild(td);
		}
		var td = el("td");
		td.innerHTML = moment(item.ended*1000).format("YYYY-MM-DD hh:mm:ss a");
		tr.appendChild(td);
		$.each(columns, function(key, column) {
			if (ignoreColumns.indexOf(column) > -1) {
				return;
			}	
			var td = el("td");
			var amount = item[column];
			td.innerHTML = amount;
			tr.appendChild(td);
		});
		tbody.appendChild(tr);
	});
	table.appendChild(tbody);
	section.appendChild(table);
	holder.appendChild(section);
	$(table).DataTable({
		responsive: true,
		dom: 'Bfrtip',
		buttons: [
		    'copy', 'csv', 'excel', 
		    {
		        extend: 'pdfHtml5',
		        orientation: 'landscape',
		        pageSize: 'LEGAL'
		    },
		    'print'
		]
	});
}
function add_takings() {
	$.ajax({
		url:"api/kvs.php?function=DEPARTMENTS",
		dataType: "JSON",
		success: function(data) {
			var date = prompt("Date:");
			var takings = {};
			var total = data.length;
			var count = 0;
			$.each(data, function(key, item) {
				takings[item["ID"]] = prompt("(" + ++count + "/" + total + ") " + item["Name"] + ":", "0.00");
			});
			save_takings(date, takings);
		}
	});
}
function save_takings(date, json) {
	$.ajax({
		url:"api/kvs.php?function=SAVETAKINGS",
		dataType:"JSON",
		method:"POST",
		data:{"date":date, "json":JSON.stringify(json)},
		success:function(data) {
			if (!data.success) {
				alert("error");
				return;
			}
			get_takings();
		}
	});
}
function showTransaction(id) {
	$.ajax({
		url:"api/kvs.php?function=GETTRANSACTION",
		data:{"id":id},
		dataType: "JSON",
		method:"POST",
		success: function(data) {
			if (!data.success) {
				alert("Error retreiving details");
			}
			var holder = $("#product-list")[0];
			$(holder).empty();
			var transTotal = 0;
			var transQuantity = 0;
			$.each(data.products, function(key, item) {
				var total
				var row = el("section", {class:"row"});
				//quantity
				var col = el("section", {class:"col-md-2 col-xs-2 col-sm-2"});
				var label = el("label");
				label.innerHTML = 1;
				//transQuantity += parseInt(item.quantity);
				col.appendChild(label);
				row.appendChild(col);
				//name
				var col = el("section", {class:"col-md-8 col-xs-8 col-sm-8"});
				var label = el("label");
				if (item.id) {
					var a = el("a");
					a.href = "https://www.goldstandardresearch.co.uk/kvs/product.php?id=" + item.id;
					a.innerHTML = item.name;
					label.appendChild(a);
				}
				else {
					label.innerHTML = item.name;
				}
				col.appendChild(label);
				row.appendChild(col);
				//price
				var col = el("section", {class:"col-md-2 col-xs-2 col-sm-2"});
				var label = el("label");
				label.innerHTML = formatMoney(item.price);
				transTotal += parseFloat(item.price)
				col.appendChild(label);
				row.appendChild(col);
				//product total cost
				/*var col = el("section", {class:"col-md-2"});
				var label = el("label");
				transTotal += parseFloat(item.price)*parseInt(item.quantity);
				label.innerHTML = formatMoney((parseFloat(item.price)*parseInt(item.quantity)));
				col.appendChild(label);
				row.appendChild(col);*/
				holder.appendChild(row);
			});
			//$("#transQuantity").html(transQuantity);
			$("#transTotal").html(formatMoney(transTotal));
			$("#transactionProducts").modal("show");
		}
	});
}
function formatMoney(amount) {
	return accounting.formatMoney(amount, "");
}
