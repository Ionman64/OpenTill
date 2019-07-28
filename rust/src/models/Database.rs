use diesel::SqliteConnection;

#[database("my_db")]
pub struct DatabaseConnection(SqliteConnection);
