use rocket_contrib::databases::diesel;

use diesel::prelude::*;
use diesel::{Insertable, Queryable, SqliteConnection};
use utils as app;

use chrono::{NaiveDateTime, Utc};
use schema::*;
use serde::{Serialize, Deserialize};

use config;

#[table_name = "store_products"]
#[derive(Queryable, Insertable, Serialize, Deserialize)]
pub struct Product {
    pub id: String,
    pub name: String,
    pub barcode: String,
    pub price: i32,
    pub department: String,
    pub supplier: String,
    #[column_name = "labelPrinted"]
    pub label_printed: bool,
    #[column_name = "isCase"]
    pub is_case: bool,
    pub updated: NaiveDateTime,
    pub created: NaiveDateTime,
    pub deleted: bool,
    pub max_stock: i32,
    pub current_stock: i32,
}

impl Product {
    pub fn new(name: String, barcode: String, price: i32) -> Product {
        Product {
            id: app::uuid4().to_string(),
            name: name,
            barcode: barcode,
            price: price,
            department: String::from(config::NO_DEPARTMENT_GUID),
            supplier: String::from(config::NO_SUPPLIER_GUID),
            label_printed: false,
            is_case: false,
            updated: Utc::now().naive_utc(),
            created: Utc::now().naive_utc(),
            deleted: false,
            max_stock: 0,
            current_stock: 0,
        }
    }
    pub fn default() -> Product {
        Product::new(String::from("Unknown Product"), String::from("01"), 0)
    }
    pub fn get_all(conn: &SqliteConnection) -> Vec<Product> {
        match store_products::table.load(conn) {
            Ok(x) => x,
            Err(x) => {
                error!("{}", x);
                Vec::new()
            }
        }
    }
    pub fn find_by_id(id: &str, conn: &SqliteConnection) -> Option<Product> {
        match store_products::table.find(id).get_result::<Product>(conn) {
            Ok(x) => Some(x),
            Err(diesel::NotFound) => None,
            Err(x) => {
                error!("{}", x);
                None
            }
        }
    }
    pub fn find_by_barcode(code: &str, conn: &SqliteConnection) -> Option<Product> {
        match store_products::table
            .filter(store_products::barcode.eq(code))
            .first::<Product>(conn)
        {
            Ok(x) => Some(x),
            Err(diesel::NotFound) => None,
            Err(x) => {
                println!("{:?}", x);
                error!("{}", x);
                None
            }
        }
    }
    pub fn search(searchString: String, conn: &SqliteConnection) -> Option<Vec<Product>> {
        match store_products::table
            .filter(store_products::name.like(searchString))
            .load::<Product>(conn)
        {
            Ok(x) => Some(x),
            Err(diesel::NotFound) => None,
            Err(x) => {
                println!("{:?}", x);
                error!("{}", x);
                None
            }
        }
    }
    pub fn save(&self, conn: &SqliteConnection) -> bool {
        1 == match diesel::replace_into(store_products::table)
            .values(self)
            .execute(conn)
        {
            Ok(x) => x,
            Err(x) => {
                error!("{}", x);
                0
            }
        }
    }
}
