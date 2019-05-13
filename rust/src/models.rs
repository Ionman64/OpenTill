use schema::products;

use diesel::{Insertable, Queryable, Table};
use utils as app;

//#[table_name="products"]
#[derive(Queryable,Insertable)]
pub struct Product {
    pub id: String,
    pub name: String,
    pub barcode: String,
    pub price: i32,
}

impl Product {
    pub fn new(name:String, barcode:String, price:i32) -> Product {
        Product {
            id: app::uuid4().to_string(),
            name: name,
            barcode:barcode,
            price:price
        }
    }
}
