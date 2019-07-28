use diesel::{Insertable, Queryable, SqliteConnection};

use chrono::{NaiveDateTime, Utc};
use serde::{Deserialize, Serialize};

use rocket_contrib::templates::Template;

use models::TemplateContent::TemplateContent;

#[get("/")]
pub fn index() -> Template {
    let context = TemplateContent::new();
    Template::render("index2", &context)
}
#[get("/dashboard")]
pub fn dashboard() -> Template {
    let context = TemplateContent::new();
    Template::render("dashboard", &context)
}
