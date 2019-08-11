use rocket_contrib::databases::diesel;

use diesel::prelude::*;
use diesel::{Insertable, Queryable, SqliteConnection, Connection};
use utils as app;

use chrono::{NaiveDateTime, Utc};
use schema::*;
use serde::{Deserialize, Serialize};

use models::Database::DatabaseConnection;

#[derive(Deserialize)]
pub struct NewTransaction {
    pub cashier: String
}


#[table_name = "transactions"]
#[derive(Queryable, Insertable, Serialize, Deserialize)]
pub struct Transaction {
    pub id: String,
    //pub products: Vec<Product>,
    pub total: i32,
    pub cashier: String,
    pub started: NaiveDateTime,
    pub ended: Option<NaiveDateTime>,
    pub money_given: i32,
    pub card: i32,
    pub cashback: i32,
    pub payee: Option<String>,
    pub transaction_type: String
}

impl Transaction {
    pub fn new(conn: &SqliteConnection, cashier: String) -> Result<Transaction, &str> {
        let transaction = Transaction {
            id: app::uuid4().to_string(),
            total:0,
            cashier: cashier,
            started: Utc::now().naive_utc(),
            ended: None,
            money_given: 0,
            card: 0,
            cashback: 0,
            payee: None,
            transaction_type: String::from("PURCHASE")
        };
        match diesel::insert_into(transactions::table).values(&transaction).execute(conn)
        {
            Ok(x) => x,
            Err(x) => {
                error!("{}", x);
                return Err("Cannot create transaction");
            }
        };
        Ok(transaction)
    }
}