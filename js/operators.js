function Operators() {
	this.showOperator = function(id) {
		$.ajax({
			url:"api/kvs.php?function=GETOPERATOR",
			data:{"id":id},
			success:function(data) {
				if (!data.success) {
					bootbox.alert("There was an error");
					return;
				}
				var operator = data.operator;
				window.operators.clearOperatorModal();
				$("#operator-code").val(operator.code);
				$("#operator-name").val(operator.name);
				$("#operator-telephone").val(operator.telephone);
				$("#operator-email").val(operator.email);
				$("#operator-website").val(operator.website);
				$("#operator-comments").val(operator.comments);
				$("#operatorInfo").attr("data-id", operator.id).modal("show");
			}
		});
	}
	this.clearOperatorModal = function() {
		$("#operator-code").val("");
		$("#operator-name").val("");
		$("#operator-telephone").val("");
		$("#operator-email").val("");
		$("#operator-website").val("");
		$("#operator-comments").val("");
	}
	this.createOperator = function() {
		window.operators.clearOperatorModal();
		$("#operatorInfo").attr("data-id", null).modal("show");
	}
	this.deleteOperator = function() {
		var id = $("#operatorInfo").attr("data-id");
		bootbox.confirm("Are you sure you want to delete this operator?", function(result) {
			if (!result) {
				return;
			}
			$.ajax({
				url:"api/kvs.php?function=DELETEOPERATOR", 
				data: {id:$("#operatorInfo").attr("data-id")},
				success:function(data) {
					if (!data.success) {
						bootbox.alert("There was an error deleting that operator");
						return;
					}
					window.operators.clearOperatorModal();
					window.operators.getOperators();
					$("#operatorInfo").attr("data-id", null).modal("hide");
				}
			});
		});
	}
	this.getOperators = function() {
		$.ajax({
			url:"api/kvs.php?function=GETALLOPERATORS",
			success:function(data) {
				if (!data.success) {
					bootbox.alert("There has been an error");
					return;
				}
				window.operators.populate_table({"name":"Name"}, data.operators);
			}
		});
	}
	this.init = function() {
		$.ajaxSetup({
			method:"POST",
			dataType:"JSON"
		});
		window.operators.getOperators();
		$("#update-operator").on("click", function() {
			window.operators.saveOperator($("#operatorInfo").attr("data-id"));
		});
		$("#add-operator").on("click", function() {
			window.operators.createOperator();
		});
		$("#delete-operator").on("click", function() {
			window.operators.deleteOperator();
		});
	};
	this.saveOperator = function(id) {
		var name = $("#operator-name").val();
		var pass = $("#operator-password").val();
		var telephone = $("#operator-telephone").val();
		var email = $("#operator-email").val();
		var website = $("#operator-website").val();
		var comments = $("#operator-comments").val();
		if (id == null) {
			$.ajax({
				url:"api/kvs.php?function=ADDOPERATOR",
				data:{"name":name, "password":pass, "telephone":telephone, "email":email, "website":website, "comments":comments},
				success:function(data) {
					if (!data.success) {
						bootbox.alert("There has been an error");
					}
					$("#operatorInfo").attr("data-id", null).modal("hide");
					window.operators.getOperators();
				}
			});
			return;
		}	
		$.ajax({
			url:"api/kvs.php?function=UPDATEOPERATOR",
			data:{"id":id, "name":name, "password":pass, "telephone":telephone, "email":email, "website":website, "comments":comments},
			success:function(data) {
				if (!data.success) {
					bootbox.alert("There has been an error");
				}
				$("#operatorInfo").attr("data-id", null).modal("hide");
				window.operators.getOperators();
			}
		});
	}
	this.populate_table = function(columns, data) {
		var holder = document.getElementById("operators-viewport");
		$(holder).empty();
		var section = el("section");
		var table = el("table" ,{id:"operators-table", class:"table", style:"width:100%"});
		var thead = el("thead");
		var tr = el("tr");;
		$.each(columns, function(key, value) {
			var th = el("th");
			th.className = "head";
			th.innerHTML = value;
			tr.appendChild(th);
		});
		thead.appendChild(tr);
		table.appendChild(thead);
		var tbody = el("tbody");
		$.each(data, function(key, item) {
			var tr = el("tr");
			tr.setAttribute("data-id", item.id);
			$.each(columns, function(key, value) {
				var td = el("td");
				td.innerHTML = item[key] ? item[key] : "null";
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
		$(".dataTables_filter").addClass("pull-right");
		$("#operators-viewport tbody").on("click", "td", function() {
			var id = $(this).parent().attr("data-id");
			if ((id !== null) || (id.length !== 0)) {
				window.operators.showOperator(id);
			}
		});
	}
}
