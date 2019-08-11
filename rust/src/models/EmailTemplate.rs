use std::path::PathBuf;
use utils as app;
use config as config;

pub struct EmailTemplate {
    pub subject: String,
    pub title: String,
    pub text: String,
    pub html_file: PathBuf,
    pub alt_text: String
}

impl EmailTemplate {
    pub fn new_message() -> EmailTemplate {
        EmailTemplate {
            subject: format!("[{}] New messages", config::APP_NAME),
            title: String::from("You have new messages to check"),
            text: String::from("Some people have been sending you nice messages that you should check"),
            html_file: PathBuf::from(app::get_emails_dir().join("message_recieved.html")),
            alt_text: String::from("You have new messages to check")
        }
    }
}