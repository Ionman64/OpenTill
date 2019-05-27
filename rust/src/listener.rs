use std::net;
use std::env;
use std::thread;

fn receive(socket: &net::UdpSocket, mut buffer: &mut [u8]) -> usize {

    let (number_of_bytes, src_addr) = socket.recv_from(&mut buffer).expect("no data received");

    println!("[Listener] {:?}", number_of_bytes);
    println!("[Listener] {:?}", src_addr);

    number_of_bytes
}

fn send(socket: &net::UdpSocket, receiver: &str, msg: &Vec<u8>) -> usize {

    println!("[Listener] sending data");
    let result = socket.send_to(msg, receiver).expect("failed to send message");

    result
}

fn init_host(host: &str) -> net::UdpSocket {

    println!("[Listener] initializing host");
    let socket = net::UdpSocket::bind(host).expect("failed to bind host socket");

    socket
}

pub fn listen() {
    thread::spawn(move || {
        let host_arg = std::net::Ipv4Addr::LOCALHOST;
        let client_arg = std::net::Ipv4Addr::LOCALHOST;

        // TODO(alex): Currently hangs on listening, there must be a way to set a timeout, simply
        // setting the timeout to true did not work.
        let mut buf: Vec<u8> = Vec::with_capacity(100);
        let socket = init_host("127.0.0.1:8000");
        let message = String::from("hello");
        let msg_bytes = message.into_bytes();

        loop {
            while receive(&socket, &mut buf) != 0 {
                println!("[Listener] boo");
            }
        } 
    });
    
}