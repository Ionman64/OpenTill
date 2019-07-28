use rocket_contrib::json::Json;

use diesel::prelude::*;
use diesel::{Insertable, Queryable, SqliteConnection};
use rocket::http::Status;

use responses::CustomResponse::CustomResponse;

use chrono::{NaiveDateTime, Utc};
use serde::{Deserialize, Serialize};

use models::Database::DatabaseConnection;

use models::Server::Server;

#[get("/servers")]
pub fn get_all() -> Json<Vec<Server>> {
    Json(Server::get_all())
}
