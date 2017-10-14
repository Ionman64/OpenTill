window.departments = [];
$(document).ready(function() {
	$.ajax({
		url:"api/kvs.php?function=DEPARTMENTS",
		dataType: "JSON",
		success: function(data) {
			//window.departments.push({id:0, shorthand:"None", name:"--No Catagory--"});
			$.each(data, function(key, item) {
				window.departments.push(item);
			});
			get_takings();
		}
	});
	$("#viewport").on("click", "tr", function() {
		if (!$(this).attr("data-start") || !$(this).attr("data-end")) {
			return;
		}
		get_transactions($(this).attr("data-start"), $(this).attr("data-end"));
	});
});
function get_transactions(start, end) {
	$.ajax({
		url:"api/kvs.php?function=GETTRANSACTIONS",
		data:{"start":start, "end":end},
		dataType: "JSON",
		method:"POST",
		success: function(data) {
			if (!data.success) {
				alert("Error collecting totals");
			}
			var refunds = 0.00;
			var cardGiven = 0.00;
			var totalTransactions = 0.00;
			var totalPayouts = 0.00;
			var payoutsGiven = 0.00;
			var total = 0.00;
			var payouts = 0.00;
			var cashback = 0.00;
			$.each(data.transactions, function(key, item) {
				switch (item.type) {
					case "PURCHASE":
						totalTransactions++;
						cardGiven += parseFloat(item.card);
						total += parseFloat(item.total);
						cashback += parseFloat(item.cashback);
						break;
					case "REFUND":
						refunds += parseFloat(item.total);
						total -= parseFloat(item.total); //Take away refunds from takings
						break;
					case "PAYOUT":
						payouts++;
						payoutsGiven += parseFloat(item.total);
						break;
				}
			});
			$("#payouts").html(formatMoney(payoutsGiven));
			$("#refunds").html(formatMoney(refunds));
			$("#card-payments").html(formatMoney(cardGiven));
			$("#takings").html(formatMoney(total));
			$("#cashback").html(formatMoney(cashback));
			var cashid = total;
			cashid -= payoutsGiven;
			cashid -= (cardGiven-cashback);
			cashid -= (cashback*2); //this is taken away twice because it is added as a product
			$("#cash-id").html(formatMoney(cashid));
			$("#takings-modal .modal-title").html(moment(start*1000).format("LLLL"));
			$("#takings-modal").modal("show");
		}
	});
}
function get_takings() {
	window.totals = [];
	window.semaphore = 0;
	var date = moment().subtract(1, "months");
	var currentTimestamp = parseInt(moment().format("x"));
	while (currentTimestamp > parseInt(moment(date.format("YYYY-MM-DD")).format("x"))) {
		var start = moment(date.format("YYYY-MM-DD")).format("x")/1000;
		var end = moment(date.add(1, "days").format("YYYY-MM-DD")).format("x")/1000;
		$.ajax({
			url:"api/kvs.php?function=TOTALS",
			data:{"start":start, "end":end, "type":"PURCHASE"},
			dataType: "JSON",
			method:"POST",
			beforeSend: function() {
				window.semaphore++;
			},
			success: function(data) {
				if (!data.success) {
					alert("Error collecting totals");
				}
				window.totals.push(data);
				populate_table(window.totals);
			},
			complete:function() {
				window.semaphore--;
			}
		});
	}
}
function el(el_name) {
	return document.createElement(el_name);
}
function populate_table(data) {
	if (window.semaphore > 1) {
		return;
	}
	var holder = document.getElementById("viewport");
	$(holder).empty();
	var section = el("section");
	var table = el("table");
	table.id = "table";
	table.className = "table";
	var thead = el("thead");
	var tr = el("tr");
	var th = el("th");
	th.innerHTML = "Date";
	tr.appendChild(th);
	var departments = window.departments;
	for (var i=0;i<departments.length;i++) {
		var th = el("th");
		th.className = "head";
		th.innerHTML = departments[i].shorthand;
		tr.appendChild(th);
	}
	var th = el("th");
	th.className = "head";
	th.innerHTML = "Total";
	tr.appendChild(th);
	thead.appendChild(tr);
	table.appendChild(thead);
	var tbody = el("tbody");
	$.each(data, function(key, item) {
		var total = 0.00;
		var tr = el("tr");
		var td = el("td");
		td.innerHTML = moment(item.start*1000).format("YYYY-MM-DD");
		tr.appendChild(td);
		tr.setAttribute("data-start", item.start);
		tr.setAttribute("data-end", item.end);
		$.each(window.departments, function(id, department) {
			var td = el("td");
			var amount = parseFloat(item.totals[department.id] ? item.totals[department.id] : "0.00");
			total = total + amount;
			td.innerHTML = formatMoney(amount);
			tr.appendChild(td);
		});
		var td = el("td");
		td.innerHTML = formatMoney(total);
		tr.appendChild(td);
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
		],
		"order": [[ 0, "desc" ]]
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
function formatMoney(amount) {
	return accounting.formatMoney(amount, "");
}

