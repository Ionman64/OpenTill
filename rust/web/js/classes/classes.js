var GUID = /** @class */ (function () {
    function GUID(str) {
        this.str = str || GUID["new"]();
    }
    GUID.prototype.toString = function () {
        return this.str;
    };
    GUID["new"] = function () {
        // your favourite guid generation function could go here
        // ex: http://stackoverflow.com/a/8809472/188246
        var d = new Date().getTime();
        if (window.performance && typeof window.performance.now === "function") {
            d += performance.now(); //use high-precision timer if available
        }
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = (d + Math.random() * 16) % 16 | 0;
            d = Math.floor(d / 16);
            return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
        });
    };
    return GUID;
}());
var Cashier = /** @class */ (function () {
    function Cashier() {
    }
    return Cashier;
}());
var Customer = /** @class */ (function () {
    function Customer() {
    }
    return Customer;
}());
var Offer = /** @class */ (function () {
    function Offer() {
    }
    return Offer;
}());
var Discount = /** @class */ (function () {
    function Discount() {
    }
    return Discount;
}());
var Reduction = /** @class */ (function () {
    function Reduction() {
    }
    return Reduction;
}());
var Item = /** @class */ (function () {
    function Item(barcode) {
        m.request({
            url: "api/product/barcode/" + barcode,
            method: "POST",
            data: { "cashier_id": RegisterView.getCashier()._id }
        }).then(function (data) {
            console.log(data);
        })["catch"](function (e) {
            Logger.error(e);
        });
    }
    return Item;
}());
var Basket = /** @class */ (function () {
    function Basket(id, cashier) {
        this._id = id;
        this.cashier = cashier;
        Register.setCurrentBasket(this);
        m.request({
            url: "api/basket/new",
            method: "POST",
            data: { "cashier_id": this.cashier._id }
        }).then(function (data) {
            var current_basket = Register.getCurrentBasket();
            current_basket._id = data.id;
            current_basket.in_progress = true;
        })["catch"](function (e) {
            Logger.error(e);
        });
    }
    Basket.prototype.cancel = function () {
        m.request({
            url: "api/basket/cancel",
            method: "POST",
            data: { "id": this._id }
        }).then(function (data) {
            var current_basket = Register.getCurrentBasket();
            current_basket._id = data.id;
            current_basket.in_progress = false;
        })["catch"](function (e) {
            Logger.error(e);
        });
    };
    Basket.prototype.finish = function () {
        this.in_progress = false;
        m.request({
            url: "api/basket/finish",
            method: "POST",
            data: { "id": JSON.stringify(this) }
        }).then(function (data) {
            var current_basket = Register.getCurrentBasket();
            current_basket._id = data.id;
            current_basket.in_progress = false;
        })["catch"](function (e) {
            Logger.error(e);
        });
    };
    Basket.prototype.addItem = function (item) {
        var index = _.findIndex(this.items, { barcode: item.barcode, reduction: null });
        if (index == -1) {
            this.items.push(item);
        }
        else {
            this.items[index].quantity++;
        }
    };
    Basket.prototype.removeItem = function (id) {
        var index = _.findIndex(this.items, { _id: id });
        if (index == -1) {
            Logger.warn("Product does not exist to be deleted");
        }
        else {
            this.items.splice(index, 1);
        }
    };
    return Basket;
}());
