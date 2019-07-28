use diesel::prelude::*;
use diesel::{Insertable, Queryable, SqliteConnection};
use rocket_contrib::databases::diesel;

use chrono::NaiveDateTime;
use schema::*;
use serde::{Deserialize, Serialize};

#[table_name = "global_products"]
#[derive(Debug, Queryable, Insertable, Serialize, Deserialize)]
pub struct GlobalProduct {
    pub id: String,
    pub name: String,
    pub barcode: String,
    pub updated: NaiveDateTime,
}

impl GlobalProduct {
    pub fn insert(&self, conn: &SqliteConnection) -> bool {
        1 == match diesel::replace_into(global_products::table)
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
    pub fn insert_many(conn: &SqliteConnection, global_products: &Vec<GlobalProduct>) -> bool {
        0 < match diesel::replace_into(global_products::table)
            .values(global_products)
            .execute(conn)
        {
            Ok(x) => x,
            Err(x) => {
                error!("{}", x);
                0
            }
        }
    }
    pub fn find_by_barcode(conn: &SqliteConnection, barcode: String) -> Option<GlobalProduct> {
        match global_products::table
            .filter(global_products::barcode.eq(barcode))
            .first::<GlobalProduct>(conn)
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
}
