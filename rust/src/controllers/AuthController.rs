use rocket_contrib::json::Json;
use rocket::http::Status;
use models::Database::DatabaseConnection;

use models::User::{User};
use models::LoginAttempt::{CodeAttempt, LoginAttempt};
use rocket::http::{Cookie, Cookies};
use rocket::response::{Redirect, Flash};
use rocket::request::Form;

use config;

use controllers::IndexController;

#[post("/login", format = "application/json", data = "<login_attempt>")]
pub fn login_code(mut cookies: Cookies, conn:DatabaseConnection, login_attempt:Json<CodeAttempt>) -> Result<Json<User>, rocket::response::status::Custom<&'static str>> {
    let code = login_attempt.0.code;
    match User::find_by_code(code, &conn) {
        Some(x) => {
            cookies.add_private(Cookie::new(config::AUTH_COOKIE_NAME, x.id.clone()));
            return Ok(Json(x));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::NotFound, "Could not find user"));
        }
    };
}

#[post("/login", data = "<login_attempt>", rank = 2)]
pub fn login_email(mut cookies: Cookies, conn:DatabaseConnection, login_attempt:Form<LoginAttempt>) -> Result<Json<User>, rocket::response::status::Custom<&'static str>> {
    let username = &login_attempt.email;
    let password = &login_attempt.password;
    match User::find_by_username_and_password(username.to_string(), password.to_string(), &conn) {
        Some(x) => {
            cookies.add_private(Cookie::new(config::AUTH_COOKIE_NAME, x.id.clone()));
            return Ok(Json(x));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::NotFound, "Could not find user"));
        }
    };
}

#[post("/logout")]
pub fn logout(mut cookies: Cookies<'_>) -> Flash<Redirect> {
    cookies.remove_private(Cookie::named(config::AUTH_COOKIE_NAME));
    Flash::success(Redirect::to(uri!(IndexController::login)), "Successfully logged out.")
}