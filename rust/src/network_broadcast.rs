use std::net::UdpSocket;
use std::{thread, time};

const MAX_BUFFER_SIZE: usize = 255;
const SEND_ADDR: &str = "0.0.0.0:65444";
const RECIEVE_ADDR: &str = "0.0.0.0:65445";
const BROADCAST_ADDR: &str = "192.168.1.255:65445";

pub fn send(msg: String) {
    let thead_sleep_duration = time::Duration::from_secs(60);
    let now = time::Instant::now();
    thread::spawn(move || {
        loop {
            let socket = match UdpSocket::bind(SEND_ADDR) {
                Ok(x) => x,
                Err(x) => {
                    error!("Could not bind to socket {}", x);
                    return;
                }
            };
            let buf = &mut msg.as_bytes();
            if buf.len() >= MAX_BUFFER_SIZE {
                panic!("UDP message cannot exceed max buffer length {}", MAX_BUFFER_SIZE);
            }
            match socket.send_to(buf, &BROADCAST_ADDR) {
                Ok(x) => x,
                Err(x) => {
                    error!("{}", x);
                    break;
                }
            };
            thread::sleep(thead_sleep_duration);
        };
    });
}

pub fn listen() {
    thread::spawn(move || {
        let mut socket = match UdpSocket::bind(RECIEVE_ADDR) {
            Ok(x) => x,
            Err(x) => {
                warn!("Could not bind to socket {}", x);
                return;
            }
        };
        loop {
            let mut buf = [0; MAX_BUFFER_SIZE];
            let (amt, src) = match socket.recv_from(&mut buf) {
                Ok(x) => x,
                Err(x) => {
                    warn!("{}", x);
                    continue;
                }
            };
            let msg = match String::from_utf8(buf.to_vec()) {
                Ok(x) => x,
                Err(x) => {
                    warn!("Host {} sent garbage data to udp endpoint", x);
                    continue;
                }
            };
            debug!("Got msg {}", msg);
        }
    });
}