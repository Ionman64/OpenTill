use rocket_contrib::databases::diesel;

use diesel::prelude::*;
use diesel::{Insertable, Queryable, SqliteConnection};
use utils as app;

use chrono::{NaiveDateTime, Utc};
use schema::*;
use serde::Deserialize;

#[table_name = "cases"]
#[derive(serde::Serialize, Deserialize, Queryable, Insertable)]
pub struct Case {
    pub id: String,
    pub barcode: String,
    pub product_barcode: String,
    pub units: i32,
    pub updated: NaiveDateTime,
    pub created: NaiveDateTime,
    pub deleted: bool,
}

impl Case {
    pub fn new(barcode: String, product_barcode: String, units: i32) -> Case {
        Case {
            id: app::uuid4().to_string(),
            barcode: String::from(barcode),
            product_barcode: String::from(product_barcode),
            units: units,
            updated: Utc::now().naive_utc(),
            created: Utc::now().naive_utc(),
            deleted: false,
        }
    }
    pub fn insert(&self, conn: &SqliteConnection) -> Option<usize> {
        match diesel::insert_into(cases::table).values(self).execute(conn) {
            Ok(x) => Some(x),
            Err(x) => {
                error!("{}", x);
                None
            }
        }
    }
}

#[derive(Deserialize)]
pub struct NewCase {
    pub barcode: String,
    pub product_barcode: String,
    pub units: i32,
}
