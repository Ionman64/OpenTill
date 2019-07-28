use schema::*;

use diesel::prelude::*;
use diesel::{Insertable, Queryable, SqliteConnection};

use chrono::{NaiveDateTime, Utc};
use serde::Deserialize;

#[table_name = "configurations"]
#[derive(Deserialize, Debug, Queryable, Insertable)]
pub struct AppConfiguration {
    #[column_name = "config_key"]
    pub key: String,
    #[column_name = "config_value"]
    pub value: String,
}

impl AppConfiguration {
    pub fn new(key: &str, value: &str) -> AppConfiguration {
        AppConfiguration {
            key: String::from(key),
            value: String::from(value),
        }
    }
    pub fn get(key: &str, conn: &SqliteConnection) -> Option<String> {
        match configurations::table
            .find(key)
            .get_result::<AppConfiguration>(conn)
        {
            Ok(x) => Some(x.value),
            Err(diesel::NotFound) => None,
            Err(x) => {
                warn!("{}", x);
                None
            }
        }
    }
    pub fn save(&self, conn: &SqliteConnection) -> usize {
        match diesel::replace_into(configurations::table)
            .values(self)
            .execute(conn)
        {
            Ok(x) => x,
            Err(_x) => 0,
        }
    }
}
