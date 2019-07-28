use std::net::UdpSocket;
use std::{thread, time};

use config;
use models::Server::Server;
use utils as app;

const MAX_BUFFER_SIZE: usize = 255;
const SEND_ADDR: &str = "0.0.0.0:65444";
const RECIEVE_ADDR: &str = "0.0.0.0:65445";
const BROADCAST_ADDR: &str = "192.168.1.255:65445";

pub fn send(msg: String) {
    let thead_sleep_duration = time::Duration::from_secs(config::BROADCAST_INTERVAL);
    let _now = time::Instant::now();
    thread::spawn(move || loop {
        thread::sleep(thead_sleep_duration);
        if !config::AUTO_BROADCAST {
            continue;
        }
        if !send_udp_advertisement(&msg) {
            break;
        }
    });
}

fn send_udp_advertisement(msg: &String) -> bool {
    let socket = match UdpSocket::bind(SEND_ADDR) {
        Ok(x) => x,
        Err(x) => {
            error!("Could not bind to socket {}", x);
            return false;
        }
    };
    let buf = &mut msg.as_bytes();
    if buf.len() >= MAX_BUFFER_SIZE {
        panic!(
            "UDP message cannot exceed max buffer length {}",
            MAX_BUFFER_SIZE
        );
    }
    match socket.send_to(buf, &BROADCAST_ADDR) {
        Ok(x) => x,
        Err(x) => {
            error!("{}", x);
            return true;
        }
    };
    true
}

pub fn listen() {
    thread::spawn(move || {
        let socket = match UdpSocket::bind(RECIEVE_ADDR) {
            Ok(x) => x,
            Err(x) => {
                warn!("Could not bind to socket {}", x);
                return;
            }
        };
        loop {
            let mut buf = [0x20; MAX_BUFFER_SIZE];
            let (_amt, src) = match socket.recv_from(&mut buf) {
                Ok(x) => x,
                Err(x) => {
                    warn!("{}", x);
                    continue;
                }
            };
            let msg = match String::from_utf8(buf.to_vec()) {
                Ok(x) => x,
                Err(x) => {
                    warn!("Garbage data sent to udp endpoint: {}", x);
                    continue;
                }
            };
            debug!("Got msg {}", &msg.trim_end());
            let mut server: Server = match serde_json::from_str(&msg.trim_end()) {
                Ok(x) => x,
                Err(x) => {
                    warn!("Host data sent to udp endpoint {}", x);
                    continue;
                }
            };
            info!("Host is at {:?}", src);
            server.ip_address = String::from("[Known]");
            let conn = app::establish_connection();
            server.save(&conn);
        }
    });
}
