use std::net;
use std::net::Ipv4Addr;
use std::env;
use std::io;
use std::thread;

fn listen(socket: &net::UdpSocket) -> Vec<u8> {

    /// TODO(alex): Create constants for these buffer size values.
    let mut buf: [u8; 20] = [0; 20];
    let number_of_bytes: usize = 0;
    let mut result: Vec<u8> = Vec::new();
    match socket.recv_from(&mut buf) {
        Ok((number_of_bytes, src_addr)) => {
            println!("[Sender] received message: {:?}", buf);
            println!("[Sender] Origin Address {}", src_addr);
            result = Vec::from(&buf[0..number_of_bytes]);
        }
        Err(fail) => println!("[Sender] failed listening {:?}", fail)
    }

    let display_result = result.clone();
    let result_str = String::from_utf8(display_result).unwrap();
    println!("[Sender] received message: {:?}", result_str);
    result
}

fn transmit(socket: &net::UdpSocket, receiver: &str, msg: &Vec<u8>) -> usize {
    match socket.send_to(&msg, receiver) {
        Ok(number_of_bytes) => number_of_bytes,
        Err(fail) => {
            warn!("{}", fail);
            0
        },
    }
}

fn init_host(host: &str) -> net::UdpSocket {
    let socket = net::UdpSocket::bind(host).expect("failed to bind host socket");
    /// TODO(alex): Create a constant for this duration timeout value.
    let duration = std::time::Duration::new(1, 0);
    let dur = std::option::Option::Some(duration);
    let _res = socket.set_read_timeout(dur).expect("failed to set timeout");
    socket
}

#[derive(Debug, Default)]
struct HostConfig {
    local_ip: String,
    local_port: String,
    local_host: String,
    remote_ip: String,
    remote_port: String,
    remote_host: String,
}

#[derive(Debug)]
enum CommandInput {
    local_ip(String),
    local_port(String),
    remote_ip(String),
    remote_port(String),
    start_host,
    connect_remote,
    message(String),
    unknown(String),
    error(String),
}

fn identify_comand(command: &str, data: &str) -> CommandInput {

    match command {
        "-local" => CommandInput::local_ip(data.to_owned()),
        "-lport" => CommandInput::local_port(data.to_owned()),
        "-remote" => CommandInput::remote_ip(data.to_owned()),
        "-rport" => CommandInput::remote_port(data.to_owned()),
        "-lstart" => CommandInput::start_host,
        "-rconnect" => CommandInput::connect_remote,
        "-msg" => CommandInput::message(data.to_owned()),
        _ => CommandInput::unknown(data.to_owned()),
    }
}

fn read_console() -> CommandInput {

    /// TODO(alex): Create a constant for default string capacity values.
    let mut input = String::with_capacity(25);
    match io::stdin().read_line(&mut input) {
        Ok(bytes_read) => {
            println!("[Sender] read: {}", input);
            let mut split_input = input.split_whitespace();
            let cmd = split_input.next().unwrap();
            let data = split_input.collect::<String>();
            println!("[Sender] cmd: {} ------ data: {}", cmd, data);
            identify_comand(&cmd, &data)
        }
        Err(fail) => {
            println!("[Sender] Failed to read console: {}", fail);
            let invalid_data = "failed to read console".to_owned();
            CommandInput::error(invalid_data)
        }
    }
}

fn set_host_parameters(ip: &str, port: &str) -> String {

    /// TODO(alex): Create a constant for default string capacity values.
    let mut host = String::with_capacity(128);
    host.push_str(ip);
    host.push_str(":");
    host.push_str(port);

    host
}

fn build_config(cmd_input: CommandInput, host_config: &mut HostConfig) {

    println!("[Sender] build: {:?}", cmd_input);
    match cmd_input {
        CommandInput::local_ip(ip) => {
            host_config.local_ip = ip;
            host_config.local_host = set_host_parameters(&host_config.local_ip,
                                                         &host_config.local_port);
        }
        CommandInput::local_port(port) => {
            host_config.local_port = port;
            host_config.local_host = set_host_parameters(&host_config.local_ip,
                                                         &host_config.local_port);
        }
        CommandInput::remote_ip(ip) => {
            host_config.remote_ip = ip;
            host_config.remote_host = set_host_parameters(&host_config.remote_ip,
                                                          &host_config.remote_port);
        }
        CommandInput::remote_port(port) => {
            host_config.remote_port = port;
            host_config.remote_host = set_host_parameters(&host_config.remote_ip,
                                                          &host_config.remote_port);
        }
        _ => println!("[Sender] Not a configuration."),
    }

}

pub fn send() {
    thread::spawn(move || {
        /// TODO(alex): Move these calls into command based sections.
        // let message = String::from("hello");
        // let msg_bytes = message.into_bytes();
        // println!("[Sender] sending message: {:?}", msg_bytes);

        let addr = Ipv4Addr::BROADCAST;

        if !addr.is_broadcast() {
            println!("[Sender] Address is not broadcast");
        }
        if !addr.is_multicast() {
            println!("[Sender] Address is not multicast");
        }

        let mut host_config = HostConfig {
            local_ip: "127.0.0.1".to_owned(),
            local_port: "8000".to_owned(),
            /// TODO(alex): Create a constant for default string capacity values.
            local_host: String::with_capacity(128),
            remote_ip: "255.255.255.255".to_owned(),
            remote_port: "8000".to_owned(),
            /// TODO(alex): Create a constant for default string capacity values.
            remote_host: String::with_capacity(128),
        };
        let default_msg = "hello world";

        host_config.local_host = set_host_parameters(&host_config.local_ip, &host_config.local_port);
        host_config.remote_host = set_host_parameters(&host_config.remote_ip, &host_config.remote_port);

        /// TODO(alex): Create a constant for default string capacity values.
        let mut message = String::with_capacity(128);

        loop {
            match read_console() {
                CommandInput::start_host => {
                    println!("[Sender] starting host");
                    break;
                }
                CommandInput::connect_remote => println!("[Sender] connecting to remote host"),
                CommandInput::message(msg) => {
                    message = msg;
                } 
                CommandInput::unknown(unknown_data) => println!("[Sender] unknown_data: {:?}", unknown_data),
                CommandInput::error(fail) => println!("[Sender] error: {:?}", fail),
                input_cmd @ _ => build_config(input_cmd, &mut host_config),
            }
        }

        let mut socket: net::UdpSocket = init_host(&host_config.local_host);
        println!("[Sender] host config: {:?}", host_config);
        println!("[Sender] socket: {:?}", socket);
        let msg_bytes = message.into_bytes();

        /// TODO(alex): Remove this sleep timer.
        let sleep_time = std::time::Duration::from_secs(1);
        std::thread::sleep(sleep_time);

        loop {
            // TODO(alex): Move these calls into command based sections.
            let received_msg = listen(&socket);
            transmit(&socket, &host_config.remote_host, &msg_bytes);
            // send(&socket, &client_arg, &msg_bytes);
        }
    });
}