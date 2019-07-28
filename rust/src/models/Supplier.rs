use rocket_contrib::databases::diesel;

use diesel::prelude::*;
use diesel::{Insertable, Queryable, SqliteConnection};
use utils as app;

use chrono::{NaiveDateTime, Utc};
use schema::*;
use serde::{Deserialize, Serialize};

#[derive(Deserialize)]
pub struct NewSupplier {
    pub name: String,
    pub telephone: String,
    pub website: String,
    pub email: String,
}

#[table_name = "suppliers"]
#[derive(Queryable, Insertable, Serialize, Deserialize)]
pub struct Supplier {
    pub id: String,
    pub name: String,
    pub telephone: String,
    pub website: String,
    pub email: String,
    pub updated: NaiveDateTime,
    pub created: NaiveDateTime,
    pub deleted: bool,
}

impl Supplier {
    pub fn new(name: String, telephone: String, website: String, email: String) -> Supplier {
        Supplier {
            id: app::uuid4().to_string(),
            name: name,
            telephone: telephone,
            website: website,
            email: email,
            updated: Utc::now().naive_utc(),
            created: Utc::now().naive_utc(),
            deleted: false,
        }
    }
    pub fn get_all(conn: &SqliteConnection) -> Option<Vec<Supplier>> {
        match suppliers::table.load(conn) {
            Ok(x) => Some(x),
            Err(x) => {
                error!("{}", x);
                None
            }
        }
    }
    pub fn find_by_id(id: &str, conn: &SqliteConnection) -> Option<Supplier> {
        match suppliers::table.find(id).get_result::<Supplier>(conn) {
            Ok(x) => Some(x),
            Err(diesel::NotFound) => None,
            Err(x) => {
                error!("{}", x);
                None
            }
        }
    }
    pub fn insert(&self, conn: &SqliteConnection) -> Option<usize> {
        match diesel::insert_into(suppliers::table)
            .values(self)
            .execute(conn)
        {
            Ok(x) => Some(x),
            Err(x) => {
                error!("{}", x);
                None
            }
        }
    }
    pub fn delete(supplier_id: &str, conn: &SqliteConnection) -> Option<usize> {
        //Assets are never deleted
        use schema::suppliers::dsl::*;
        match diesel::update(suppliers.filter(id.eq(supplier_id)))
            .set(deleted.eq(true))
            .execute(conn)
        {
            Ok(x) => Some(x),
            Err(x) => {
                error!("{}", x);
                None
            }
        }
    }
}
