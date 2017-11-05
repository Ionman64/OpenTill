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
		$("#suppliers-viewport").on("click", ".row", function() {
			var id = $(this).attr("data-id");
			if ((id !== null) || (id.length !== 0)) {
				window.suppliers.showSupplier(id);
			}
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
		if (data.length == 0) {
			var h3 = el("h3");
			h3.innerHTML = "No Suppliers Yet";
			holder.appendChild(h3);
			return;
		}
		$.each(data, function(key, item) {
			var row = el("section", {class:"row selectable", "data-id":item.id});
			//Date
			var col = el("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12"});
			var label = el("label", {html:item.name});
			col.appendChild(label);
			row.appendChild(col);
			holder.appendChild(row);
		});	
	}
}
