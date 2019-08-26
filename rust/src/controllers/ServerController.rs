use rocket_contrib::json::Json;

use models::Server::Server;

#[get("/servers")]
pub fn get_all() -> Json<Vec<Server>> {
    Json(Server::get_all())
}
