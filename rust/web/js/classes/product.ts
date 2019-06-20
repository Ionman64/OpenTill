class GUID {
    private str: string;

    constructor(str?: string) {
        this.str = str || GUID.new();
    }

    toString() {
        return this.str;
    }

    private static new(): string {
        // your favourite guid generation function could go here
        // ex: http://stackoverflow.com/a/8809472/188246
        let d = new Date().getTime();
        if (window.performance && typeof window.performance.now === "function") {
            d += performance.now(); //use high-precision timer if available
        }
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
            let r = (d + Math.random() * 16) % 16 | 0;
            d = Math.floor(d/16);
            return (c=='x' ? r : (r & 0x3 | 0x8)).toString(16);
        });
    }
}

class Cashier {
    _id: GUID
    name: string;
    type: string;
}

class Customer {
    _id: GUID
    name: string;
}

class Offer {
    _id: GUID
    name: string;
    reduction: number;
}

class Discount {
    _id: GUID
    name: string;
    reduction: number;
}

class Reduction {
    amount: number;
    reason: string;
}

class Item {
    _id: GUID
    type: number;
    price: number;
    name: string;
    barcode:string;
    quantity: number;
    offers: Offer[];
    discounts: Discount[];
    reduction?: number;
    constructor(barcode: string) {
        m.request({
            url:"api/product/barcode/" + barcode,
            method:"GET",
            data:{"cashier_id":RegisterView.getCashier()._id}
        }).then(function(data) {
            console.log(data);
        }).catch(function(e) {
            Logger.error(e);
        });
    }
}

class Basket {
    _id: GUID
    cashier: Cashier;
    customer: Customer;
    time?: any;
    offer: Offer[];
    card_given: number;
    cash_given: number;
    discounts: Discount[];
    in_progress: boolean;
    items: Item[];
    constructor(id:GUID, cashier:Cashier) {
        this._id = id;
        this.cashier = cashier;
        Register.setCurrentBasket(this);
        m.request({
            url:"api/basket/new",
            method:"POST",
            data:{"cashier_id":this.cashier._id}
        }).then(function(data) {
            let current_basket: Basket = Register.getCurrentBasket();
            current_basket._id = data.id;
            current_basket.in_progress = true;
        }).catch(function(e) {
            Logger.error(e);
        });
    }
    cancel() {
        m.request({
            url:"api/basket/cancel",
            method:"POST",
            data:{"id":this._id}
        }).then(function(data) {
            let current_basket: Basket = Register.getCurrentBasket();
            current_basket._id = data.id;
            current_basket.in_progress = false;
        }).catch(function(e) {
            Logger.error(e);
        });
    }
    finish() {
        this.in_progress = false;
        m.request({
            url:"api/basket/finish",
            method:"POST",
            data:{"id":JSON.stringify(this)}
        }).then(function(data) {
            let current_basket: Basket = Register.getCurrentBasket();
            current_basket._id = data.id;
            current_basket.in_progress = false;
        }).catch(function(e) {
            Logger.error(e);
        });
    }
    addItem(item: Item) {
        let index = _.findIndex(this.items, {barcode:item.barcode, reduction:null});
        if (index == -1) {
            this.items.push(item);
        }
        else {
            this.items[index].quantity++;
        }
    }
    removeItem(id: GUID) {
        let index = _.findIndex(this.items, {_id:id});
        if (index == -1) {
            Logger.warn("Product does not exist to be deleted");
        }
        else {
            this.items.splice(index, 1);
        }
    }
}