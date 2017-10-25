function Suppliers() {
	this.showSupplier = function(id) {
		$.ajax({
			url:"api/kvs.php?function=GETSUPPLIER",
			data:{"id":id},
			success:function(data) {
				if (!data.success) {
					bootbox.alert("There was an error");
					return;
				}
				var supplier = data.supplier;
				window.suppliers.clearSupplierModal();
				$("#supplier-name").val(supplier.name);
				$("#supplier-telephone").val(supplier.telephone);
				$("#supplier-email").val(supplier.email);
				$("#supplier-website").val(supplier.website);
				$("#supplier-comments").val(supplier.comments);
				$("#supplierInfo").attr("data-id", supplier.id).modal("show");
			}
		});
	}
	this.clearSupplierModal = function() {
		$("#supplier-name").val("");
		$("#supplier-telephone").val("");
		$("#supplier-email").val("");
		$("#supplier-website").val("");
		$("#supplier-comments").val("");
	}
	this.createSupplier = function() {
		window.suppliers.clearSupplierModal();
		$("#supplierInfo").attr("data-id", null).modal("show");
	}
	this.deleteSupplier = function() {
		var id = $("#supplierInfo").attr("data-id");
		bootbox.confirm("Are you sure you want to delete this supplier?", function(result) {
			if (!result) {
				return;
			}
			$.ajax({
				url:"api/kvs.php?function=DELETESUPPLIER", 
				data: {id:$("#supplierInfo").attr("data-id")},
				success:function(data) {
					if (!data.success) {
						bootbox.alert("There was an error deleting that supplier");
						return;
					}
					window.suppliers.clearSupplierModal();
					window.suppliers.getSuppliers();
					$("#supplierInfo").attr("data-id", null).modal("hide");
				}
			});
		});
	}
	this.getSuppliers = function() {
		$.ajax({
			url:"api/kvs.php?function=GETALLSUPPLIERS",
			success:function(data) {
				if (!data.success) {
					bootbox.alert("There has been an error");
					return;
				}
				window.suppliers.populate_table({"name":"Name"}, data.suppliers);
			}
		});
	}
	this.init = function() {
		$.ajaxSetup({
			method:"POST",
			dataType:"JSON"
		});
		window.suppliers.getSuppliers();
		$("#update-supplier").on("click", function() {
			window.suppliers.saveSupplier($("#supplierInfo").attr("data-id"));
		});
		$("#add-supplier").on("click", function() {
			window.suppliers.createSupplier();
		});
		$("#delete-supplier").on("click", function() {
			window.suppliers.deleteSupplier();
		});
	}
	this.saveSupplier = function(id) {
		var name = $("#supplier-name").val();
		var telephone = $("#supplier-telephone").val();
		var email = $("#supplier-email").val();
		var website = $("#supplier-website").val();
		var comments = $("#supplier-comments").val();
		if (id == null) {
			$.ajax({
				url:"api/kvs.php?function=ADDSUPPLIER",
				data:{"name":name, "telephone":telephone, "email":email, "website":website, "comments":comments},
				success:function(data) {
					if (!data.success) {
						bootbox.alert("There has been an error");
					}
					$("#supplierInfo").attr("data-id", null).modal("hide");
					window.suppliers.getSuppliers();
				}
			});
			return;
		}	
		$.ajax({
			url:"api/kvs.php?function=UPDATESUPPLIER",
			data:{"id":id, "name":name, "telephone":telephone, "email":email, "website":website, "comments":comments},
			success:function(data) {
				if (!data.success) {
					bootbox.alert("There has been an error");
				}
				$("#supplierInfo").attr("data-id", null).modal("hide");
				window.suppliers.getSuppliers();
			}
		});
	}
	this.populate_table = function(columns, data) {
		var holder = document.getElementById("suppliers-viewport");
		$(holder).empty();
		var section = el("section");
		var table = el("table", {id:"suppliers-table", class:"table", style:"width:100%"});
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
		$("#suppliers-viewport tbody").on("click", "td", function() {
			var id = $(this).parent().attr("data-id");
			if ((id !== null) || (id.length !== 0)) {
				window.suppliers.showSupplier(id);
			}
		});
	}
}
