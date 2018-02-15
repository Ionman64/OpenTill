window.departmentNames = {};
function Takings(){
	this.init = function() {
		window.takings.populate_table(window.dashboard_data.totals);
		$("#takings-export-btn").click(function() {
			$("#takings-date-start").val(moment().format("YYYY-MM-DD"));
			$("#takings-date-end").val(moment().format("YYYY-MM-DD"));
			var holder = document.getElementById("takings-departments-export");
			$(holder).empty();
			$.each(window.dashboard_data.departments, function(id, department) {
				var li = el("li");
				var label = el("label", {"for":"takings-checkbox-" + id});
				var input = el("input", {"id":"takings-checkbox-" + id, type:"checkbox", checked:true, "data-id":id});
				label.appendChild(input);
				label.appendChild(document.createTextNode(department));
				li.appendChild(label);
				holder.appendChild(li);
			});
			$("#takings-export-success").addClass("hidden");
			$("#takings-export-failure").addClass("hidden");
			$("#export-takings").modal("show");
		});
		$("#takings-departments-export").on("change", "input[type=checkbox]", function() {
			if ($("#takings-departments-export input[type=checkbox]:checked").length == 0) {
				$("#takings-export").attr("disabled", true);
			}
			else {
				$("#takings-export").attr("disabled", false);
			}
		});
		$("#takings-export").click(function() {
			var selectedDepartments = [];
			$("#takings-departments-export input[type=checkbox]:checked").each(function() {
				selectedDepartments.push(this.getAttribute("data-id"));
			});
			$.ajax({
				url:"api/kvs.php?function=GENERATETAKINGSREPORT",
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
		/*var date = moment().subtract(9, "days");
		var currentTimestamp = parseInt(moment().format("x"));
		while (currentTimestamp > parseInt(moment(date.format("YYYY-MM-DD")).format("x"))) {
			var col = el("section", {class:"col-md-1 table-box"});
			var label = el("label", {html:date.format("YYYY-MM-DD"), style:"padding-left:0;padding-right:0;"});
			col.appendChild(label);
			$("#dates-header").append(col);
			date.add(1, "days");
		}*/
	}
	this.get_transactions = function(start, end) {
		$.ajax({
			url:"api/kvs.php?function=GETDAYTOTALS",
			data:{"start":start, "end":end},
			dataType: "JSON",
			method:"POST",
			success: function(data) {
				if (!data.success) {
					alert("Error collecting totals");
				}
				var refunds = data["refunds"];
				var cardGiven = data["card"];
				var total = data["takings"];
				var payouts = data["payouts"];
				var cashback = data["cashback"];
				$("#payouts").html(formatMoney(payouts));
				$("#refunds").html(formatMoney(refunds));
				$("#card-payments").html(formatMoney(cardGiven));
				$("#takings").html(formatMoney(total));
				$("#cashback").html(formatMoney(cashback));
				var cashid = total;
				cashid -= payouts;
				cashid -= (cardGiven-cashback);
				cashid -= (cashback*2); //this is taken away twice because it is added as a product
				$("#cash-id").html(formatMoney(cashid));
				$("#takings-modal .modal-title").html(moment(start*1000).format("LLLL"));
				$("#takings-modal").modal("show");
			}
		});
	}
	this.populate_table = function() {
		var holder = document.getElementById("takings-viewport");
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
		$.each(window.dashboard_data.departments, function(id, department) {
			var th = el("th");
			th.className = "head";
			th.innerHTML = department;
			tr.appendChild(th);
		});
		var th = el("th");
		th.className = "head";
		th.innerHTML = "Total";
		tr.appendChild(th);
		thead.appendChild(tr);
		table.appendChild(thead);
		var tbody = el("tbody");
		$.each(window.dashboard_data.takings, function(date, totals) {
			var total = 0.00;
			var tr = el("tr", {"data-start":moment(date).unix(), "data-end":moment(date).add(1, "days").subtract(1, "seconds").unix()});
			var td = el("td");
			td.innerHTML = date;
			tr.appendChild(td);
			$.each(window.dashboard_data.departments, function(id, department) {
				var td = el("td");
				var amount = parseFloat(totals[id] ? totals[id] : "0.00");
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
		$(table).on("click", "tr", function() {
			if (!$(this).attr("data-start") || !$(this).attr("data-end")) {
				return;
			}
			window.takings.get_transactions($(this).attr("data-start"), $(this).attr("data-end"));
		});
		/*$(table).DataTable({
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
		});*/
	}
}
function formatMoney(amount) {
	if (typeof amount != "String") {
		amount = amount.toString();
	}
	return accounting.formatMoney(amount, "");
}
