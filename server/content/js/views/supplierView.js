var SupplierView = {
	supplier: {},
	oninit: function() {
		m.request({
			url:"api/supplier/" + m.route.param("id"),
			method:"GET"
		}).then(function(data) {
			SupplierView.supplier = data;
		}).catch(function(e) {
			console.log(e);
		});
	},
	view: function(vnode) {
		return m("section", [
			m("section.container-fluid", [
				m("section.row", [
					m("section.col-lg-3.col-md-4.col-sm-6.d-xs-none", [
						m("section.card.card-default", [
							m("section.card-header", [
								m("label.card-title", this.supplier.name)
							]),
							m("section.card-body.no-padding", [
								m("iframe", {src:"https://www.google.com/maps/embed?pb=!1m16!1m12!1m3!1d2965.0824050173574!2d-93.63905729999999!3d41.998507000000004!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!2m1!1sWebFilings%2C+University+Boulevard%2C+Ames%2C+IA!5e0!3m2!1sen!2sus!4v1390839289319", style:"width:100%;height:175px"}),
								m("ul.list-group.list-group-flush", [
									m("li.list-group-item", "Telephone:", [
										m("a[href='tel:" + this.supplier.telephone + "']", {oncreate:m.route.link, onupdate:m.route.link})
									]),
									m("li.list-group-item", "Email:", [
										m("a[href='mailto:" + this.supplier.email + "']", {oncreate:m.route.link, onupdate:m.route.link})
									]),
									m("li.list-group-item", "Comments:", this.supplier.comments),
								])
							])
						])
					]),
					m("section.col-lg-9.col-md-8.col-sm-6.col-xs-12", [
							m("section.card.card-default", [
								m("section.card-header", [
									m("label.card-title", "Orders")
								]),
								m("section.card-body", [
									m("ul.list-group.list-group-flush", [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0].map(function(x,y) {
										return m("li.list-group-item", moment(new Date()).format("YYYY-MM-DD"));
									}))
								])
							])
					])
				])
			])
		]);
	}
}