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
		$("#operators-viewport").on("click", ".row", function() {
			var id = $(this).attr("data-id");
			if ((id !== null) || (id.length !== 0)) {
				window.operators.showOperator(id);
			}
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
		$.each(data, function(key, item) {
			var row = el("section", {class:"row selectable", "data-id":item.id});
			//Name
			var col = el("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12"});
			var label = el("label", {html:item.name});
			col.appendChild(label);
			row.appendChild(col);
			holder.appendChild(row);
		});	
	}
}
