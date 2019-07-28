use diesel::prelude::*;
use diesel::{Insertable, Queryable, SqliteConnection};
use rocket_contrib::databases::diesel;

use chrono::NaiveDateTime;
use serde::Deserialize;

use schema::*;

#[table_name = "versions"]
#[derive(Deserialize, Debug, Queryable, Insertable)]
pub struct Version {
    pub id: String,
    #[serde(with = "date_serializer")]
    pub release_date: NaiveDateTime,
    pub major: i32,
    pub minor: i32,
}

impl Version {
    pub fn save(&self, conn: &SqliteConnection) -> usize {
        match diesel::replace_into(versions::table)
            .values(self)
            .execute(conn)
        {
            Ok(x) => x,
            Err(_x) => 0,
        }
    }
}

//Taken from https://earvinkayonga.com/posts/deserialize-date-in-rust/
mod date_serializer {
    use chrono::{DateTime, NaiveDateTime, Utc};
    use serde::{de::Error, Deserialize, Deserializer, Serialize, Serializer};

    fn time_to_json(t: NaiveDateTime) -> String {
        DateTime::<Utc>::from_utc(t, Utc).to_rfc3339()
    }

    pub fn serialize<S: Serializer>(
        time: &NaiveDateTime,
        serializer: S,
    ) -> Result<S::Ok, S::Error> {
        time_to_json(*time).serialize(serializer)
    }

    pub fn deserialize<'de, D: Deserializer<'de>>(
        deserializer: D,
    ) -> Result<NaiveDateTime, D::Error> {
        let time: String = Deserialize::deserialize(deserializer)?;
        Ok(NaiveDateTime::parse_from_str(&time, "%Y-%m-%d %H:%M").map_err(D::Error::custom)?)
    }
}
