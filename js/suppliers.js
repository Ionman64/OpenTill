function showSupplier(id) {
	$.ajax({
		url:"api/kvs.php?function=GETSUPPLIER",
		data:{"id":id},
		success:function(data) {
			if (!data.success) {
				bootbox.alert("There was an error");
				return;
			}
			var supplier = data.supplier;
			clearSupplierModal();
			$("#supplier-name").val(supplier.name);
			$("#supplier-telephone").val(supplier.telephone);
			$("#supplier-email").val(supplier.email);
			$("#supplier-website").val(supplier.website);
			$("#supplier-comments").val(supplier.comments);
			$("#supplierInfo").attr("data-id", supplier.id).modal("show");
		}
	});
}
function clearSupplierModal() {
	$("#supplier-name").val("");
	$("#supplier-telephone").val("");
	$("#supplier-email").val("");
	$("#supplier-website").val("");
	$("#supplier-comments").val("");
}
function createSupplier() {
	clearSupplierModal();
	$("#supplierInfo").attr("data-id", null).modal("show");
}
function deleteSupplier() {
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
				clearSupplierModal();
				getSuppliers();
				$("#supplierInfo").attr("data-id", null).modal("hide");
			}
		});
	});
}
function getSuppliers() {
	$.ajax({
		url:"api/kvs.php?function=GETALLSUPPLIERS",
		success:function(data) {
			if (!data.success) {
				bootbox.alert("There has been an error");
				return;
			}
			populate_table({"name":"Name"}, data.suppliers);
		}
	});
}
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
	$.each(options, function(key, value) {
		el.setAttribute(key, value);
	});
	return el;
}
$(document).ready(function() {
	$.ajaxSetup({
		method:"POST",
		dataType:"JSON"
	});
	getSuppliers();
	$("#update-supplier").on("click", function() {
		saveSupplier($("#supplierInfo").attr("data-id"));
	});
	$("#add-supplier").on("click", function() {
		createSupplier();
	});
	$("#delete-supplier").on("click", function() {
		deleteSupplier();
	});
});
function saveSupplier(id) {
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
				getSuppliers();
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
			getSuppliers();
		}
	});
}
function populate_table(columns, data) {
	var holder = document.getElementById("viewport");
	$(holder).empty();
	var section = el("section");
	var table = el("table");
	table.id = "table";
	table.className = "table";
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
	$("#viewport tbody").on("click", "td", function() {
		var id = $(this).parent().attr("data-id");
		if ((id !== null) || (id.length !== 0)) {
			showSupplier(id);
		}
	});
}
