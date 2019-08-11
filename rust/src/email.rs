use lettre::{
    ClientSecurity, ClientTlsParameters, EmailAddress, Envelope, 
    SendableEmail, SmtpClient, Transport,
};
use lettre::smtp::authentication::{Credentials, Mechanism};
use lettre::smtp::ConnectionReuseParameters;
use native_tls::{Protocol, TlsConnector};

use lettre_email::{Email, mime::TEXT_PLAIN};
use std::path::{PathBuf};

use utils as app;

use config as config;

use models::EmailTemplate::EmailTemplate;


pub fn prepare_email_body() -> String {
    let email_template = EmailTemplate::new_message();
    let mut email_body = app::read_all_lines(email_template.html_file);
    email_body = email_body.replace("{{AppName}}", config::APP_NAME);
    email_body = email_body.replace("{{title}}", &email_template.title);
    email_body = email_body.replace("{{text}}", &email_template.text);
    email_body
}

pub fn prepare_alt_body() -> String {
    let email_template = EmailTemplate::new_message();
    let mut email_body = email_template.alt_text;
    email_body = email_body.replace("{{AppName}}", config::APP_NAME);
    email_body = email_body.replace("{{title}}", &email_template.title);
    email_body = email_body.replace("{{text}}", &email_template.text);
    email_body
}

pub fn get_subject() -> String {
    let email_template = EmailTemplate::new_message();
    email_template.subject
}

pub fn send_mail() {
    let emailAddressToSendTo: &str = "peterpickerill2016@gmail.com";
    let email = Email::builder()
        .to(emailAddressToSendTo)
        .from("robot@goldstandardresearch.co.uk")
        .subject(get_subject())
        .html(prepare_email_body())
        .alternative(prepare_alt_body(), "")
        .build()
        .unwrap();
    
    //.attachment_from_file(&app::get_app_temp().join("Cheese.lbx"), None, &TEXT_PLAIN).unwrap()

    let mut tls_builder = TlsConnector::builder();
    tls_builder.min_protocol_version(Some(Protocol::Tlsv10));
    let tls_parameters = ClientTlsParameters::new(String::from(config::EMAIL_SMTP_ADDR), tls_builder.build().unwrap());

    let mut mailer = SmtpClient::new(
        (config::EMAIL_SMTP_ADDR, config::EMAIL_SMTP_PORT), ClientSecurity::Wrapper(tls_parameters)
    ).unwrap()
        .authentication_mechanism(Mechanism::Login)
        .credentials(Credentials::new("robot@goldstandardresearch.co.uk".to_string(), "RobotPassword#1".to_string()))
        .connection_reuse(ConnectionReuseParameters::ReuseUnlimited)
        .transport();

    match mailer.send(email.into()) {
        Ok(x) => {
            info!("Email sent to {}", emailAddressToSendTo);
        },
        Err(x) => {
            warn!("Could not send email to {}: {}", emailAddressToSendTo, x);
        }   
    };
    mailer.close();
}
