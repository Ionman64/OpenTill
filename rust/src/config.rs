//Here lives the applications constants, some may be replaced later by environment variables

pub enum ProgramMode {
    PRODUCTION,
    DEVELOPMENT,
    TESTING,
}

pub const APP_NAME: &str = "OpenTill";
pub const DEVELOPMENT_MODE: ProgramMode = ProgramMode::TESTING;

pub const APP_HOME: &str = "";
pub const LOG_HOME: &str = "logs";
pub const UPDATES_HOME: &str = "updates";
pub const TEMP_HOME: &str = "temp";
pub const DATA_HOME: &str = "data";
pub const EMAILS_HOME: &str = "emails";
pub const WEB_CONTENT_HOME: &str = "web";
pub const AUTO_DOWNLOAD_UPDATES: bool = false;
pub const USER_CODE_LENGTH: usize = 7;
pub const NOTIFICATIONS_ON: bool = false;
pub const NO_DEPARTMENT_GUID: &str = "566944d0-09b9-499c-9aca-76aacedf2c33";
pub const NO_SUPPLIER_GUID: &str = "9ccd5b50-f524-4428-a1ac-2ba6bd780e9e";
pub const INSTANCE_GUID_KEY: &str = "INSTANCE_GUID";
pub const APP_VERSION_MAJOR: i32 = 2;
pub const APP_VERSION_MINOR: i32 = 3;
pub const AUTO_BROADCAST: bool = false;
pub const BROADCAST_INTERVAL: u64 = 60;
pub const DATABASE_NAME: &str = "database.sqlite3";
pub const DEFAULT_ADMIN_NAME: &str = "Admin";
pub const PRODUCTS_ZIP_PRODUCTS_FILENAME: &str = "products.csv";
pub const EMAIL_SMTP_ADDR: &str = "send.one.com";
pub const EMAIL_SMTP_PORT: u16 = 465;
pub const AUTH_COOKIE_NAME: &str = "auth";
