use diesel::prelude::*;
use diesel::{Insertable, Queryable, SqliteConnection};
use rocket_contrib::databases::diesel;
use utils as app;

use chrono::{NaiveDateTime, Utc};
use serde::{Deserialize, Serialize};

use schema::*;

#[derive(Deserialize)]
pub struct NewDepartment {
    pub name: String,
    pub short_hand: String,
    pub comments: Option<String>,
    pub colour: String,
}

#[derive(Deserialize)]
pub struct UpdateDepartment {
    pub id: String,
    pub name: String,
    pub short_hand: String,
    pub comments: Option<String>,
    pub colour: String,
}

#[table_name = "departments"]
#[derive(Queryable, Insertable, Serialize)]
pub struct Department {
    pub id: String,
    pub name: String,
    pub short_hand: String,
    pub comments: Option<String>,
    pub colour: String,
    pub order_num: i32,
    pub updated: NaiveDateTime,
    pub created: NaiveDateTime,
    pub deleted: bool,
}

impl Department {
    pub fn new(name: String, short_hand: String, colour: String) -> Department {
        Department {
            id: app::uuid4().to_string(),
            name: name,
            short_hand: short_hand,
            comments: None,
            colour: colour,
            order_num: std::i32::MAX,
            updated: Utc::now().naive_utc(),
            created: Utc::now().naive_utc(),
            deleted: false,
        }
    }
    pub fn get_all(conn: &SqliteConnection) -> Option<Vec<Department>> {
        match departments::table.load(conn) {
            Ok(x) => Some(x),
            Err(x) => {
                error!("{}", x);
                None
            }
        }
    }
    pub fn find_by_id(id: &str, conn: &SqliteConnection) -> Option<Department> {
        match departments::table.find(id).get_result::<Department>(conn) {
            Ok(x) => Some(x),
            Err(diesel::NotFound) => None,
            Err(x) => {
                error!("{}", x);
                None
            }
        }
    }
    pub fn insert(&self, conn: &SqliteConnection) -> Option<usize> {
        match diesel::insert_into(departments::table)
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
    pub fn update(&self, conn: &SqliteConnection) -> bool {
        1 == match diesel::replace_into(departments::table)
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
    pub fn delete(department_id: &str, conn: &SqliteConnection) -> Option<usize> {
        //Assets are never deleted
        use schema::departments::dsl::*;
        match diesel::update(departments.filter(id.eq(department_id)))
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
