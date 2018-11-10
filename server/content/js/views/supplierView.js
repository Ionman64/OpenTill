var SupplierView = {
	view: function(vnode) {
		return m("section", [
			m("jumbotron.jumbotron-fluid", [
				m("iframe", {src:"https://www.google.com/maps/embed?pb=!1m16!1m12!1m3!1d2965.0824050173574!2d-93.63905729999999!3d41.998507000000004!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!2m1!1sWebFilings%2C+University+Boulevard%2C+Ames%2C+IA!5e0!3m2!1sen!2sus!4v1390839289319", style:"width:100%;height:200px"})
			]),
			m("section.container-fluid", [
				m("section.row", [
					m("section.col-lg-2.col-md-2.col-sm-4.d-xs-none", [
						m("section.card.card-default", [
							m("section.card-header", [
								m("label.card-title", "Wrights")
							]),
							m("section.card-body", [
								m("ul.list-group.list-group-flush", [
									m("li.list-group-item", "Telephone: 01782 565200"),
									m("li.list-group-item", "Email: wrights@wrights.com"),
									m("li.list-group-item", "Comments: Where we get pies from"),
								])
							])
						])
					]),
					m("section.col-lg-10.col-md-10.col-sm-8.col-xs-12", [
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