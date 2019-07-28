use config;
use diesel::{Insertable, Queryable};
use utils as app;

use serde::Deserialize;

#[derive(serde::Serialize, Deserialize)]
pub struct TemplateContent {
    pub logo: String,
    pub app_name: String,
    pub app_version_major: i32,
    pub app_version_minor: i32,
}

impl TemplateContent {
    pub fn new() -> TemplateContent {
        TemplateContent {
            logo: app::logo_ascii(),
            app_name: String::from(config::APP_NAME),
            app_version_major: config::APP_VERSION_MAJOR,
            app_version_minor: config::APP_VERSION_MINOR,
        }
    }
}
