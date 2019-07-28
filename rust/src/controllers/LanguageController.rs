use rocket_contrib::json::Json;

use models::Database::DatabaseConnection;
use responses::CustomResponse::CustomResponse;
use rocket::http::Status;

use models::Language::Language;

#[get("/")]
pub fn get_all() -> Json<Vec<Language>> {
    let mut languages = Vec::new();
    languages.push(Language::new("English", "en"));
    languages.push(Language::new("Deutsch", "de"));
    languages.push(Language::new("汉语", "ch"));
    languages.push(Language::new("Français", "fr"));
    languages.push(Language::new("Svenska", "sv"));
    Json(languages)
}
