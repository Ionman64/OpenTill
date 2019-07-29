use lettre::{
    ClientSecurity, ClientTlsParameters, EmailAddress, Envelope, 
    SendableEmail, SmtpClient, Transport,
};
use lettre::smtp::authentication::{Credentials, Mechanism};
use lettre::smtp::ConnectionReuseParameters;
use native_tls::{Protocol, TlsConnector};

use lettre_email::{Email, mime::TEXT_PLAIN};
use std::path::{Path, PathBuf};

use utils as app;

pub fn send_mail() {
    let email = SendableEmail::new(
        Envelope::new(
            Some(EmailAddress::new("robot@goldstandardresearch.co.uk".to_string()).unwrap()),
            vec![EmailAddress::new("peterpickerill2016@gmail.com".to_string()).unwrap()],
        ).unwrap(),
        "message_id".to_string(),
        "Hello world".to_string().into_bytes(),
    );

    let email = Email::builder()
        // Addresses can be specified by the tuple (email, alias)
        .to(("peterpickerill2016@gmail.com", "Peter Pickerill"))
        // ... or by an address only
        .from("robot@goldstandardresearch.co.uk")
        .subject("Hi, Hello world")
        .html(app::get_emails_dir().join("message_recieved.html"))
        .attachment_from_file(&app::get_app_temp().join("Cheese.lbx"), None, &TEXT_PLAIN)
        .unwrap()
        .build()
        .unwrap();
    

    let mut tls_builder = TlsConnector::builder();
    tls_builder.min_protocol_version(Some(Protocol::Tlsv10));
    let tls_parameters = ClientTlsParameters::new("send.one.com".to_string(), tls_builder.build().unwrap());

    let mut mailer = SmtpClient::new(
        ("send.one.com", 465), ClientSecurity::Wrapper(tls_parameters)
    ).unwrap()
        .authentication_mechanism(Mechanism::Login)
        .credentials(Credentials::new("robot@goldstandardresearch.co.uk".to_string(), "RobotPassword#1".to_string()))
        .connection_reuse(ConnectionReuseParameters::ReuseUnlimited)
        .transport();

    let _result = mailer.send(email.into());
    mailer.close();
    /*
     let email = Email::builder()
        // Addresses can be specified by the tuple (email, alias)
        .to(("peterpickerill2016@gmail.com", "Peter Pickerill"))
        // ... or by an address only
        .from("robot@goldstandardresearch.co.uk")
        .subject("Hi, Hello world")
        .alternative("<h2>Hi, Hello world.</h2>", "Hi, Hello world.");

    let mut tls_builder = TlsConnector::builder();
    tls_builder.min_protocol_version(Some(Protocol::Tlsv10));
    let tls_parameters =
        ClientTlsParameters::new(
            "send.one.com".to_string(),
            tls_builder.build().unwrap()
        );

    let mut mailer = SmtpClient::new("send.one.com", ClientSecurity::Wrapper(tls_parameters)).unwrap()
        // Set the name sent during EHLO/HELO, default is `localhost`
        .hello_name(ClientId::Domain("smtp.one.com".to_string()))
        // Add credentials for authentication
        .credentials()
        // Enable SMTPUTF8 if the server supports it
        .smtp_utf8(true)
        // Configure expected authentication mechanism
        .authentication_mechanism(Mechanism::Login)
        // Enable connection reuse
        .connection_reuse(ConnectionReuseParameters::ReuseUnlimited).transport();

    let result = mailer.send(email.build().unwrap());

    if result.is_ok() {
        println!("Email sent");
    } else {
        println!("Could not send email: {:?}", result);
    }
    mailer.close();*/
}
