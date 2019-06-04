var SuppliersView = {
	suppliers:[],
	oninit:function() {
		m.request({
			url:"api/supplier",
			method:"GET"
		}).then(function(data) {
			SuppliersView.suppliers = data;
		}).catch(function(e) {
			alert(e.message);
		});
	},
	view: function(vnode) {
		return m("section.container-fluid", [
			m("section.row", [
				m("section.col-lg-2.col-md-2.col-sm-4.d-xs-none", [
					m("section.card.fill", {style:"height:80vh;margin-top:1em"}, [
						m("section.card-body", [
							m("label", "Filter")
						])
					])
				]),
				m("section.col-lg-10.col-md-10.col-sm-8.col-xs-12", [
					m("section.container-fluid", [
						m("section.row", this.suppliers.map(function(supplier) {
							return m("section[href='/supplier/" + supplier.id + "'].col-lg-3.col-md-4.col-sm-6.col-xs-12", {oncreate:m.route.link, onupdate:m.route.link}, [
								m("section.card.rounded-0", {style:"margin-top:1em;"}, [
									m("h5.card-title", supplier.name),
									m("p.card-text", moment(supplier.updated*1000).fromNow()),
								])
							])
						}))
						])
					])
				])
			]);
	}
}