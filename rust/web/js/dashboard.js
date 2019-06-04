document.addEventListener('DOMContentLoaded', main);

function main() {
    window.Translate = new Translate();
    Translate.load("translate/languages.json");
    setTimeout(function () {
        closeLoadingScreen();
    }, 1000);
    /*setInterval(function() {
        m.request({
            url:"/api/heartbeat",
            method:"GET"
        }).then(function(data) {

        }).catch(function(e) {
            alert("Lost connection to server");
        });
    }, 5000);*/
    m.mount(document.getElementById("mithril-navbar-container"), NavigationView);
    m.mount(document.getElementById("mithril-notification-container"), UserNotification);
    m.route(document.getElementById("mithril-container"), "/home", {
        "/home": HomeView,
        "/overview": OverviewView,
        "/sales": SalesView,
        "/sales/:year/:month": SalesView,
        "/transactions": TransactionsView,
        "/products": ProductsView,
        "/departments": DepartmentsView,
        "/department/add": NewDepartment,
        "/department/:id": SelectedDepartment,
        "/users": UsersView,
        "/users/:id": SelectedUserView,
        "/suppliers": SuppliersView,
        "/supplier/:id": SupplierView,
        "/cloud": CloudView,
        "/settings": SettingsView,
    });
}

function isZero(m) {
    if (m == 0) {
        return true;
    }
    return false;
}

function isUndefined(m) {
    if (m == undefined) {
        return true;
    }
    return false;
}

function isSameValue(x, y) {
    return x == y;
}

function beep() {
    if (!window.playSound) {
        return false;
    }
    let snd = new Audio("data:audio/wav;base64,//uQRAAAAWMSLwUIYAAsYkXgoQwAEaYLWfkWgAI0wWs/ItAAAGDgYtAgAyN+QWaAAihwMWm4G8QQRDiMcCBcH3Cc+CDv/7xA4Tvh9Rz/y8QADBwMWgQAZG/ILNAARQ4GLTcDeIIIhxGOBAuD7hOfBB3/94gcJ3w+o5/5eIAIAAAVwWgQAVQ2ORaIQwEMAJiDg95G4nQL7mQVWI6GwRcfsZAcsKkJvxgxEjzFUgfHoSQ9Qq7KNwqHwuB13MA4a1q/DmBrHgPcmjiGoh//EwC5nGPEmS4RcfkVKOhJf+WOgoxJclFz3kgn//dBA+ya1GhurNn8zb//9NNutNuhz31f////9vt///z+IdAEAAAK4LQIAKobHItEIYCGAExBwe8jcToF9zIKrEdDYIuP2MgOWFSE34wYiR5iqQPj0JIeoVdlG4VD4XA67mAcNa1fhzA1jwHuTRxDUQ//iYBczjHiTJcIuPyKlHQkv/LHQUYkuSi57yQT//uggfZNajQ3Vmz+Zt//+mm3Wm3Q576v////+32///5/EOgAAADVghQAAAAA//uQZAUAB1WI0PZugAAAAAoQwAAAEk3nRd2qAAAAACiDgAAAAAAABCqEEQRLCgwpBGMlJkIz8jKhGvj4k6jzRnqasNKIeoh5gI7BJaC1A1AoNBjJgbyApVS4IDlZgDU5WUAxEKDNmmALHzZp0Fkz1FMTmGFl1FMEyodIavcCAUHDWrKAIA4aa2oCgILEBupZgHvAhEBcZ6joQBxS76AgccrFlczBvKLC0QI2cBoCFvfTDAo7eoOQInqDPBtvrDEZBNYN5xwNwxQRfw8ZQ5wQVLvO8OYU+mHvFLlDh05Mdg7BT6YrRPpCBznMB2r//xKJjyyOh+cImr2/4doscwD6neZjuZR4AgAABYAAAABy1xcdQtxYBYYZdifkUDgzzXaXn98Z0oi9ILU5mBjFANmRwlVJ3/6jYDAmxaiDG3/6xjQQCCKkRb/6kg/wW+kSJ5//rLobkLSiKmqP/0ikJuDaSaSf/6JiLYLEYnW/+kXg1WRVJL/9EmQ1YZIsv/6Qzwy5qk7/+tEU0nkls3/zIUMPKNX/6yZLf+kFgAfgGyLFAUwY//uQZAUABcd5UiNPVXAAAApAAAAAE0VZQKw9ISAAACgAAAAAVQIygIElVrFkBS+Jhi+EAuu+lKAkYUEIsmEAEoMeDmCETMvfSHTGkF5RWH7kz/ESHWPAq/kcCRhqBtMdokPdM7vil7RG98A2sc7zO6ZvTdM7pmOUAZTnJW+NXxqmd41dqJ6mLTXxrPpnV8avaIf5SvL7pndPvPpndJR9Kuu8fePvuiuhorgWjp7Mf/PRjxcFCPDkW31srioCExivv9lcwKEaHsf/7ow2Fl1T/9RkXgEhYElAoCLFtMArxwivDJJ+bR1HTKJdlEoTELCIqgEwVGSQ+hIm0NbK8WXcTEI0UPoa2NbG4y2K00JEWbZavJXkYaqo9CRHS55FcZTjKEk3NKoCYUnSQ0rWxrZbFKbKIhOKPZe1cJKzZSaQrIyULHDZmV5K4xySsDRKWOruanGtjLJXFEmwaIbDLX0hIPBUQPVFVkQkDoUNfSoDgQGKPekoxeGzA4DUvnn4bxzcZrtJyipKfPNy5w+9lnXwgqsiyHNeSVpemw4bWb9psYeq//uQZBoABQt4yMVxYAIAAAkQoAAAHvYpL5m6AAgAACXDAAAAD59jblTirQe9upFsmZbpMudy7Lz1X1DYsxOOSWpfPqNX2WqktK0DMvuGwlbNj44TleLPQ+Gsfb+GOWOKJoIrWb3cIMeeON6lz2umTqMXV8Mj30yWPpjoSa9ujK8SyeJP5y5mOW1D6hvLepeveEAEDo0mgCRClOEgANv3B9a6fikgUSu/DmAMATrGx7nng5p5iimPNZsfQLYB2sDLIkzRKZOHGAaUyDcpFBSLG9MCQALgAIgQs2YunOszLSAyQYPVC2YdGGeHD2dTdJk1pAHGAWDjnkcLKFymS3RQZTInzySoBwMG0QueC3gMsCEYxUqlrcxK6k1LQQcsmyYeQPdC2YfuGPASCBkcVMQQqpVJshui1tkXQJQV0OXGAZMXSOEEBRirXbVRQW7ugq7IM7rPWSZyDlM3IuNEkxzCOJ0ny2ThNkyRai1b6ev//3dzNGzNb//4uAvHT5sURcZCFcuKLhOFs8mLAAEAt4UWAAIABAAAAAB4qbHo0tIjVkUU//uQZAwABfSFz3ZqQAAAAAngwAAAE1HjMp2qAAAAACZDgAAAD5UkTE1UgZEUExqYynN1qZvqIOREEFmBcJQkwdxiFtw0qEOkGYfRDifBui9MQg4QAHAqWtAWHoCxu1Yf4VfWLPIM2mHDFsbQEVGwyqQoQcwnfHeIkNt9YnkiaS1oizycqJrx4KOQjahZxWbcZgztj2c49nKmkId44S71j0c8eV9yDK6uPRzx5X18eDvjvQ6yKo9ZSS6l//8elePK/Lf//IInrOF/FvDoADYAGBMGb7FtErm5MXMlmPAJQVgWta7Zx2go+8xJ0UiCb8LHHdftWyLJE0QIAIsI+UbXu67dZMjmgDGCGl1H+vpF4NSDckSIkk7Vd+sxEhBQMRU8j/12UIRhzSaUdQ+rQU5kGeFxm+hb1oh6pWWmv3uvmReDl0UnvtapVaIzo1jZbf/pD6ElLqSX+rUmOQNpJFa/r+sa4e/pBlAABoAAAAA3CUgShLdGIxsY7AUABPRrgCABdDuQ5GC7DqPQCgbbJUAoRSUj+NIEig0YfyWUho1VBBBA//uQZB4ABZx5zfMakeAAAAmwAAAAF5F3P0w9GtAAACfAAAAAwLhMDmAYWMgVEG1U0FIGCBgXBXAtfMH10000EEEEEECUBYln03TTTdNBDZopopYvrTTdNa325mImNg3TTPV9q3pmY0xoO6bv3r00y+IDGid/9aaaZTGMuj9mpu9Mpio1dXrr5HERTZSmqU36A3CumzN/9Robv/Xx4v9ijkSRSNLQhAWumap82WRSBUqXStV/YcS+XVLnSS+WLDroqArFkMEsAS+eWmrUzrO0oEmE40RlMZ5+ODIkAyKAGUwZ3mVKmcamcJnMW26MRPgUw6j+LkhyHGVGYjSUUKNpuJUQoOIAyDvEyG8S5yfK6dhZc0Tx1KI/gviKL6qvvFs1+bWtaz58uUNnryq6kt5RzOCkPWlVqVX2a/EEBUdU1KrXLf40GoiiFXK///qpoiDXrOgqDR38JB0bw7SoL+ZB9o1RCkQjQ2CBYZKd/+VJxZRRZlqSkKiws0WFxUyCwsKiMy7hUVFhIaCrNQsKkTIsLivwKKigsj8XYlwt/WKi2N4d//uQRCSAAjURNIHpMZBGYiaQPSYyAAABLAAAAAAAACWAAAAApUF/Mg+0aohSIRobBAsMlO//Kk4soosy1JSFRYWaLC4qZBYWFRGZdwqKiwkNBVmoWFSJkWFxX4FFRQWR+LsS4W/rFRb/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////VEFHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAU291bmRib3kuZGUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMjAwNGh0dHA6Ly93d3cuc291bmRib3kuZGUAAAAAAAAAACU=");
    snd.play();
}

function closeLoadingScreen() {
    document.getElementById("loader").classList.add("d-none");
    document.getElementById("loading-overlay").classList.add("closed");
}

var HomeView = {
    view: function (vnode) {
        return m(".container-fluid", [
            m(".row", [
                m(".col-lg-8.col-md-10.col-sm-12.col-xs-12.mx-auto", [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0].map(function (x, y) {
                    return m(".row", [
                        m(".col-lg-12.col-md-12.col-sm-12.col-xs-12", [
                            m(".card.card-default.rounded-0", { style: "margin-top:1em;" }, [
                                m(".card-body", [
                                    m("label", "System Event")
                                ]),
                                m(".card-footer", [
                                    m("label.card-text", "Peter Pickerill"),
                                    m("label.card-text.float-right", "6 minutes ago")
                                ])
                            ])
                        ]),
                    ]);
                }))
            ])
        ]);
    }
}

var OverviewView = {
    view: function (vnode) {
        return m(".container-fluid", [
            m(".row", [
                m(".col-lg-2.col-md-3.col-sm-6.col-xs-12", [
                    m(".card.fill", [
                        m(".card-body", [
                            m("label", "Totals List")
                        ])
                    ])
                ]),
                m(".col-lg-10.col-md-9.col-sm-6.col-xs-12", [
                    m(".card.fill", [
                        m(".card-body", [
                            m("label", "Graph of Takings")
                        ])
                    ])
                ]),
            ]),
            m(".row", [
                m(".col-lg-4.col-md-4.col-sm-6.col-xs-12", [
                    m(".card.fill", [
                        m(".card-body", [
                            m("label", "Top Items List")
                        ])
                    ])
                ]),
                m(".col-lg-8.col-md-8.col-sm-6.col-xs-12", [
                    m(".card.fill", [
                        m(".card-body", [
                            m("label", "Takings Per Cashier")
                        ])
                    ])
                ]),
            ])
        ]);
    }
}

var SalesView = {
    createCells: function () {
        var year = valueOrDefault(m.route.param('year'), moment().year());
        var month = valueOrDefault(m.route.param('month'), moment().month());
        let tempDate = moment().set("year", year).set("month", month).set("date", 1);

        while (tempDate.day() != 0) {
            tempDate.subtract(1, "days");
        }

        var cells = [];
        var tr = [];
        var i = 0;

        while (i < (5 * 7)) {
            if (i % 7 == 0) {
                cells.push(m("tr", tr));
                tr = [];
            }
            tr.push(m("td.no-padding", [
                m(".calendar-date", tempDate.date())
            ]));
            tempDate.add(1, "days");
            i++;
        }

        if (tr.length > 0) {
            cells.push(m("tr", tr));
        }

        return cells;
    },
    view: function (ctrl) {
        var year = valueOrDefault(m.route.param('year'), moment().year());
        var month = valueOrDefault(m.route.param('month'), moment().month());
        var tempDate = moment().set("year", year).set("month", month).set("date", 1);
        console.log(tempDate);
        return m(".container-fluid", [
            m(".row", [
                m(".col-lg-4.col-md-4.col-sm-4.col-xs-4", [
                    m("button[href='" + tempDate.clone().subtract(2, 'months').format("/[sales]/YYYY/MM") + "'].btn.btn-default.btn-lg", { onupdate: m.route.link, oncreate: m.route.link }, tempDate.clone().subtract(1, 'months').format("MMMM"))
                ]),
                m(".col-lg-4.col-md-4.col-sm-4.col-xs-4.text-center", [
                    m("h3.text-default", tempDate.format("MMMM YYYY"))
                ]),
                m(".col-lg-4.col-md-4.col-sm-4.col-xs-4", [
                    m("button[href='" + tempDate.format("/[sales]/YYYY/MM") + "'].btn.btn-default.btn-lg.float-right", { onupdate: m.route.link, oncreate: m.route.link }, tempDate.clone().add(1, 'months').format("MMMM"))
                ]),
            ]),
            m(".row", [
                m(".col-lg-12.col-md-12.col-sm-12.col-xs-12", [
                    m("table.table", [
                        m("thead", moment.weekdays().map(function (day) {
                            return m("th", day);
                        })),
                        m("tbody", this.createCells())
                    ])
                ])
            ])
        ])
    }
}

var TransactionsView = {
    view: function (vnode) {
        return m(".container-fluid", [
            m(".row", [
                m(".col-lg-12.col-md-12.col-sm-12.col-xs-12", [
                    m(".card.fill", [
                        m(".card-body", [
                            m("label", "Transactions Graph")
                        ])
                    ])
                ]),
            ]),
            m(".row", [
                m(".col-lg-12.col-md-12.col-sm-12.col-xs-12", [
                    m(".card.fill", [
                        m(".card-body", [
                            m("label", "Transactions Table")
                        ])
                    ])
                ]),
            ])
        ]);
    }
}

var ProductsView = {
    view: function (vnode) {
        return m(".container-fluid", [
            m(".row", [
                m(".col-lg-2.col-md-2.col-sm-4.d-xs-none", [
                    m(".card.fill", { style: "height:80vh;margin-top:1em;" }, [
                        m(".card-body", [
                            m("label", "Filter")
                        ])
                    ])
                ]),
                m(".col-lg-10.col-md-10.col-sm-8.col-xs-12", [
                    m(".container-fluid", [
                        m(".row", [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0].map(function (x, y) {
                            return m(".col-lg-3.col-md-4.col-sm-6.col-xs-12", [
                                m(".card.bg-dark.text-white.rounded-0", { style: "margin-top:1em;" }, [
                                    m("img.card-img", { style: "filter: brightness(50%);", src: "https://proxy.duckduckgo.com/iu/?u=https%3A%2F%2Ftse1.mm.bing.net%2Fth%3Fid%3DOIP.B2NJxbmg3p9Jno9Pl_7MNwHaDt%26pid%3D15.1&f=1" }),
                                    m(".card-img-overlay", [
                                        m("h5.card-title", "Product " + y),
                                        m("p.card-text", "Last updated 3 mins ago"),
                                    ])
                                ])
                            ])
                        }))
                    ])
                ])
            ])
        ]);
    }
}

var DepartmentsView = {
    departments: [],
    oninit: function () {
        m.request({
            url: "api/department",
            method: "GET"
        }).then(function (data) {
            DepartmentsView.departments = data;
        }).catch(function (e) {
            Logger.error(e);
        });
    },
    delete_department: function (e) {
        let id = e.target.getAttribute("data-id");
        m.request({
            url: format("api/department/{0}", id),
            method: "DELETE"
        }).then(function (data) {
            if (!data.success) {
                alert("Could not delete department");
            }
        }).catch(function (e) {
            Logger.error(e);
        });
    },
    view: function (vnode) {
        if (isZeroLength(this.departments)) {
            return m(".container-fluid.no-padding", [
                m(".row-fluid", [
                    m(".col-lg-12.col-md-12.col-sm-12.col-xs-12", [
                        m(".card.bg-info.custom-fill-parent.mt-2.text-white", [
                            m(".card-body.p-3", [
                                m("label", Translate.translate("No departments have been added, click here to add some")),
                                m("button[href='/department/add'].btn.btn-outline-outline.pull-right", { oncreate: m.route.link, onupdate: m.route.link }, Translate.translate("New Department"))
                            ])
                        ])
                    ])
                ])
            ]);
        }
        else {
            return m(".container-fluid", [
                m(".row", [
                    m(".col-lg-12.col-md-12.col-sm-12.col-xs-12", [
                        m(".container-fluid", [
                            m(".row", [
                                m("button[href='/department/add'].btn.btn-primary.btn-lg", { oncreate: m.route.link, onupdate: m.route.link }, Translate.translate("New Department"))
                            ]),
                            m(".row", [
                                this.departments.map(function (department, y) {
                                    return m(".col-lg-3.col-md-4.col-sm-6.col-xs-12", [
                                        m("div.card.card-default.mt-2.rounded-0", [
                                            m(".card-body", [
                                                m("h4.card-title", department.name),
                                                m(".custom-color-badge", { style: format("background-color:{0}", department.colour) }),
                                                m("p.card-text", "Last updated 3 mins ago"),
                                                m(format("a[href='/department/{0}'].card-link", department.id), { oncreate: m.route.link, onupdate: m.route.link }, Translate.translate("Details")),
                                                m("button.btn.btn-sm.btn-outline-danger.card-link.pull-right", { "data-id": department.id, onclick: DepartmentsView.delete_department }, Translate.translate("Delete"))
                                            ])
                                        ])
                                    ]);
                                }),
                                m(".col-lg-3.col-md-4.col-sm-6.col-xs-12", [
                                    m("div[href='/department/add'].card.card-default.rounded-0.bg-secondary", { oncreate: m.route.link, onupdate: m.route.link, style: "margin-top:1em;" }, [
                                        m(".card-body", [
                                            m("label", Translate.translate("Click here to add a new department"))
                                        ])
                                    ])
                                ])
                            ])
                        ])
                    ])
                ])
            ]);
        }
    }
}

var UsersView = {
    users: [],
    oninit: function () {
        m.request({
            url: "api/user",
            method: "GET"
        }).then(function (data) {
            usersView.users = data;
        }).catch(function (e) {
            Logger.error(e);
        });
    },
    delete_user: function (e) {
        let id = e.target.getAttribute("data-id");
        m.request({
            url: format("api/user/{0}", id),
            method: "DELETE"
        }).then(function (data) {

        }).catch(function (e) {
            Logger.error(e);
        });
    },
    view: function (vnode) {
        if (isZeroLength(this.users)) {
            return m(".container-fluid.no-padding", [
                m(".row-fluid", [
                    m(".col-lg-12.col-md-12.col-sm-12.col-xs-12", [
                        m(".card.bg-info.custom-fill-parent.mt-2.text-white", [
                            m(".card-body.p-3", [
                                m("label", Translate.translate("No users have been added, click here to add some")),
                                m("button[href='/user/add'].btn.btn-outline-outline.pull-right", { oncreate: m.route.link, onupdate: m.route.link }, Translate.translate("New User"))
                            ])
                        ])
                    ])
                ])
            ]);
        }
        else {
            return m(".container-fluid", [
                m(".row", [
                    m(".col-lg-12.col-md-12.col-sm-12.col-xs-12", [
                        m(".container-fluid", [
                            m(".row", [
                                m("button[href='/user/add'].btn.btn-primary.btn-lg", { oncreate: m.route.link, onupdate: m.route.link }, Translate.translate("New User"))
                            ]),
                            m(".row", [
                                this.users.map(function (user, y) {
                                    return m(".col-lg-3.col-md-4.col-sm-6.col-xs-12", [
                                        m("div.card.card-default.mt-2.rounded-0", [
                                            m(".card-body", [
                                                m("h4.card-title", user.name),
                                                m(".custom-color-badge", { style: format("background-color:{0}", user.colour) }),
                                                m("p.card-text", "Last updated 3 mins ago"),
                                                m(format("a[href='/user/{0}'].card-link", user.id), { oncreate: m.route.link, onupdate: m.route.link }, Translate.translate("Details")),
                                                m(format("label.card-link.text-danger.pull-right", user.id), { "data-id": user.id, onclick: this.delete_user }, Translate.translate("Delete"))
                                            ])
                                        ])
                                    ])
                                }),
                                m(".col-lg-3.col-md-4.col-sm-6.col-xs-12", [
                                    m("div[href='/user/add'].card.card-default.rounded-0.bg-secondary", { oncreate: m.route.link, onupdate: m.route.link, style: "margin-top:1em;" }, [
                                        m(".card-body", [
                                            m("label", Translate.translate("Click here to add a new user"))
                                        ])
                                    ])
                                ])
                            ])
                        ])
                    ])
                ])
            ]);
        }
    }
}

var SelectedUserView = {
    user: {},
    oninit: function () {
        this.fetch();
    },
    fetch: function () {
        m.request({
            url: "/api/users/:id",
            data: { id: m.route.param("id") }
        }).then(function (data) {
            StaffView.staff = data;
        }).catch(function () {
            alert("there has been an error");
        });
    },
    view: function (vnode) {
        return m(".container-fluid", [
            m(".row", [
                m(".col-lg-2.col-md-2.col-sm-4.d-xs-none", [
                    m(".card.fill", { style: "height:80vh;margin-top:1em;" }, [
                        m(".card-body", [
                            m("label", "Filter")
                        ])
                    ])
                ]),
                m(".col-lg-10.col-md-10.col-sm-8.col-xs-12", [
                    m(".container-fluid", [
                        m(".row", this.staff.map(function (x) {
                            return m(".col-lg-3.col-md-4.col-sm-6.col-xs-12", [
                                m("a[href=/users/" + x.id + "]", { onupdate: m.route.link, oncreate: m.route.link }, [
                                    m(".card.bg-dark.rounded-0.text-white", { style: "margin-top:1em;" }, [
                                        m(".card-body", [
                                            m("h5.card-title", x.name),
                                            m("p.card-text", moment(x.updated * 1000).fromNow()),
                                        ])
                                    ])
                                ])
                            ])
                        }))
                    ])
                ])
            ])
        ]);
    }
}

var SuppliersView = {
    suppliers: [],
    oninit: function () {
        m.request({
            url: "api/supplier",
            method: "GET"
        }).then(function (data) {
            SuppliersView.suppliers = data;
        }).catch(function (e) {
            alert(e.message);
        });
    },
    view: function (vnode) {
        return m(".container-fluid", [
            m(".row", [
                m(".col-lg-2.col-md-2.col-sm-4.d-xs-none", [
                    m(".card.fill", { style: "height:80vh;margin-top:1em" }, [
                        m(".card-body", [
                            m("label", "Filter")
                        ])
                    ])
                ]),
                m(".col-lg-10.col-md-10.col-sm-8.col-xs-12", [
                    m(".container-fluid", [
                        m(".row", this.suppliers.map(function (supplier) {
                            return m("section[href='/supplier/" + supplier.id + "'].col-lg-3.col-md-4.col-sm-6.col-xs-12", { oncreate: m.route.link, onupdate: m.route.link }, [
                                m(".card.rounded-0", { style: "margin-top:1em;" }, [
                                    m("h5.card-title", supplier.name),
                                    m("p.card-text", moment(supplier.updated * 1000).fromNow()),
                                ])
                            ])
                        }))
                    ])
                ])
            ])
        ]);
    }
}

var SettingsView = {
    view: function (vnode) {
        return m(".container-fluid", [
            m(".row", [
                m(".col-lg-6.col-md-8.col-sm-10.col-xs-12.mx-auto", [
                    m(".card.card-default.rounded-0", [
                        m(SettingsNavigation),
                        m(".card-body", [
                            m("label", "Settings Page")
                        ])
                    ])
                ])
            ])
        ]);
    }
}

var SettingsNavigation = {
    view: function (vnode) {
        return m(".card-header", [

        ]);
    }
}

var NavigationView = {
    searchQuery: "",
    search: function (e) {
        //m.startComputation()
        console.log(e);
        NavigationView.searchQuery = e.target.value;
    },
    view: function () {
        return m(".container-fluid", [
            m(".row", [
                m("nav.navbar.navbar-dark.bg-dark.navbar-expand-md.navbar-expand-lg.fixed-top", [
                    m("a.navbar-brand[href='/']", "OpenTill v2.0"),
                    m("section#navbarCollapse.collapse.navbar-collapse", [
                        m(".navbar-nav", [
                            m("a[href='/home']", { class: isSameValue(m.route.get(), "/home") ? "nav-item nav-link active" : "nav-item nav-link", onupdate: m.route.link, oncreate: m.route.link }, "Home"),
                            m("a[href='/overview']", { class: isSameValue(m.route.get(), "/overview") ? "nav-item nav-link active" : "nav-item nav-link", onupdate: m.route.link, oncreate: m.route.link }, "Overview"),
                            m("a[href='/sales']", { class: isSameValue(m.route.get(), "/sales") ? "nav-item nav-link active" : "nav-item nav-link", onupdate: m.route.link, oncreate: m.route.link }, "Sales"),
                            m("a[href='/transactions']", { class: isSameValue(m.route.get(), "/transactions") ? "nav-item nav-link active" : "nav-item nav-link", onupdate: m.route.link, oncreate: m.route.link }, "Transactions"),
                            m("a[href='/products']", { class: isSameValue(m.route.get(), "/products") ? "nav-item nav-link active" : "nav-item nav-link", onupdate: m.route.link, oncreate: m.route.link }, "Products"),
                            m("a[href='/departments']", { class: isSameValue(m.route.get(), "/departments") ? "nav-item nav-link active" : "nav-item nav-link", onupdate: m.route.link, oncreate: m.route.link }, "Departments"),
                            m("a[href='/orders']", { class: isSameValue(m.route.get(), "/orders") ? "nav-item nav-link active" : "nav-item nav-link", onupdate: m.route.link, oncreate: m.route.link }, "Orders"),
                            m("a[href='/users']", { class: isSameValue(m.route.get(), "/users") ? "nav-item nav-link active" : "nav-item nav-link", onupdate: m.route.link, oncreate: m.route.link }, "Users"),
                            m("a[href='/suppliers']", { class: isSameValue(m.route.get(), "/suppliers") ? "nav-item nav-link active" : "nav-item nav-link", onupdate: m.route.link, oncreate: m.route.link }, "Suppliers"),
                            m("a[href='/cloud']", { class: isSameValue(m.route.get(), "/cloud") ? "nav-item nav-link active" : "nav-item nav-link", onupdate: m.route.link, oncreate: m.route.link }, "Cloud"),
                            m("a[href='/settings']", { class: isSameValue(m.route.get(), "/settings") ? "nav-item nav-link active" : "nav-item nav-link", onupdate: m.route.link, oncreate: m.route.link }, "Settings"),
                        ]),
                    ]),
                    m("span#user-name.navbar-text.text-info.pull-right", "Unknown User"),
                ]),
            ]),
            m(".row", [
                m("input[type='text'].form-control.navbar-search.bg-dark.text-info.rounded-0", { placeholder: "Search...", style: "margin-top:56px;", value: this.searchQuery, onkeyup: this.search })
            ]),
            !isZero(this.searchQuery.length) ? m.fragment({}, [
                m(".search-overlay.bg-dark.text-white", [
                    m("ul.list-group.list-group-flush", [
                        m("li.list-group-item.bg-dark", [
                            m(".badge.badge-success.badge-pill", "product"),
                            "Hello World"
                        ]),
                        m("li.list-group-item.bg-dark", [
                            m(".badge.badge-primary.badge-pill", "supplier"),
                            "Ben Benson"
                        ]),
                        m("li.list-group-item.bg-dark", [
                            m(".badge.badge-warning.badge-pill", "employee"),
                            "Tony Roberts"
                        ]),
                    ])
                ])
            ]) : null
        ]);
    }
}

var UserNotification = {
    _message: "",
    _timeout: -1,
    _alertType: "alert-default",
    _shown: false,
    _timeoutFunc: undefined,
    show: function (message, timeout, alertType) {
        UserNotification._message = message;
        UserNotification._timeout = timeout;
        UserNotification._alertType = alertType;
        UserNotification._shown = true;
    },
    hide: function () {
        this._shown = false;
        m.redraw();
    },
    _hide: function () {
        if (UserNotification._timeoutFunc) {
            clearTimeout(UserNotification.timeoutFunc);
        }
        if (isSameValue(UserNotification._timeout, 2)) {
            UserNotification._shown = false;
        }
        if (isSameValue(UserNotification._timeout, -1)) {
            return;
        }
        UserNotification._timeoutFunc = setTimeout(function () {
            UserNotification._shown = false;
            m.redraw();
        }, UserNotification._timeout);
    },
    view() {
        if (this._shown) {
            return m("section#userNotification.alert.no-padding.user-notification." + this._alertType, { oncreate: this._hide, onupdate: this._hide, onclick: this.hide });
        }
        return m("section#userNotification.alert.no-padding.user-notification.d-none." + this._alertType, { oncreate: this._hide, onupdate: this._hide, onclick: this.hide });
    }
}

var SupplierView = {
    supplier: {},
    oninit: function () {
        m.request({
            url: "api/supplier/" + m.route.param("id"),
            method: "GET"
        }).then(function (data) {
            SupplierView.supplier = data;
        }).catch(function (e) {
            console.log(e);
        });
    },
    view: function (vnode) {
        return m("section", [
            m(".container-fluid", [
                m(".row", [
                    m(".col-lg-3.col-md-4.col-sm-6.d-xs-none", [
                        m(".card.card-default", [
                            m(".card-header", [
                                m("label.card-title", this.supplier.name)
                            ]),
                            m(".card-body.no-padding", [
                                m("iframe", { src: "https://www.google.com/maps/embed?pb=!1m16!1m12!1m3!1d2965.0824050173574!2d-93.63905729999999!3d41.998507000000004!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!2m1!1sWebFilings%2C+University+Boulevard%2C+Ames%2C+IA!5e0!3m2!1sen!2sus!4v1390839289319", style: "width:100%;height:175px" }),
                                m("ul.list-group.list-group-flush", [
                                    m("li.list-group-item", "Telephone:", [
                                        m("a[href='tel:" + this.supplier.telephone + "']", { oncreate: m.route.link, onupdate: m.route.link })
                                    ]),
                                    m("li.list-group-item", "Email:", [
                                        m("a[href='mailto:" + this.supplier.email + "']", { oncreate: m.route.link, onupdate: m.route.link })
                                    ]),
                                    m("li.list-group-item", "Comments:", this.supplier.comments),
                                ])
                            ])
                        ])
                    ]),
                    m(".col-lg-9.col-md-8.col-sm-6.col-xs-12", [
                        m(".card.card-default", [
                            m(".card-header", [
                                m("label.card-title", "Orders")
                            ]),
                            m(".card-body", [
                                m("ul.list-group.list-group-flush", [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0].map(function (x, y) {
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

var CloudView = {
    status: {
        "cloud": {
            "connected": true,
        },
        "nodes": [
            { "name": "Cashier 1", "ip_address": "192.168.1.1" }
        ]
    },
    fetch: function () {
        m.request({
            url: "api/cloud",
            method: "GET",
        }).then(function (data) {
            CloudView.status = data;
        }).catch(function (e) {
            Logger.error(e);
        })
    },
    view: function () {
        return m(".container-fluid", [
            m(".row", [
                m("col-lg-12.col-md-12.col-sm-12.col-xs-12.text-center", [
                    m(".fas.fa-cloud.fa-5x"),
                    this.status.cloud.connected ? m("p.text-success", Translate.translate("Cloud Connected")) : m("p.text-danger", Translate.translate("Cloud Disconnected"))
                ])
            ]),
            m(".row",
                //add a card deck here with the nodes
                this.status.nodes.map(function (node) {
                    return m("p", "node");
                })
            )
        ]);
    }
}

var NewDepartment = {
    save: function (e) {
        e.preventDefault();
        e.stopPropagation();
        m.request({
            url: "api/department",
            method: "POST",
            data: getFormData(this),
        }).then(function (data) {
            console.log(data);
            m.route.set("/department/" + data.id);
        }).catch(function (e) {
            console.log(e);
        });
        return false;
    },
    view: function (vnode) {
        return m("section", [
            m(".container-fluid", [
                m("form.form", { onsubmit: this.save }, [
                    m(".row", [
                        m("h3.text-info", "Add new department")
                    ]),
                    m(".row", [
                        m(".col-lg-6.col-md-8.col-sm-10.col-xs-12", [
                            m("label", "Department Name"),
                            m("input[name='name'][type='text'].form-control.form-control-lg")
                        ])
                    ]),
                    m("row", [
                        m(".col-lg-6.col-md-8.col-sm-10.col-xs-12", [
                            m("label", "Department shortHand"),
                            m("input[name='short_hand'][type='text'].form-control.form-control-lg")
                        ])
                    ]),
                    m(".row", [
                        m(".col-lg-6.col-md-8.col-sm-10.col-xs-12", [
                            m("label", "Colour as Hex"),
                            m("input[name='colour'][type='color'].form-control.form-control-lg")
                        ])
                    ]),
                    m(".row", [
                        m(".col-lg-6.col-md-8.col-sm-10.col-xs-12", [
                            m("label", "Comments"),
                            m("textarea[name='comments'].form-control.form-control-lg")
                        ])
                    ]),
                    m(".row", [
                        m("button.btn.btn-success.btn-sm", "Create New")
                    ]),
                ])
            ])
        ]);
    }
}

var SelectedDepartment = {
    department: {},
    oninit: function (e) {
        SelectedDepartment.department = {};
        m.request({
            url: format("api/department/{0}", m.route.param("id")),
            method: "GET",
        }).then(function (data) {
            SelectedDepartment.department = data;
        }).catch(function (e) {
            console.log(e);
        });
    },
    view: function (vnode) {
        return m("section", [
            m(".container-fluid", [
                m(".row", [
                    m("h3.text-info", this.department.name)
                ]),
                m(".row", [
                    m(".col-lg-6.col-md-8.col-sm-10.col-xs-12", [
                        m("label", "Department Name"),
                        m("input[name='name'][type='text'].form-control.form-control-lg", { value: this.department.name })
                    ])
                ]),
                m("row", [
                    m(".col-lg-6.col-md-8.col-sm-10.col-xs-12", [
                        m("label", "Department shortHand"),
                        m("input[name='short_hand'][type='text'].form-control.form-control-lg", { value: this.department.short_hand })
                    ])
                ]),
                m(".row", [
                    m(".col-lg-6.col-md-8.col-sm-10.col-xs-12", [
                        m("label", "Colour as Hex"),
                        m("input[name='colour'][type='color'].form-control.form-control-lg", { value: this.department.colour })
                    ])
                ]),
                m(".row", [
                    m(".col-lg-6.col-md-8.col-sm-10.col-xs-12", [
                        m("label", "Comments"),
                        m("textarea[name='comments'].form-control.form-control-lg", { value: this.department.comments })
                    ])
                ]),
                m(".row", [
                    m("button.btn.btn-success.btn-sm", { disabled: "disabled" }, "Save Changes")
                ])
            ])
        ]);
    }
}