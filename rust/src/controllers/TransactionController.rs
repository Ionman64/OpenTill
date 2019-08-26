use rocket_contrib::json::Json;

use models::Database::DatabaseConnection;
use models::Transaction::{Transaction, NewTransaction};


#[post("/", format = "application/json", data = "<new_transaction>")]
pub fn new(conn: DatabaseConnection, new_transaction: Json<NewTransaction>) -> Result<String, rocket::response::status::NotFound<&'static str>> {
    match Transaction::new(&conn, new_transaction.0.cashier) {
        Ok(x) => Ok(format!(r#"{{"id":"{}"}}"#, x.id)),
        Err(x) => {
            return Err(rocket::response::status::NotFound("Could not create transaction"));
        }
    }   
}