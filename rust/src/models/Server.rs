use config;
use diesel::prelude::*;
use diesel::{Insertable, Queryable};
use rocket_contrib::databases::diesel;
use utils as app;

use configuration::AppConfiguration::AppConfiguration;
use serde::Deserialize;

use schema::*;

#[table_name = "servers"]
#[derive(serde::Serialize, Deserialize, Queryable, Insertable)]
pub struct Server {
    pub id: String,
    pub major: i32,
    pub minor: i32,
    pub ip_address: String,
}

impl Server {
    pub fn details(conn: &SqliteConnection) -> Server {
        Server {
            id: AppConfiguration::get(config::INSTANCE_GUID_KEY, conn)
                .expect("Cannot find INSTANCE_GUID_KEY in configuration"),
            major: config::APP_VERSION_MAJOR,
            minor: config::APP_VERSION_MINOR,
            ip_address: String::from("[Unknown]"),
        }
    }
    pub fn get_all() -> Vec<Server> {
        let conn = app::establish_connection();
        match servers::table.load::<Server>(&conn) {
            Ok(x) => x,
            Err(x) => {
                warn!("{}", x);
                Vec::new()
            }
        }
    }
    pub fn save(&self, conn: &SqliteConnection) -> usize {
        match diesel::replace_into(servers::table)
            .values(self)
            .execute(conn)
        {
            Ok(x) => x,
            Err(_x) => 0,
        }
    }
}
