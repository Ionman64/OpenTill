use rocket_contrib::templates::Template;
use models::TemplateContent::TemplateContent;

#[get("/")]
pub fn login() -> Template {
    let context = TemplateContent::new();
    Template::render("login", &context)
}

#[get("/register")]
pub fn index() -> Template {
    let context = TemplateContent::new();
    Template::render("index", &context)
}

#[get("/dashboard")]
pub fn dashboard() -> Template {
    let context = TemplateContent::new();
    Template::render("dashboard", &context)
}
