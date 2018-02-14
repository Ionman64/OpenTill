function Suppliers() {
	this.suppliers = {};
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
	this.init = function() {
		$.ajaxSetup({
			method:"POST",
			dataType:"JSON"
		});
		this.populate_table();
		$("#update-supplier").on("click", function() {
			window.suppliers.saveSupplier($("#supplierInfo").attr("data-id"));
		});
		$("#add-supplier-btn").on("click", function() {
			$("#create-supplier").modal("show");
		});
		$("#create-supplier-button").on("click", function() {
			
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
		if (id == null) {
			var name = $("#new-supplier-name").val();
			var telephone = $("#new-supplier-telephone").val();
			var email = $("#new-supplier-email").val();
			var website = $("#new-supplier-website").val();
			var comments = $("#new-supplier-comments").val();
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
		var name = $("#supplier-name").val();
		var telephone = $("#supplier-telephone").val();
		var email = $("#supplier-email").val();
		var website = $("#supplier-website").val();
		var comments = $("#supplier-comments").val();
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
	this.populate_table = function() {
		var holder = document.getElementById("suppliers-viewport");
		$(holder).empty();
		if (Object.keys(window.dashboard_data.suppliers).length == 0) {
			var h3 = el("h3");
			h3.innerHTML = "No Suppliers Yet";
			holder.appendChild(h3);
			return;
		}
		$.each(window.dashboard_data.suppliers, function(key, item) {
			var row = el("section", {class:"row selectable", "data-id":key});
			//Date
			var col = el("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12"});
			var label = el("h4", {html:item});
			col.appendChild(label);
			row.appendChild(col);
			holder.appendChild(row);
		});	
	}
}
